package app;

import app.servlets.*;
import app.tools.CookieFilter;
import app.tools.MigrationTool;
import app.tools.TemplateEngine;
import lombok.SneakyThrows;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import java.util.EnumSet;

public class TinderApp {
  @SneakyThrows
  public static void main(String[] args) {

    MigrationTool.prepare(
            System.getenv("JDBC_URL"),
            System.getenv("JDBC_USER"),
            System.getenv("JDBC_PASS")
    );


    int port;

    try {
      port = Integer.parseInt(System.getenv("PORT"));
    } catch (NumberFormatException e) {
      port = 5000;
    }


    //Configuration
    Server server = new Server(port);
    ServletContextHandler handler = new ServletContextHandler();
    TemplateEngine engine = TemplateEngine.folder("content");

    //Servlets
    handler.addServlet(new ServletHolder(new MainServlet()), "/*");
    handler.addServlet(new ServletHolder(new LoginServlet(engine)), "/login");
    handler.addServlet(RegistrationServlet.class, "/registration")
            .getRegistration().setMultipartConfig(new MultipartConfigElement("./img"));
    handler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
    handler.addServlet(new ServletHolder(new UserServlet(engine)), "/users");
    handler.addServlet(new ServletHolder(new LikedServlet(engine)), "/liked");
    handler.addServlet(new ServletHolder(new MessagesServlet(engine)), "/messages");
    handler.addServlet(new ServletHolder(new LinkServlet("css")), "/css/*");
    handler.addServlet(new ServletHolder(new LinkServlet("img")), "/img/*");


    //Filters
    handler.addFilter(CookieFilter.class, "/messages", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(CookieFilter.class, "/users", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(CookieFilter.class, "/liked", EnumSet.of(DispatcherType.REQUEST));
    handler.addFilter(CookieFilter.class, "/logout", EnumSet.of(DispatcherType.REQUEST));


    server.setHandler(handler);
    server.start();
    server.join();

  }
}
