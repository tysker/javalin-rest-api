package dk.lyngby.config;

import dk.lyngby.handler.AccessManagerHandler;
import dk.lyngby.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApplicationConfig {

    private static final AccessManagerHandler ACCESS_MANAGER_HANDLER = new AccessManagerHandler();

    public static void configurations(JavalinConfig config) {
        // logging
        if (System.getenv("DEPLOYED") == null)
            config.plugins.enableDevLogging(); // enables extensive development logging in terminal

        // http
        config.http.defaultContentType = "application/json"; // default content type for requests
        //config.compression.brotliAndGzip(); // enable brotli and gzip compression of responses

        // routing
        config.routing.contextPath = "/api/v1"; // base path for all routes
        config.routing.ignoreTrailingSlashes = true; // removes trailing slashes for all routes

        // access management roles allowed for routes (see AccessManagerHandler)
        config.accessManager(ACCESS_MANAGER_HANDLER::accessManagerHandler);

        // Route overview
        config.plugins.register(new RouteOverviewPlugin("/routes")); // enables route overview at /routes


    }

    public static String getProperty(String propName) throws IOException {
        try (InputStream is = HibernateConfig.class.getClassLoader().getResourceAsStream("properties-from-pom.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(propName);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("Could not read property from pom file. Build Maven!");
        }
    }

    public static void startServer(Javalin app, int port) {
        Routes routes = new Routes();
        app.updateConfig(ApplicationConfig::configurations);
        app.options("/*", )
        app.routes(routes.getRoutes(app));
        HibernateConfig.setTest(false);
        app.start(port);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

}
