package whiteboard.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.stories.ScrumIntegration;

public class ScrumStoriesHandler implements HttpHandler {

    private ObjectMapper mapper;
    private ScrumIntegration scrumSystem;

    public ScrumStoriesHandler(ObjectMapper mapper, ScrumIntegration scrumSystem) {
        this.mapper = mapper;
        this.scrumSystem = scrumSystem;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        // output stories as JSON.
        httpResponse.header("Content-Type", "application/json")
                .content(mapper.writeValueAsString(scrumSystem.getStories()))
                .end();
    }

}
