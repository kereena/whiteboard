package whiteboard.persistence.memory;

import org.junit.Before;
import org.junit.Test;
import whiteboard.colors.ColorsIntegration;
import whiteboard.colors.CyclingHtmlColors;
import whiteboard.persistence.WhiteboardDetail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class MemoryNoPersistenceTest {

    MemoryNoPersistence testing;

    @Before
    public void setup() throws Exception {
        ColorsIntegration colors = new CyclingHtmlColors(); // it has already been tested.
        testing = new MemoryNoPersistence(colors);
    }

    @Test
    public void testCreate() throws Exception {

        String owner = "kereena";
        String title = "the title";
        String description = "the description";

        WhiteboardDetail detail = testing.create(owner, title, description);

        assertNotNull(detail);
        assertNotNull(detail.boardID);

        assertEquals(owner, detail.owner);
        assertEquals(title, detail.title);
        assertEquals(description, detail.description);
    }

    @Test
    public void testFindByBoardID() throws Exception {

        WhiteboardDetail detail = testing.create("kereena", "the title", "the description");

        WhiteboardDetail detail2 = testing.findByBoardID(detail.boardID);

        assertNotNull(detail2);
        assertEquals("kereena", detail2.owner);
        assertEquals("the title", detail2.title);
        assertEquals("the description", detail2.description);

        assertEquals(detail.owner, detail2.owner);
        assertEquals(detail.title, detail2.title);
        assertEquals(detail.description, detail2.description);
    }

    @Test
    public void testAddUser() throws Exception {

        WhiteboardDetail detail = testing.create("aaaa", "the title", "the description");

        assertEquals(0, detail.users.size());

        WhiteboardDetail detail2 = testing.addUser(detail.boardID, "bbbb");

        assertEquals(1, detail2.users.size());
        assertEquals("bbbb", detail2.users.keySet().iterator().next());
    }

    @Test
    public void testAddDrawingItem() throws Exception {

        WhiteboardDetail detail = testing.create("aaaa", "the title", "the description");

        assertEquals(0, detail.items.size());

        WhiteboardDetail detail2 = testing.addDrawingItem(detail.boardID, new WhiteboardDetail.DrawingItem("bbbb", "eid", null));

        assertEquals(1, detail2.items.size());
        assertEquals("bbbb", detail2.items.get(0).username);
        assertEquals("eid", detail2.items.get(0).elementID);
        assertNull(detail2.items.get(0).elementData);

    }

    @Test
    public void testRemoveDrawingItem() throws Exception {

        WhiteboardDetail detail = testing.create("aaaa", "the title", "the description");

        assertEquals(0, detail.items.size());

        testing.addDrawingItem(detail.boardID, new WhiteboardDetail.DrawingItem("bbbb", "eid1", null));
        testing.addDrawingItem(detail.boardID, new WhiteboardDetail.DrawingItem("bbbb", "eid2", null));
        testing.addDrawingItem(detail.boardID, new WhiteboardDetail.DrawingItem("bbbb", "eid3", null));
        WhiteboardDetail detail2 = testing.addDrawingItem(detail.boardID, new WhiteboardDetail.DrawingItem("bbbb", "eid4", null));

        assertEquals(4, detail2.items.size());

        WhiteboardDetail detail3 = testing.removeDrawingItem(detail.boardID, "eid2");

        assertEquals(3, detail3.items.size());

    }
}
