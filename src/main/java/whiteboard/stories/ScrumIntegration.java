package whiteboard.stories;

import java.util.List;

public interface ScrumIntegration {
    /**
     * Get information about stories in the scrum system
     * @return
     */
    public List<StoryDetail> getStories();
}
