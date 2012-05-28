package whiteboard.web;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class SnapshotHandler implements HttpHandler {

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        String type = httpRequest.queryParam("type");
        String svg  = httpRequest.queryParam("svg");
        String file = httpRequest.queryParam("file");


    }
}
