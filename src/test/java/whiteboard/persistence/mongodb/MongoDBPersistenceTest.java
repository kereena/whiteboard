package whiteboard.persistence.mongodb;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.DB;
import com.mongodb.Mongo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import whiteboard.colors.CyclingHtmlColors;
import whiteboard.persistence.WhiteboardDetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class MongoDBPersistenceTest {

    DB database;

    MongoDBPersistence testing;

    @After
    public void tearDown() throws Exception {
        this.database.dropDatabase();
    }

    @Before
    public void setUp() throws Exception {
        Mongo mongo = new Mongo();
        this.database = mongo.getDB("whiteboard-testing");
        testing = new MongoDBPersistence(this.database, new CyclingHtmlColors());
    }

    @Test
    public void testCreate() throws Exception {

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        assertNotNull(detail);

        assertEquals("aaaa", detail.owner);
        assertEquals("ttttt", detail.title);
        assertEquals("dddddddddd", detail.description);

        assertNotNull(detail.boardID);

        assertEquals(1, database.getCollection("whiteboards").count());

    }

    @Test
    public void testCreate2() throws Exception {

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        WhiteboardDetail detail2 = testing.create("aaaa", "ttttt", "dddddddddd");

        assertEquals(detail.boardID, detail2.boardID);

        assertEquals(1, database.getCollection("whiteboards").count());

    }


    @Test
    public void testFindByBoardID() throws Exception {

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        assertNotNull(detail);
        assertNotNull(detail.boardID);
        assertEquals(1, database.getCollection("whiteboards").count());

        WhiteboardDetail success = testing.findByBoardID(detail.boardID);
        assertNotNull(success);
        assertEquals("aaaa", success.owner);
        assertEquals("ttttt", success.title);
        assertEquals("dddddddddd", success.description);
        assertEquals(success.boardID, detail.boardID);

        WhiteboardDetail failure = testing.findByBoardID(detail.boardID + "morestuff");
        assertNull(failure);

    }

    @Test
    public void testAddUser() throws Exception {

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        assertNotNull(detail);
        assertNotNull(detail.boardID);
        assertEquals(0, detail.users.size());
        assertEquals(1, database.getCollection("whiteboards").count());

        WhiteboardDetail updated = testing.addUser(detail.boardID, "kkkkkkk");

        assertNotNull(updated);
        assertEquals(1, updated.users.size());
        assertEquals("kkkkkkk", updated.users.keySet().iterator().next());
        assertNotNull(updated.users.get("kkkkkkk"));

        assertEquals(1, database.getCollection("whiteboards").count());

        WhiteboardDetail found = testing.findByBoardID(detail.boardID);
        assertNotNull(found);
        assertEquals(1, found.users.size());
        assertEquals("kkkkkkk", updated.users.keySet().iterator().next());
        assertNotNull(updated.users.get("kkkkkkk"));
    }

    @Test
    public void testAddDrawingItem() throws Exception {

        JsonElement json = new Gson().fromJson("['a','b']", JsonElement.class);

        WhiteboardDetail.DrawingItem item = new WhiteboardDetail.DrawingItem("kkkkkkk", "ee", "ttt", json);

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        assertNotNull(detail);
        assertNotNull(detail.boardID);
        assertEquals(0, detail.items.size());
        assertEquals(1, database.getCollection("whiteboards").count());

        WhiteboardDetail updated = testing.addDrawingItem(detail.boardID, item);

        assertNotNull(updated);
        assertEquals(1, updated.items.size());
        WhiteboardDetail.DrawingItem di = updated.items.get(0);

        assertEquals("kkkkkkk", di.username);
        assertEquals("ee", di.elementID);
        assertEquals("ttt", di.elementType);
        assertNotNull(di.elementData);

        assertEquals(1, database.getCollection("whiteboards").count());

        WhiteboardDetail found = testing.findByBoardID(detail.boardID);
        assertNotNull(found);
        assertEquals(1, updated.items.size());
        di = updated.items.get(0);

        assertEquals("kkkkkkk", di.username);
        assertEquals("ee", di.elementID);
        assertEquals("ttt", di.elementType);
        assertNotNull(di.elementData);

        System.out.println("elementData loaded = " + di.elementData);
    }

    @Test
    public void testRemoveDrawingItem() throws Exception {

        JsonElement json = new Gson().fromJson("['a','b']", JsonElement.class);

        WhiteboardDetail.DrawingItem item = new WhiteboardDetail.DrawingItem("kkkkkkk", "ee1", "ttt", json);
        WhiteboardDetail.DrawingItem item2 = new WhiteboardDetail.DrawingItem("kkkkkkk", "ee2", "ttt", json);

        assertEquals(0, database.getCollection("whiteboards").count());

        WhiteboardDetail detail = testing.create("aaaa", "ttttt", "dddddddddd");

        assertNotNull(detail);
        assertNotNull(detail.boardID);
        assertEquals(0, detail.items.size());
        assertEquals(1, database.getCollection("whiteboards").count());

        testing.addDrawingItem(detail.boardID, item);
        testing.addDrawingItem(detail.boardID, item2);

        WhiteboardDetail found = testing.findByBoardID(detail.boardID);

        assertEquals(2, found.items.size());

        testing.removeDrawingItem(detail.boardID, "ee1");

        found = testing.findByBoardID(detail.boardID);
        assertEquals(1, found.items.size());

        assertEquals("ee2", found.items.get(0).elementID);


    }
}
