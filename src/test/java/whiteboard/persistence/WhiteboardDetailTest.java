package whiteboard.persistence;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class WhiteboardDetailTest {

    WhiteboardDetail testing;

    @Before
    public void setUp() throws Exception {
        testing = new WhiteboardDetail();
    }

    @Test
    public void testCreate() throws Exception {

        assertNotNull(testing.items);
        assertNotNull(testing.users);

        assertNull(testing.boardID);
        assertNull(testing.description);
        assertNull(testing.owner);
        assertNull(testing.title);
    }
}
