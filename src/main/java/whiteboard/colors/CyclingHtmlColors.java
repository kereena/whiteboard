package whiteboard.colors;

/**
 * This class returns a HTML color based on the number of users. It will cycle between the predefined
 * set of colors.
 *
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class CyclingHtmlColors implements ColorsIntegration {

    public static final String[] HTML_COLORS = {
            "black",
            "blue",
            "red",
            "green",
            "yellow",
            "cyan",
            "magenta",
    };

    @Override
    public String getColor(String username, int users) {
        return HTML_COLORS[users % HTML_COLORS.length];
    }
}
