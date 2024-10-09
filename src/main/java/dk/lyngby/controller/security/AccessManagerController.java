package dk.lyngby.controller.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.AuthDao;
import dk.lyngby.dto.TokenDto;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import dk.token.TokenFactory;
import dk.token.exceptions.TokenException;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;

import java.util.stream.Collectors;

public class AccessManagerController {

    public void accessManagerHandler(Context ctx) throws Exception {

//        if (ctx.routeRoles().isEmpty()) { // route roles can only be attached to endpoint handlers
//            return; // if there are no route roles, we don't need to check anything
//        }


        if (!ctx.routeRoles().contains(Role.RoleName.ANYONE)) {
            boolean isAuthorized = false;

            Role.RoleName[] userRoles = getUserRoles(ctx);

            for (RouteRole role : userRoles) {
                if (ctx.routeRoles().contains(role)) {
                    isAuthorized = true;
                    break;
                }
            }

            if (!isAuthorized) {
                throw new AuthorizationException(401, "You are not authorized to perform this action");
            }
        }
    }

    private Role.RoleName[] getUserRoles(Context ctx) throws TokenException {

        AuthDao authDao = AuthDao.getInstance(HibernateConfig.getEntityManagerFactory(false));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String token;

        // TODO: get token from header
        try {
            token = ctx.header("Authorization").split(" ")[1];
        } catch (Exception e) {
            throw new TokenException("No Token provided", e);
        }

        // TODO: verify token and get roles from token
        try {
            // TODO: get user from token and get roles from user
            String usernameFromToken = TokenFactory.getUsernameFromToken(token);
            User user = authDao.getUser(usernameFromToken);

            // TODO: get roles from user and transform to a String like this: "[ADMIN, USER]"
            String roles = user.getRoleList().stream().map(role -> role.getRoleName().toString()).collect(Collectors.joining(", ", "[", "]"));

            String claim = TokenFactory.verifyToken(token, ApplicationConfig.getProperty("secret.key"), ApplicationConfig.getClaimBuilder(user, roles));
            return gson.fromJson(claim, TokenDto.class).roles();
        } catch (Exception e) {
            throw new TokenException("Token is not valid", e);
        }

    }

}
