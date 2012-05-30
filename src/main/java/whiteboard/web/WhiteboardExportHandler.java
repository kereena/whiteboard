package whiteboard.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.WhiteboardDetail;

import java.net.URLDecoder;
import java.util.logging.Logger;

public class WhiteboardExportHandler implements HttpHandler {

    private Logger LOG = Logger.getLogger(WhiteboardExportHandler.class.getName());
    private ObjectMapper mapper;
    private PersistenceIntegration whiteboardPersistence;

    public WhiteboardExportHandler(ObjectMapper mapper, PersistenceIntegration whiteboardPersistence) {
        this.mapper = mapper;
        this.whiteboardPersistence = whiteboardPersistence;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {

        String boardID    = param(httpRequest, "t");

        WhiteboardDetail detail = whiteboardPersistence.findByBoardID(boardID);

        LOG.info("Exported whiteboard : " + detail.boardID + " with title '" + detail.title);

        // output stories as JSON.
        httpResponse.header("Content-Type", "application/json").content(mapper.writeValueAsString(detail)).end();
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
