package whiteboard.web;

import com.google.gson.Gson;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WhiteboardListHandler implements HttpHandler {

    private Logger LOG = Logger.getLogger(WhiteboardListHandler.class.getName());
    private PersistenceIntegration whiteboardPersistence;

    public WhiteboardListHandler(PersistenceIntegration whiteboardPersistence) {
        this.whiteboardPersistence = whiteboardPersistence;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        WhiteboardListResponse response = new WhiteboardListResponse();
        response.boardIDs = whiteboardPersistence.findIDs();
        response.success = true;

        // output list as JSON.
        httpResponse.header("Content-Type", "application/json").content(new Gson().toJson(response)).end();
    }

    public class WhiteboardListResponse {
        public boolean success;
        public List<String> boardIDs = new ArrayList<String>();
    }

}
