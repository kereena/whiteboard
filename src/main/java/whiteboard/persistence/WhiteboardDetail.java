package whiteboard.persistence;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class WhiteboardDetail {

    @JsonProperty("_id") // NOTE: this is for MongoDB to use boardID as _id (primary key)
    public String boardID;

    public String owner;
    public String title;
    public String description;
    public Map<String, String> users = new LinkedHashMap<java.lang.String, java.lang.String>();
    public List<DrawingItem> items = new ArrayList<DrawingItem>();

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
