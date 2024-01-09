package dk.lyngby.routes;

import dk.lyngby.handler.UserHandler;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoutes {
    private final UserHandler userHandler = new UserHandler();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/auth", () -> {
                post("/login", userHandler::login);
                post("/register", userHandler::register);
                get("/", userHandler::readAll, RouteRoles.ADMIN);
                get("{name}", userHandler::read, RouteRoles.ADMIN);
                put("{name}", userHandler::update, RouteRoles.ADMIN);
                delete("{name}", userHandler::delete, RouteRoles.ADMIN);
            });
        };
    }
}
