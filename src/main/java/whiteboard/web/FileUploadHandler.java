package whiteboard.web;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class FileUploadHandler implements HttpHandler {

    // allowed content types
    public static String[] CONTENT_TYPES = {
            "image/png",
            "image/jpeg"
    };

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {


        String username = httpRequest.queryParam("username");
        String boardID  = httpRequest.queryParam("boardID");

        byte[] body = httpRequest.bodyAsBytes();

        httpResponse.end();

        //To change body of implemented methods use File | Settings | File Templates.
    }
}
