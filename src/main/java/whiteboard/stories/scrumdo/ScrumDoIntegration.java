package whiteboard.stories.scrumdo;

import scrumdo.ScrumDoApiFactory;
import scrumdo.meta.FindResult;
import scrumdo.meta.Tastypie;
import scrumdo.model.Story;
import whiteboard.stories.ScrumIntegration;
import whiteboard.stories.StoryDetail;

import java.util.ArrayList;
import java.util.List;

public class ScrumDoIntegration implements ScrumIntegration {

    private static String SCRUMDO_DEVELOPER_KEY = "c49089fa940d115318dcc17434fd23f682577740";
    private static String SCRUMDO_USERNAME = "kereena";
    private static String SCRUMDO_PASSWORD = "Sunflower1";

    public static ScrumDoIntegration newScrumDoIntegration() {
        // construct ScrumDoApi object with my given details.
        Tastypie pie = ScrumDoApiFactory.newScrumDo(
                SCRUMDO_DEVELOPER_KEY,
                SCRUMDO_USERNAME,
                SCRUMDO_PASSWORD
        ).getPie();

        return new ScrumDoIntegration(pie);
    }

    // tastypie is the API SCrumDo are using.
    private Tastypie pie;

    private long nextUpdate = 0;
    private List<StoryDetail> lastFound;

    protected ScrumDoIntegration(Tastypie pie) {
        this.pie = pie;
    }

    /**
     * Returns stories which are in Progress or in Reviewing state.
     * @return
     */
    public List<StoryDetail> getStories() {

        long now = System.currentTimeMillis();

        if (now > nextUpdate) {

            System.out.println("loading ... ");

            FindResult<Story> stories = pie.findAll(Story.class, Story.TYPE);

            List<StoryDetail> result = new ArrayList<StoryDetail>();
            for (Story story : stories.objects) {
                // status documentation: 1 = todo, 2 = in progress, 3 = reviewing, 4 = done.
                if (story.status == 2 || story.status == 3) {
                    StoryDetail detail = new StoryDetail("Story " + story.local_id + " " + story.summary, story.detail, story.resource_uri.resource_uri);
                    result.add(detail);
                }
            }

            this.lastFound = result;
            nextUpdate = now + (3600 * 1000); // wait 1 hr before next update
        }

        return lastFound;
    }
}
