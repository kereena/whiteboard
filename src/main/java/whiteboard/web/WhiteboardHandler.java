package whiteboard.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.io.IOException;
import java.util.logging.Logger;

public class WhiteboardHandler extends BaseWebSocketHandler {

    private Logger LOG = Logger.getLogger(WhiteboardHandler.class.getName());

    private ObjectMapper mapper;
    private OnlineUsers onlineUsers;
    private PersistenceIntegration whiteboardPersistence;

    public WhiteboardHandler(ObjectMapper mapper, OnlineUsers onlineUsers, PersistenceIntegration whiteboardPersistence) {
        this.mapper = mapper;
        this.onlineUsers = onlineUsers;
        this.whiteboardPersistence = whiteboardPersistence;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        String connID = connection.httpRequest().id().toString();
        String boardID = connection.httpRequest().queryParam("t");
        String username = connection.httpRequest().queryParam("u");

        LOG.info("(p) username=" + username + ", boardID=" + boardID);

        WhiteboardDetail whiteboard = whiteboardPersistence.addUser(boardID, username);
        if (whiteboard == null) {
            connection.close();
            return;
        }

        OnlineUser user = onlineUsers.addUser(connID, boardID, username, whiteboard.users.get(username), connection);
        connection.data("user", user);

        LOG.info("(+)    connected: " + connID + ", user=" + user.getUsername() + ", board=" + user.getBoardID());

        // send drawing items to user.
        for (WhiteboardDetail.DrawingItem drawingItem : whiteboard.items)
            encode(connection, "ondraw", drawingItem);

        // notify other users
        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            if (!tmp.getConnectionID().equals(user.getConnectionID())) {
                encode(tmp.client, "onjoin", user);
            }
        }
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        String connID = connection.httpRequest().id().toString();
        OnlineUser removed = onlineUsers.removeUser(connID);
        LOG.info("(-) disconnected: " + connID + ", user=" + removed.getUsername() + ", board=" + removed.getBoardID());

        // notify other users
        for (OnlineUser tmp : onlineUsers.findByBoardID(removed.getBoardID())) {
            encode(tmp.client, "onleave", tmp);
        }
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) throws Throwable {

        OnlineUser user = (OnlineUser) connection.data("user");

        LOG.info("received(" + user.client.httpRequest().id() + ") = " + message);

        int idx = message.indexOf(",");
        if (idx == -1)
            return;
        String action = message.substring(0, idx);
        String payload = message.substring(idx + 1);

        if ("draw".equals(action))
            draw(user, payload);
        else if ("remove".equals(action))
            remove(user, payload);
        else if ("move".equals(action))
            move(user, payload);
    }

    public void draw(OnlineUser user, String payload) {

        try {
            WhiteboardDetail.DrawingItem item = mapper.readValue(payload, WhiteboardDetail.DrawingItem.class);
            item.username = user.getUsername();

            WhiteboardDetail detail = whiteboardPersistence.addDrawingItem(user.getBoardID(), item);

            for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
                if (!tmp.getConnectionID().equals(user.getConnectionID())) {
                    encode(tmp.client, "ondraw", item);
                }
            }
        } catch (IOException e) {
            LOG.warning("Error parsing: " + e.getMessage());
        }
    }

    public void remove(OnlineUser user, String elementID) {

        whiteboardPersistence.removeDrawingItem(user.getBoardID(), elementID);

        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            if (!tmp.getConnectionID().equals(user.getConnectionID())) {
                encode(tmp.client, "onremove", elementID);
            }
        }
    }

    public class MoveCoords {
        public double x;
        public double y;
    }

    public void move(OnlineUser user, String payload) throws Exception {

        MoveCoords move = mapper.readValue(payload, MoveCoords.class);

        System.out.println(user.getUsername() + " moved to " + move.x + ", " + move.y);

        /*
        String id = webSocketConnection.httpRequest().id().toString();
        WhiteboardUser user = users.findByConnectionID(id);
        System.out.println(user.getUsername() + " moves to " + x + ", " + y);
        for (WhiteboardUser wbUser : users.findExcept(id))
            wbUser.getClient().onmove(user.getUsername(), x, y);
            */
    }

    private void encode(WebSocketConnection connection, String action, Object object) {
        try {
            String encoded = action + "," + mapper.writeValueAsString(object);
            LOG.info("sending(" + connection.httpRequest().id() + ") = " + encoded);
            connection.send(encoded);
        } catch (Exception e) {
            LOG.warning("Error processing: " + e.getMessage());
        }
    }

}
