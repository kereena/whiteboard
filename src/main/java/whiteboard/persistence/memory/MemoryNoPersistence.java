package whiteboard.persistence.memory;

import slugify.Slugify;
import whiteboard.colors.ColorsIntegration;
import whiteboard.colors.CyclingHtmlColors;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * MemoryNoPersistence is a PersistenceIntegration which uses memory for storing items. It is implemented for
 * testing and before I get to implement the database interface.
 *
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class MemoryNoPersistence implements PersistenceIntegration {

    private ColorsIntegration colors;
    private Map<String, WhiteboardDetail> whiteboards = new HashMap<String, WhiteboardDetail>();

    public MemoryNoPersistence(ColorsIntegration colors) {
        this.colors = colors;
    }

    @Override
    public List<String> findIDs() {
        return new ArrayList<String>(whiteboards.keySet());
    }

    @Override
    public WhiteboardDetail create(String owner, String title, String description) {
        try {
            WhiteboardDetail detail = new WhiteboardDetail();
            detail.boardID = Slugify.slugify(title); // use Slugify to create boardID from title.
            detail.title = title;
            detail.owner = owner;
            detail.description = description;

            if (whiteboards.containsKey(detail.boardID))
                throw new IllegalArgumentException("Contains whiteboard " + detail.boardID);

            whiteboards.put(detail.boardID, detail);

            return detail;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedCodingException", e);
        }
    }

    @Override
    public WhiteboardDetail findByBoardID(String boardID) {
        return whiteboards.get(boardID);
    }

    @Override
    public WhiteboardDetail addUser(String boardID, String username) {
        WhiteboardDetail detail = whiteboards.get(boardID);
        if (detail == null)
            throw new IllegalArgumentException("Invalid boardID " + boardID);

        String color = colors.getColor(username, detail.users.size());

        detail.users.put(username, color);

        return detail;
    }

    @Override
    public WhiteboardDetail addDrawingItem(String boardID, WhiteboardDetail.DrawingItem drawingItem) {
        WhiteboardDetail detail = whiteboards.get(boardID);
        if (detail == null)
            throw new IllegalArgumentException("Invalid boardID " + boardID);

        detail.items.add(drawingItem);

        return detail;
    }

    @Override
     public WhiteboardDetail removeDrawingItem(String boardID, String elementID) {
        WhiteboardDetail detail = whiteboards.get(boardID);
        if (detail == null)
            throw new IllegalArgumentException("Invalid boardID " + boardID);

        Iterator<WhiteboardDetail.DrawingItem> it = detail.items.iterator();
        while (it.hasNext()) {
            WhiteboardDetail.DrawingItem item = it.next();
            if (item.elementID.equals(elementID))
                it.remove();
        }

        return detail;
    }
}
