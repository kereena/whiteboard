package whiteboard.stories.scrumdo;

import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import scrumdo.meta.FindResult;
import scrumdo.meta.GsonBuilderCallback;
import scrumdo.meta.ResourceURI;
import scrumdo.meta.Tastypie;
import scrumdo.model.Story;
import whiteboard.stories.StoryDetail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author A. Y. Kereena Davidsen <yani.kereena@gmail.com>
 */
public class ScrumDoIntegrationTest {

    // test stories
    FindResult<Story> stories = new FindResult<Story>();

    ScrumDoIntegration testing;

    @Before
    public void setup() {
        // create a "mockup" pie object.
        Tastypie pie = new Tastypie(new GsonBuilderCallback() {
            @Override
            public void doWithBuilder(GsonBuilder gsonBuilder) {
                // do nothing
            }
        }, null, null) {
            @Override
            public <T> FindResult<T> findAll(Class<T> clazz, Type actualRequestedType) {
                return (FindResult<T>) stories;
            }
        };
        // make scrumdo integration using this mockup object.
        testing = new ScrumDoIntegration(pie);
    }

    @Test
    public void testGetStories() throws Exception {

        Story story = new Story();
        story.detail = "The story description";
        story.summary = "The story summary";
        story.local_id = 13;
        story.status = 3;
        story.resource_uri = new ResourceURI("/story/1");
        Story story2 = new Story();
        story2.detail = "The story description 2";
        story2.summary = "The story summary 2";
        story2.local_id = 15;
        story2.status = 1;
        story2.resource_uri = new ResourceURI("/story/2");
        stories.objects = Arrays.asList(story);

        List<StoryDetail> details = testing.getStories();

        assertEquals(1, details.size());
        assertEquals("The story description", details.get(0).getStoryDescription());
        assertEquals("/story/1", details.get(0).getStoryExternalID());

    }
}
