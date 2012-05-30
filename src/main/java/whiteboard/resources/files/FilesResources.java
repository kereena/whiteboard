package whiteboard.resources.files;

import org.apache.commons.io.IOUtils;
import whiteboard.resources.ResourceItem;
import whiteboard.resources.ResourcesIntegration;

import java.io.*;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class FilesResources implements ResourcesIntegration {

    private File path;

    public FilesResources(File path) {
        this.path = path;
    }

    @Override
    public ResourceItem create(String boardID, String filename, String contentType, byte[] content) {
        ResourceItem item = new ResourceItem();
        item.id = boardID + "/" + new File(filename).getName();
        item.filename = filename;
        item.content = content;
        item.contentType = contentType;

        File file = new File(path, item.id);
        file.getParentFile().mkdirs();

        try {
            FileOutputStream out = new FileOutputStream(file);
            IOUtils.write(content, out);
            out.close();

            return item;
        } catch (IOException e) {
            throw new RuntimeException("Error saving: " + e.getMessage(), e);
        }
    }

    @Override
    public ResourceItem findByID(String resourceID) {

        File file = new File(path, resourceID);
        if (!file.exists())
            return null;

        ResourceItem item = new ResourceItem();
        item.contentType = file.getName().endsWith("jpg") ? "image/jpeg" : "image/png";
        item.filename = file.getName();
        item.id = resourceID;

        int length = (int) file.length();
        item.content = new byte[length];
        try {
            FileInputStream in = new FileInputStream(file);
            item.content = IOUtils.toByteArray(in);
            in.close();

            return item;
        } catch (IOException e) {
            throw new RuntimeException("Error reading: " + e.getMessage(), e);
        }
    }
}
