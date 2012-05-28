package whiteboard.stories;

public class StoryDetail {

    private String storyTitle;
    private String storyDescription;
    private String storyExternalID;

    public StoryDetail(String storyTitle, String storyDescription, String storyExternalID) {
        this.storyTitle = storyTitle;
        this.storyDescription = storyDescription;
        this.storyExternalID = storyExternalID;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public String getStoryExternalID() {
        return storyExternalID;
    }
}
