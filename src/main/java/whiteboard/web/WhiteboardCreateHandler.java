package whiteboard.web;

import com.google.gson.Gson;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.net.URLDecoder;
import java.util.logging.Logger;

public class WhiteboardCreateHandler implements HttpHandler {

    private Logger LOG = Logger.getLogger(WhiteboardCreateHandler.class.getName());
    private PersistenceIntegration whiteboardPersistence;

    public WhiteboardCreateHandler(PersistenceIntegration whiteboardPersistence) {
        this.whiteboardPersistence = whiteboardPersistence;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        String username = param(httpRequest, "u");
        String title    = param(httpRequest, "t");
        String descript = param(httpRequest, "d");

        WhiteboardDetail detail = whiteboardPersistence.create(username, title, descript);

        WhiteboardCreateResponse response = new WhiteboardCreateResponse();
        response.success = true;
        response.boardID = detail.boardID;
        response.message = "Created board '" + detail.title + "'";

        LOG.info("Created whiteboard : " + detail.boardID + " with title '" + title);

        // output stories as JSON.
        httpResponse.header("Content-Type", "application/json").content(new Gson().toJson(response)).end();
    }

    private String param(HttpRequest request, String param) throws Exception {
        String value = request.queryParam(param);
        if (value == null)
            return null;
        else
            return URLDecoder.decode(value.trim(), "utf-8");
    }

    public class WhiteboardCreateResponse {
        public boolean success;
        public String boardID;
        public String message;
    }

}
