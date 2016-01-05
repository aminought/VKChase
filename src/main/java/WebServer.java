import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import shared.controller.ApplicationModule;
import webserver.view.servlet.*;

import javax.servlet.Servlet;

public class WebServer {
    public static void main(String[] args) throws Exception {
        Injector servletsInjector = Guice.createInjector(new ApplicationModule());

        Servlet rootServlet = servletsInjector.getInstance(RootServlet.class);
        Servlet loginServlet = servletsInjector.getInstance(LoginServlet.class);
        Servlet addFollowingServlet= servletsInjector.getInstance(AddFollowingServlet.class);
        Servlet getGroupsServlet = servletsInjector.getInstance(GetGroupsServlet.class);
        Servlet likesInGroupsServlet = servletsInjector.getInstance(LikesInGroupsServlet.class);
        Servlet resultServlet = servletsInjector.getInstance(ResultServlet.class);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/vkchase");

        context.addServlet(new ServletHolder(rootServlet), "/");
        context.addServlet(new ServletHolder(loginServlet), "/login");
        context.addServlet(new ServletHolder(addFollowingServlet), "/following");
        context.addServlet(new ServletHolder(getGroupsServlet), "/groups");
        context.addServlet(new ServletHolder(likesInGroupsServlet), "/grouplikes");
        context.addServlet(new ServletHolder(resultServlet), "/result");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(false);
        resourceHandler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});

        Server server = new Server(8080);
        server.setHandler(handlers);
        server.start();
        server.join();
    }
}
