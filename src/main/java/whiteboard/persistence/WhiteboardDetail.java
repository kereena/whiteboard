package whiteboard.persistence;

import com.google.gson.JsonElement;

import java.util.*;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class WhiteboardDetail {

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
        public JsonElement elementData;
    }

}
