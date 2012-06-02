package whiteboard.persistence;

import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
@Invariant({
        "users.size() <= items.size()", // there cant be more users than items
        "boardID != null", // boradID must be there.
        "title != null" // title must be there.
})
public class WhiteboardDetail {
    @JsonProperty("_id") // NOTE: this is for MongoDB to use boardID as _id (primary key)
    public String boardID;
    public String owner;
    public String title;
    public String description;
    public Map<String, String> users = new LinkedHashMap<java.lang.String, java.lang.String>();
    public List<DrawingItem> items = new ArrayList<DrawingItem>();

    @Requires({
            "item.username != null", // we need username
            "item.elementID != null", // we need id
            "item.elementData != null", // we need data
            "users.containsKey(item.username)" // username must be registered already.
    })
    @Ensures("items.size() == old(items.size()) + 1")
    public void addDrawingItem(DrawingItem item) {
        items.add(item);
    }

    public void addUser(String username, String color) {
        users.put(username, color);
    }

    public static class DrawingItem {
        public String username;
        public String elementID;
        public JsonNode elementData;
        public DrawingItem() {
        }
        public DrawingItem(String username, String elementID, JsonNode elementData) {
            this.username = username;
            this.elementID = elementID;
            this.elementData = elementData;
        }
    }
}
