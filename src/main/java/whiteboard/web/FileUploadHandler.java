package whiteboard.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.resources.ResourceItem;
import whiteboard.resources.ResourcesIntegration;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class FileUploadHandler implements HttpHandler {

    private Logger LOG = Logger.getLogger(FileUploadHandler.class.getName());

    private ObjectMapper mapper;
    private ResourcesIntegration resourcesIntegration;

    public FileUploadHandler(ObjectMapper mapper, ResourcesIntegration resourcesIntegration) {
        this.mapper = mapper;
        this.resourcesIntegration = resourcesIntegration;
    }

    // allowed content types
    public static List<String> CONTENT_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg"
    );

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        String boardID  = httpRequest.queryParam("t");
        String filename = httpRequest.header("X-File-Name");
        String contentType = httpRequest.header("X-Content-Type");
        String contentLength = httpRequest.header("Content-Length");

        LOG.info("boardID=" + boardID + ", filename=" + filename + ", contentType=" + contentType + ", contentLength=" + contentLength);

        if (!CONTENT_TYPES.contains(contentType))
            return;

        byte[] body = httpRequest.bodyAsBytes();
        System.out.println("bytes = " + body.length);

        ResourceItem item = resourcesIntegration.create(boardID, filename, contentType, body);

        item.url = "resource/download?r=" + item.id;

        httpResponse.header("Content-Type", "application/json").content(mapper.writeValueAsString(item)).end();
    }
}
