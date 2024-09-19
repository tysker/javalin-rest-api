package dk.lyngby.routes;

import dk.lyngby.handler.PersonHandler;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PersonRoutes {

    private final PersonHandler personHandler = new PersonHandler();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/person", () -> {
                post("/", personHandler::create, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/", personHandler::readAll, RouteRoles.ANYONE);
                get("/search", (ctx) -> {
                    String hello = "Hello World";
                    ctx.json(hello);
                }, RouteRoles.ANYONE);
                get("{id}", personHandler::read, RouteRoles.USER, RouteRoles.ADMIN);
                put("{id}", personHandler::update, RouteRoles.ADMIN);
                delete("{id}", personHandler::delete, RouteRoles.ADMIN);
            });
        };
    }
}
