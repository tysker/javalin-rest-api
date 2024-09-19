package dk.lyngby.handler;

import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.exceptions.Message;
import dk.lyngby.routes.Routes;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

    public void exceptionHandlerNotAuthorized(AuthorizationException e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.json(new Message(e.getStatusCode(), e.getMessage()));
    }

    public void exceptionHandlerApi(ApiException e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        ctx.status(e.getStatusCode());
        ctx.json(new Message(e.getStatusCode(), e.getMessage()));
    }
    public void exceptionHandler(Exception e, Context ctx) {
        LOGGER.error(ctx.attribute("requestInfo") + " " + ctx.res().getStatus() + " " + e.getMessage());
        ctx.status(500);
        ctx.json(new Message(500, e.getMessage()));
    }
}
