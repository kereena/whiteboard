package whiteboard.stories;

import java.util.List;
import java.util.ArrayList;

public class DummyScrumIntegration implements ScrumIntegration {
    // Dummy stories
    public List<StoryDetail> getStories() {
        List<StoryDetail> r = new ArrayList<StoryDetail>();
        r.add(new StoryDetail("Finish the stuff", "To finish it", "1"));
        r.add(new StoryDetail("Finish the stuff 2", "To finish it 2", "2"));
        return r;
    }
}
