package whiteboard.colors;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class CyclingHtmlColorsTest {

    @Test
    public void testGetColor() throws Exception {

        CyclingHtmlColors testing = new CyclingHtmlColors();

        assertEquals("blue", testing.getColor("kereena", 1));

        assertEquals("black", testing.getColor("kereena", 0));

        assertEquals("blue", testing.getColor("kereena", 8));
    }
}
