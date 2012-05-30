package whiteboard.web;

import com.mongodb.Mongo;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.EmbeddedResourceHandler;
import org.webbitserver.handler.StaticFileHandler;
import whiteboard.colors.ColorsIntegration;
import whiteboard.colors.CyclingHtmlColors;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.memory.MemoryNoPersistence;
import whiteboard.persistence.mongodb.MongoDBPersistence;
import whiteboard.resources.ResourcesIntegration;
import whiteboard.resources.files.FilesResources;
import whiteboard.web.util.RequireGoogleChromeHandler;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

        ResourcesIntegration resources = new FilesResources(new File("./files"));

        // create object for online users
        OnlineUsers users = new OnlineUsers();

        // create object for colors
        ColorsIntegration colors = new CyclingHtmlColors();

        // create object for whiteboards.
        //PersistenceIntegration whiteboardPersistence = new MemoryNoPersistence(colors);
        Mongo mongo = new Mongo();
        PersistenceIntegration whiteboardPersistence = new MongoDBPersistence(mongo.getDB("whiteboards"), colors);

        WebServer server = WebServers.createWebServer(8080)
                .add(new RequireGoogleChromeHandler()) // block other browsers than Chrome
                .add("/whiteboard/interface", new WhiteboardHandler(users, whiteboardPersistence)) // handle whiteboard
                .add("/whiteboard/create", new WhiteboardCreateHandler(whiteboardPersistence))
                // .add("/whiteboard/snapshot", new WhiteboardSnapshotHandler(whiteboardPersistence))
                .add("/whiteboard/export", new WhiteboardCreateHandler(whiteboardPersistence))
                .add("/whiteboard/list", new WhiteboardListHandler(whiteboardPersistence))
                .add("/resource/upload", new FileUploadHandler(resources))  // handle uploads from whiteboard
                .add("/resource/download", new FileDownloadHandler(resources))  // handle downloads from whiteboard
                .add("/scrum/stories", new ScrumStoriesHandler()) // handle scrum stories
                .add(new StaticFileHandler("src/main/webapp")) // give path to HTML and Javascript files.
                .start() // start the server
                .get(); // and return it

        // print the server web address
        System.out.println("server started at " + server.getUri());

    }
}
