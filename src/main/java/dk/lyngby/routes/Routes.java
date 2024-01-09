package dk.lyngby.routes;

import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.handler.ExceptionHandler;
import dk.lyngby.exceptions.Message;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final PersonRoutes personRoutes = new PersonRoutes();
    private final UserRoutes userRoutes = new UserRoutes();
    private final Logger LOGGER = LoggerFactory.getLogger(Routes.class);
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private int count = 0;

    private void requestInfoHandler(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        ctx.attribute("requestInfo", requestInfo);
    }

    private void corsHandler(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    public EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.before(this::requestInfoHandler);
            app.before(this::corsHandler);
            app.options("/*", this::corsHandler);
            app.routes(() -> {
                path("/", userRoutes.getRoutes());
                path("/", personRoutes.getRoutes());
            });
            app.error(404, ctx -> {
                ctx.json(new Message(404, "Not found"));
                ctx.status(404);
            });
            app.after(ctx -> LOGGER.info(" Request {} - {} was handled with status code {}", count++, ctx.attribute("requestInfo"), ctx.status()));
            app.exception(ApiException.class, exceptionHandler::exceptionHandlerApi);
            app.exception(AuthorizationException.class, exceptionHandler::exceptionHandlerNotAuthorized);
            app.exception(Exception.class, exceptionHandler::exceptionHandler);
        };
    }
}
