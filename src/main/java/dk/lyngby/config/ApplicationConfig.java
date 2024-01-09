package dk.lyngby.config;

import dk.lyngby.controller.security.AccessManagerController;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.model.User;
import dk.lyngby.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApplicationConfig {

    private static final AccessManagerController accessManagerController = new AccessManagerController();
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private static void configuration(JavalinConfig config) {
        config.routing.contextPath = "/api/v1"; // base path for all routes
        config.http.defaultContentType = "application/json"; // default content type for requests
        config.plugins.register(new RouteOverviewPlugin("/routes")); // enables route overview at /
        config.accessManager(accessManagerController::accessManagerHandler);
    }

    public static void corsConfig(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    public static void startServer(Javalin app, int port) {
        Routes routes = new Routes();
        app.updateConfig(ApplicationConfig::configuration);
        app.before(ApplicationConfig::corsConfig);
        app.options("/*", ApplicationConfig::corsConfig);
        app.routes(routes.getRoutes(app));
        app.start(port);
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    public static ClaimBuilder getClaimBuilder(User user, String roles) throws IOException {
        return ClaimBuilder.builder()
                .issuer(ApplicationConfig.getProperty("issuer"))
                .audience(ApplicationConfig.getProperty("audience"))
                .claimSet(Map.of("username", user.getUsername(), "roles", roles))
                .expirationTime(Long.parseLong(ApplicationConfig.getProperty("token.expiration.time")))
                .issueTime(3600000L)
                .build();
    }


    public static String getProperty(String propName) throws IOException {
        try (InputStream is = HibernateConfig.class.getClassLoader().getResourceAsStream("properties-from-pom.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(propName);
        } catch (IOException ex) {
            LOGGER.error("Could not read property from pom file. Build Maven!");
            throw new IOException("Could not read property from pom file. Build Maven!");
        }
    }
}
