package whiteboard.persistence;

import java.util.List;

/**
 * Defines how the persistence should work.
 */
public interface PersistenceIntegration {

    public WhiteboardDetail create(String owner, String title, String description);

    public List<String> findIDs();

    public WhiteboardDetail findByBoardID(String boardID);

    public WhiteboardDetail addUser(String boardID, String username);

    public WhiteboardDetail addDrawingItem(String boardID, WhiteboardDetail.DrawingItem drawingItem);

    public WhiteboardDetail removeDrawingItem(String boardID, String elementID);
}
