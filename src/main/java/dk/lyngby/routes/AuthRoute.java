package dk.lyngby.routes;

import dk.lyngby.controller.security.AuthController;
import dk.lyngby.model.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class AuthRoute {
    private final AuthController userController = new AuthController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/auth", () -> {
                post("/login", userController::login, Role.RoleName.ANYONE);
                post("/register", userController::register, Role.RoleName.ANYONE);
            });
        };
    }

}
