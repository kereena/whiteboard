package whiteboard.resources;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public interface ResourcesIntegration {

    public ResourceItem create(String boardID, String filename, String contentType, byte[] content);

    public ResourceItem findByID(String resourceID);
}
