package whiteboard.persistence.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import slugify.Slugify;
import whiteboard.colors.ColorsIntegration;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class MongoDBPersistence implements PersistenceIntegration {

    private DB database;
    private ColorsIntegration colors;
    private JacksonDBCollection<WhiteboardDetail, String> whiteboards;

    public MongoDBPersistence(DB database, ColorsIntegration colors) {
        this.database = database;
        this.colors = colors;
        DBCollection mongoCollection = database.getCollection("whiteboards");
        this.whiteboards = JacksonDBCollection.wrap(mongoCollection, WhiteboardDetail.class, String.class);
    }

    @Override
    public List<String> findIDs() {
        List<String> ids = new ArrayList<String>();
        for (WhiteboardDetail detail : whiteboards.find())
            ids.add(detail.boardID);
        return ids;
    }

    @Override
    public WhiteboardDetail create(String owner, String title, String description) {

        try {
            // create object to save
            WhiteboardDetail detail = new WhiteboardDetail();
            detail.description = description;
            detail.owner = owner;
            detail.title = title;
            detail.boardID = Slugify.slugify(title);

            // save it
            WriteResult<WhiteboardDetail, String> result = whiteboards.insert(detail);

            // return saved object
            return result.getSavedObject();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error creating board ID: " + e.getMessage(), e);
        }
    }

    @Override
    public WhiteboardDetail findByBoardID(String boardID) {
        return whiteboards.findOneById(boardID);
    }

    @Override
    public WhiteboardDetail addUser(String boardID, String username) {
        WhiteboardDetail detail = whiteboards.findOneById(boardID);
        if (detail.users.containsKey(username))
            return detail;
        detail.users.put(username, colors.getColor(username, detail.users.size()));
        WriteResult<WhiteboardDetail, String> result =
                whiteboards.updateById(boardID, new DBUpdate.Builder().set("users." + username, detail.users.get(username)));
        return detail;
    }

    @Override
    public WhiteboardDetail addDrawingItem(String boardID, WhiteboardDetail.DrawingItem drawingItem) {
        WhiteboardDetail detail = whiteboards.findOneById(boardID);
        detail.items.add(drawingItem);
        WriteResult<WhiteboardDetail, String> result =
                whiteboards.updateById(boardID, new DBUpdate.Builder().push("items", drawingItem));
        return detail;
    }

    @Override
    public WhiteboardDetail removeDrawingItem(String boardID, String elementID) {
        whiteboards.updateById(boardID, new DBUpdate.Builder().pull("items", new BasicDBObject("elementID", elementID)));
        return whiteboards.findOneById(boardID);
    }
}
