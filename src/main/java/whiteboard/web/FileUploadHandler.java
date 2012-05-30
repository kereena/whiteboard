package whiteboard.web;

import com.google.gson.Gson;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.resources.ResourceItem;
import whiteboard.resources.ResourcesIntegration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUploadHandler implements HttpHandler {

    private ResourcesIntegration resourcesIntegration;

    public FileUploadHandler(ResourcesIntegration resourcesIntegration) {
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
        String filename = httpRequest.queryParam("f");
        String contentType = httpRequest.header("Content-Type");

        if (!CONTENT_TYPES.contains(contentType))
            return;

        byte[] body = httpRequest.bodyAsBytes();

        ResourceItem item = resourcesIntegration.create(boardID, filename, contentType, body);

        httpResponse.header("Content-Type", "application/json").content(new Gson().toJson(item)).end();
    }
}
