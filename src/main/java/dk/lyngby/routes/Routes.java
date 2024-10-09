package dk.lyngby.routes;

import dk.lyngby.model.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final AuthRoute authRoutes = new AuthRoute();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/auth", authRoutes.getRoutes());
            path("/test", () -> {
                get("/", ctx -> ctx.json("{\"msg\":\"Hello from test\"}"), Role.RoleName.ADMIN);
            });
        };
    }
}
