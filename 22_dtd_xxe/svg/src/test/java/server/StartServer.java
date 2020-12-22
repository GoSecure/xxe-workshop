package server;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.webapp.WebAppContext;

import java.util.logging.LogManager;

public class StartServer {
    
    public static void main(String[] args) throws Exception {

        LogManager.getLogManager().reset();
        // Webapp code and page contexts
        final WebAppContext context = new WebAppContext();
        context.setWar(System.getProperty("user.dir") + "/src/main/webapp/");
        context.setParentLoaderPriority(true);
        context.setContextPath("/");

        final Server server = new Server(80);
        server.setHandler(context);

        server.start();
        server.join();
    }

}
