package dk.lyngby.controller.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.TokenFactory;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.AuthDao;
import dk.lyngby.dto.TokenDto;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessManagerController {

    public void accessManagerHandler(Handler handler, Context ctx, Set<? extends RouteRole> permittedRoles) throws Exception {
        System.out.println(permittedRoles);
        String path = ctx.path();
        boolean isAuthorized = false;

        if (path.equals("/api/v1/routes") || permittedRoles.contains(Role.RoleName.ANYONE) || Objects.equals(ctx.method().toString(), "OPTIONS")) {
            handler.handle(ctx);
            return;
        } else {
            RouteRole[] roles = getRoles(ctx);
            for (RouteRole role : roles) {
                if (permittedRoles.contains(role)) {
                    isAuthorized = true;
                    break;
                }
            }
        }

        if (isAuthorized) {
            handler.handle(ctx);
        } else {
            throw new AuthorizationException(401, "You are not authorized to perform this action");
        }
    }

    private Role.RoleName[] getRoles(Context ctx) throws TokenException, ApiException, IOException {

        AuthDao dao = AuthDao.getInstance(HibernateConfig.getEntityManagerFactory(false));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String token = ctx.header("Authorization").split(" ")[1];

        // TODO: get user from token and get roles from user
        String usernameFromToken = TokenFactory.getUsernameFromToken(token);
        User user = dao.getUser(usernameFromToken);

        // TODO: get roles from user and transform to a String like this: "[ADMIN, USER]"
        String roles = user.getRoleList().stream().map(role -> role.getRoleName().toString()).collect(Collectors.joining(", ", "[", "]"));

        String claim = TokenFactory.verifyToken(token, ApplicationConfig.getProperty("secret.key"), ApplicationConfig.getClaimBuilder(user, roles));
        return gson.fromJson(claim, TokenDto.class).roles();

    }

}
