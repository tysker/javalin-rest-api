package dk.lyngby.routes;

import dk.lyngby.controller.security.AuthController;
import dk.lyngby.model.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class AuthRoute {
    private final AuthController authController = new AuthController();

    public EndpointGroup getRoutes() {

        return () -> {
            path("/", () -> {
                post("/login", authController::login, Role.RoleName.ANYONE);
                post("/register", authController::register, Role.RoleName.ANYONE);
            });
        };
    }

}
