package whiteboard.stories;

import org.junit.Test;
import whiteboard.stories.scrumdo.ScrumDoIntegration;

import java.util.List;

public class ScrumDoIntegrationTest {

    ScrumDoIntegration scrumDoIntegration;

    @Test
    public void testGetAllStories() throws Exception {

        scrumDoIntegration = new ScrumDoIntegration();

        List<StoryDetail> allStories = scrumDoIntegration.getStories();

        for (StoryDetail story : allStories) {
            System.out.println("story.title       = " + story.getStoryTitle());
            System.out.println("story.description = " + story.getStoryDescription());
            System.out.println("story.externalID  = " + story.getStoryExternalID());
        }
    }
}
