package slugify;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 * Class to slugify strings for SEO-friendly urls
 * @author jsk, www.maddemcode.com
 */
public class Slugify {

    public static String slugify(String input) throws UnsupportedEncodingException {
        if (input == null || input.length() == 0) return "";
        String toReturn = normalize(input);
        toReturn = toReturn.replace(" ", "-");
        toReturn = toReturn.toLowerCase();
        toReturn = URLEncoder.encode(toReturn, "UTF-8");
        return toReturn;
    }

    private static String normalize(String input) {
        if (input == null || input.length() == 0) return "";
        return Normalizer.normalize(input, Form.NFD).replaceAll("[^\\p{ASCII}]","");
    }
}