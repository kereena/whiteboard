package whiteboard.web.util;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class RequireGoogleChromeHandler implements HttpHandler {

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        String userAgent = httpRequest.header("User-Agent");
        if (userAgent != null && !userAgent.contains("Chrome"))
            httpResponse.status(400).content("Please use Google Chrome for this application.").end();
        else
            httpControl.nextHandler();
    }
}
