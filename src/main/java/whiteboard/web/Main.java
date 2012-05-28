package whiteboard.web;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.handler.EmbeddedResourceHandler;
import org.webbitserver.handler.StaticFileHandler;
import whiteboard.colors.ColorsIntegration;
import whiteboard.colors.CyclingHtmlColors;
import whiteboard.persistence.PersistenceIntegration;
import whiteboard.persistence.memory.MemoryNoPersistence;
import whiteboard.web.util.RequireGoogleChromeHandler;

public class Main {

    public static void main(String[] args) throws Exception {

        // create object for online users
        OnlineUsers users = new OnlineUsers();

        // create object for colors
        ColorsIntegration colors = new CyclingHtmlColors();

        // create object for whiteboards.
        PersistenceIntegration whiteboardPersistence = new MemoryNoPersistence(colors);


        WebServer server = WebServers.createWebServer(1080)
                .add(new RequireGoogleChromeHandler()) // block other browsers than Chrome
                .add("/whiteboard", new WhiteboardHandler(users, whiteboardPersistence)) // handle whiteboard
                .add("/create", new WhiteboardCreateHandler(whiteboardPersistence)) // handle "/create" to create whiteboards.
                .add("/upload", new FileUploadHandler())  // handle uploads from whiteboard
                .add("/stories", new ScrumStoriesHandler()) // handle scrum stories
                .add(new EmbeddedResourceHandler("org/webbitserver/easyremote")) // helper for whiteboard communication
                .add(new StaticFileHandler("src/main/webapp")) // give path to HTML and Javascript files.
                .start() // start the server
                .get(); // and return it

        // print the server web address
        System.out.println("server started at " + server.getUri());

    }
}
