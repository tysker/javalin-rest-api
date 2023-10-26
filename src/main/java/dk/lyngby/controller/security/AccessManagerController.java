package dk.lyngby.controller.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.TokenFactory;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.AuthDao;
import dk.lyngby.dto.TokenDto;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessManagerController {

    public void accessManagerHandler(Handler handler, Context ctx, Set<? extends RouteRole> permittedRoles) throws Exception {

        String path = ctx.path();
        boolean isAuthorized = false;

        if (path.equals("/api/v1/routes") || permittedRoles.contains(Role.RoleName.ANYONE)) {
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

    private Role.RoleName[] getRoles(Context ctx) throws TokenException, ApiException {

        AuthDao dao = AuthDao.getInstance(HibernateConfig.getEntityManagerFactory());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String token = ctx.header("Authorization").split(" ")[1];

        // TODO: get user from token and get roles from user
        String usernameFromToken = TokenFactory.getUsernameFromToken(token);
        User user = dao.getUser(usernameFromToken);

        // TODO: get roles from user and transform to a String like this: "[ADMIN, USER]"
        String roles = user.getRoleList().stream().map(role -> role.getRoleName().toString()).collect(Collectors.joining(", ", "[", "]"));

        ClaimBuilder claimBuilder = ClaimBuilder.builder()
                .issuer("lyngby")
                .audience("datamatiker")
                .claimSet(Map.of("username", user.getUsername(), "roles", roles))
                .expirationTime(3600000L)
                .issueTime(3600000L)
                .build();

        String claim = TokenFactory.verifyToken(token, "841D8A6C80CBA4FCAD32D5367C18C53B", claimBuilder);
        return gson.fromJson(claim, TokenDto.class).roles();

    }

}
