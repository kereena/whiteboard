package whiteboard.persistence;

public interface PersistenceIntegration {

    public WhiteboardDetail create(String owner, String title, String description);

    public WhiteboardDetail findByBoardID(String boardID);

    public WhiteboardDetail addUser(String boardID, String username);

    public WhiteboardDetail addDrawingItem(String boardID, WhiteboardDetail.DrawingItem drawingItem);

    public WhiteboardDetail removeDrawingItem(String boardID, String elementID);
}
