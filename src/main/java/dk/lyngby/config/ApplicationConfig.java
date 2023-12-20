package dk.lyngby.config;

import dk.lyngby.controller.exceptions.ExceptionController;
import dk.lyngby.controller.security.AccessManagerController;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import dk.lyngby.routes.Routes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static io.javalin.apibuilder.ApiBuilder.get;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ApplicationConfig {

    private static final AccessManagerController accessManagerController = new AccessManagerController();
    private static final ExceptionController exceptionController = new ExceptionController();
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final Routes routes = new Routes();
    private static int count = 0;

    private static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api/v1"; // base path for all routes
        config.http.defaultContentType = "application/json"; // default content type for requests
        config.bundledPlugins.enableRouteOverview("/routes", Role.RoleName.ANYONE); // enables route overview at /routes
        config.router.apiBuilder(ApplicationConfig.routes.getRoutes()); // register routes
    }

    public static void corsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    private static void requestInfoHandler(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        ctx.attribute("requestInfo", requestInfo);
    }

    public static void beforeContext(Javalin app){
        app.before(ApplicationConfig::requestInfoHandler);
        app.before(ApplicationConfig::corsHeaders);
    }

    public static void afterContext(Javalin app){
        app.after(ctx -> LOGGER.info(" Request {} - {} was handled with status code {}", count++, ctx.attribute("requestInfo"), ctx.status()));
    }

    public static void exceptionContext(Javalin app){
        app.exception(ConstraintViolationException.class, exceptionController::constraintViolationExceptionHandler);
        app.exception(ValidationException.class, exceptionController::validationExceptionHandler);
        app.exception(ApiException.class, exceptionController::apiExceptionHandler);
        app.exception(AuthorizationException.class, exceptionController::exceptionHandlerNotAuthorized);
        app.exception(TokenException.class, exceptionController::tokenExceptionHandler);
        app.exception(Exception.class, exceptionController::exceptionHandler);
    }

    public static void startServer(int port) {
        var app = Javalin.create(ApplicationConfig::configuration);
        app.beforeMatched(accessManagerController::accessManagerHandler);
        app.options("/*", ApplicationConfig::corsHeaders);
        beforeContext(app);
        exceptionContext(app);
        afterContext(app);
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
