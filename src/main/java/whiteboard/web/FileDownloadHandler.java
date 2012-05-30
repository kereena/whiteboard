package whiteboard.web;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.resources.ResourceItem;
import whiteboard.resources.ResourcesIntegration;

public class FileDownloadHandler implements HttpHandler {

    private ResourcesIntegration resourcesIntegration;

    public FileDownloadHandler(ResourcesIntegration resourcesIntegration) {
        this.resourcesIntegration = resourcesIntegration;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        String resourceID = httpRequest.queryParam("r");

        ResourceItem item = resourcesIntegration.findByID(resourceID);

        if (item == null)
            return;

        httpResponse.header("Content-Type", item.contentType).content(item.content).end();
    }
}
