package whiteboard.web;

import org.webbitserver.WebSocketConnection;

/**
 * Online user has connectionID, username, a boardID and the websocket client connection.
 */
public class OnlineUser {

    private String connectionID;
    private String boardID;
    private String username;
    private String color;
    private transient WebSocketConnection client;

    public OnlineUser(String connectionID, String boardID, String username, String color, WebSocketConnection client) {
        this.connectionID = connectionID;
        this.boardID = boardID;
        this.username = username;
        this.color = color;
        this.client = client;
    }

    public String getConnectionID() {
        return connectionID;
    }

    public String getBoardID() {
        return boardID;
    }

    public String getUsername() {
        return username;
    }

    public String getColor() {
        return color;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public WebSocketConnection getClient() {
        return client;
    }
}
