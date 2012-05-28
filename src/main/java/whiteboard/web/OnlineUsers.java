package whiteboard.web;

import org.webbitserver.WebSocketConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to keep track of users who are online.
 */
public class OnlineUsers {

    private List<OnlineUser> users = new ArrayList<OnlineUser>();

    public OnlineUsers() {
    }

    public OnlineUser addUser(String connectionID, String boardID, String username, String color, WebSocketConnection client) {
        OnlineUser user = new OnlineUser(connectionID, boardID, username, color, client);
        users.add(user);
        return user;
    }

    public OnlineUser removeUser(String connectionID) {
        OnlineUser found = null;
        for (OnlineUser user : users)
            if (connectionID.equals(user.getConnectionID()))
                found = user;
        if (found != null)
            users.remove(found);
        return found;
    }

    public OnlineUser findByUsernameAndBoardID(String username, String boardID) {
        for (OnlineUser user : users)
            if (username.equals(user.getUsername()) && boardID.equals(user.getBoardID()))
                return user;
        return null;
    }

    public OnlineUser findByConnectionID(String connectionID) {
        for (OnlineUser user : users)
            if (connectionID.equals(user.getConnectionID()))
                return user;
        return null;
    }

    public List<OnlineUser> findByBoardID(String boardID) {
        List<OnlineUser> r = new ArrayList<OnlineUser>();
        for (OnlineUser user : users)
            if (user.getBoardID().equals(boardID))
                r.add(user);
        return r;
    }
}
