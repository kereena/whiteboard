package whiteboard.web;

import com.google.gson.Gson;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.util.logging.Logger;

public class WhiteboardHandler extends BaseWebSocketHandler {

    private Logger LOG = Logger.getLogger(WhiteboardHandler.class.getName());

    private OnlineUsers onlineUsers;
    private PersistenceIntegration whiteboardPersistence;

    private Gson gson = new Gson();

    public WhiteboardHandler(OnlineUsers onlineUsers, PersistenceIntegration whiteboardPersistence) {
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

        // send online users to user.
        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            encode(connection, "onjoin", tmp);
        }

        // send drawing items to user.
        for (WhiteboardDetail.DrawingItem drawingItem : whiteboard.items)
            encode(connection, "ondraw", drawingItem);

        // notify other users
        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            if (!tmp.getConnectionID().equals(user.getConnectionID())) {
                encode(tmp.getClient(), "onjoin", user);
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
            encode(tmp.getClient(), "onleave", tmp);
        }
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) throws Throwable {

        OnlineUser user = (OnlineUser) connection.data("user");

        LOG.info("received(" + user.getClient().httpRequest().id() + ") = " + message);

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

        WhiteboardDetail.DrawingItem item = gson.fromJson(payload, WhiteboardDetail.DrawingItem.class);
        System.out.println("item = " + item + ", user=" + user);
        item.username = user.getUsername();

        WhiteboardDetail detail = whiteboardPersistence.addDrawingItem(user.getBoardID(), item);

        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            if (!tmp.getConnectionID().equals(user.getConnectionID())) {
                encode(tmp.getClient(), "ondraw", item);
            }
        }
    }

    public void remove(OnlineUser user, String elementID) {

        whiteboardPersistence.removeDrawingItem(user.getBoardID(), elementID);

        for (OnlineUser tmp : onlineUsers.findByBoardID(user.getBoardID())) {
            if (!tmp.getConnectionID().equals(user.getBoardID())) {
                encode(tmp.getClient(), "onremove", elementID);
            }
        }
    }

    public void move(OnlineUser user, String elementID) {
        /*
        String id = webSocketConnection.httpRequest().id().toString();
        WhiteboardUser user = users.findByConnectionID(id);
        System.out.println(user.getUsername() + " moves to " + x + ", " + y);
        for (WhiteboardUser wbUser : users.findExcept(id))
            wbUser.getClient().onmove(user.getUsername(), x, y);
            */
    }

    private void encode(WebSocketConnection connection, String action, Object object) {
        String encoded = action + "," + gson.toJson(object);
        LOG.info("sending(" + connection.httpRequest().id() + ") = " + encoded);
        connection.send(encoded);
    }

}
