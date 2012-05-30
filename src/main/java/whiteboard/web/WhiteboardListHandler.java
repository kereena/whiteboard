package whiteboard.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.persistence.PersistenceIntegration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WhiteboardListHandler implements HttpHandler {

    private Logger LOG = Logger.getLogger(WhiteboardListHandler.class.getName());
    private ObjectMapper mapper;
    private PersistenceIntegration whiteboardPersistence;

    public WhiteboardListHandler(ObjectMapper mapper, PersistenceIntegration whiteboardPersistence) {
        this.mapper = mapper;
        this.whiteboardPersistence = whiteboardPersistence;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        WhiteboardListResponse response = new WhiteboardListResponse();
        response.boardIDs = whiteboardPersistence.findIDs();
        response.success = true;

        LOG.info("boards: " + response.boardIDs);

        // output list as JSON.
        httpResponse.header("Content-Type", "application/json").content(mapper.writeValueAsString(response)).end();
    }

    public class WhiteboardListResponse {
        public boolean success;
        public List<String> boardIDs = new ArrayList<String>();
    }

}
