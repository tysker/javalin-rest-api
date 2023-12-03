package dk.lyngby.routes;

import dk.lyngby.controller.exceptions.ExceptionController;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.Role;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final ExceptionController exceptionController = new ExceptionController();
    private int count = 0;

    private final AuthRoute authRoutes = new AuthRoute();

    private final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

    private void requestInfoHandler(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        ctx.attribute("requestInfo", requestInfo);
    }


    public EndpointGroup getRoutes(Javalin app) {
        return () -> {
            app.before(this::requestInfoHandler);
            app.routes(() -> {
                path("/", authRoutes.getRoutes());
                path("/test", () -> {
                    get("/", ctx -> ctx.json("{\"msg\":\"Hello from test\"}"), Role.RoleName.ANYONE);
                });
            });

            app.after(ctx -> LOGGER.info(" Request {} - {} was handled with status code {}", count++, ctx.attribute("requestInfo"), ctx.status()));

            app.exception(ConstraintViolationException.class, exceptionController::constraintViolationExceptionHandler);
            app.exception(ValidationException.class, exceptionController::validationExceptionHandler);
            app.exception(ApiException.class, exceptionController::apiExceptionHandler);
            app.exception(AuthorizationException.class, exceptionController::exceptionHandlerNotAuthorized);
            app.exception(TokenException.class, exceptionController::tokenExceptionHandler);
            app.exception(Exception.class, exceptionController::exceptionHandler);

        };
    }
}
