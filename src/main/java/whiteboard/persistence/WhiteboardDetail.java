package whiteboard.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.vz.mongodb.jackson.ObjectId;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

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

        public DrawingItem(String username, String elementID, String elementType, JsonElement elementData) {
            this.username = username;
            this.elementID = elementID;
            this.elementType = elementType;
            this.elementData = elementData;
        }

        public DrawingItem() {
        }

        public String elementID;
        public String elementType;
        public String username;
        public transient JsonElement elementData;

        // this is to help mongodb to save a json string instead of serializing JsonElement
        // it converts the elementData into a string and vice versa.
        public String getJsonData() {
            return new Gson().toJson(elementData);
        }
        public void setJsonData(String json) {
            this.elementData = new Gson().fromJson(json, JsonElement.class);
        }
    }

}
