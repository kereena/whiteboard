package whiteboard.web;

import com.google.gson.Gson;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import whiteboard.stories.ScrumIntegration;
import whiteboard.stories.scrumdo.ScrumDoIntegration;

public class ScrumStoriesHandler implements HttpHandler {

    private ScrumIntegration scrumSystem = ScrumDoIntegration.newScrumDoIntegration();

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse, HttpControl httpControl) throws Exception {
        // output stories as JSON.
        httpResponse.header("Content-Type", "application/json")
                .content(new Gson().toJson(scrumSystem.getStories()))
                .end();
    }

}
