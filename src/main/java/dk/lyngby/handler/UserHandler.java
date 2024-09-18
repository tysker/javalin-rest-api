package dk.lyngby.handler;

import com.google.gson.JsonObject;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.daos.UserDAO;
import dk.lyngby.dtos.UserDTO;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.exceptions.Message;
import dk.lyngby.model.User;
import dk.lyngby.security.TokenFactory;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UserHandler implements IEntityHandler<UserDTO, String> {

    private final UserDAO USER_DAO;
    private final TokenFactory TOKEN_FACTORY = TokenFactory.getInstance();

    public UserHandler() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        USER_DAO = UserDAO.getInstance(emf);
    }

    public void login(Context ctx) throws ApiException, AuthorizationException {
        String[] userInfos = getUserInfos(ctx, true);

        User user = getVerfiedOrRegisterUser(userInfos[0], userInfos[1], "", false);
        String token = getToken(userInfos[0], user.getRolesAsStrings());

        // Create response
        ctx.status(200);
        ctx.result(createResponse(userInfos[0], token));
    }

    public void register(Context ctx) throws ApiException, AuthorizationException {
        String[] userInfos = getUserInfos(ctx, false);
        User user = getVerfiedOrRegisterUser(userInfos[0], userInfos[1], userInfos[2], true);
        String token = getToken(userInfos[0], user.getRolesAsStrings());

        // Create response
        ctx.res().setStatus(201);
        ctx.result(createResponse(userInfos[0], token));
    }

    @Override
    public void read(Context ctx) throws ApiException {
        // request
        String userName = ctx.pathParamAsClass("name", String.class).check(this::validatePrimaryKey, "Not a valid name").get();
        // entity
        User user = USER_DAO.read(userName);
        // dto
        UserDTO userDTO = new UserDTO(user);
        // response
        ctx.res().setStatus(200);
        ctx.json(userDTO, UserDTO.class);
    }

    @Override
    public void readAll(Context ctx) throws ApiException {
        // entity
        List<User> users = USER_DAO.readAll();
        // dto
        List<UserDTO> userDTOS = UserDTO.toUserDTOList(users);
        // response
        ctx.status(200);
        ctx.json(userDTOS, UserDTO.class);
    }

    @Override
    public void create(Context ctx) throws ApiException {
        ctx.result("Use register instead");
    }

    @Override
    public void update(Context ctx) throws ApiException {
        ctx.result("Not implemented");
    }

    @Override
    public void delete(Context ctx) throws ApiException {
        // request
        String userName = ctx.pathParamAsClass("name", String.class).check(this::validatePrimaryKey, "Not a valid name").get();
        // entity
        USER_DAO.delete(userName);
        // response
        ctx.res().setStatus(200);
        ctx.json(new Message(200, "User with name " + userName + " deleted"), Message.class);
    }

    @Override
    public boolean validatePrimaryKey(String userName) {
        return USER_DAO.validatePrimaryKey(userName);
    }

    @Override
    public UserDTO validateEntity(Context ctx) {
        return null;
    }

    private String createResponse(String username, String token) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("username", username);
        responseJson.addProperty("token", token);
        return responseJson.toString();
    }

    private String[] getUserInfos(Context ctx, boolean tryLogin) throws ApiException {
        String request = ctx.body();
        return TOKEN_FACTORY.parseJsonObject(request, tryLogin);
    }

    private User getVerfiedOrRegisterUser(String username, String password, String role, boolean isCreate) throws AuthorizationException {
        return isCreate ? USER_DAO.registerUser(username, password, role) : USER_DAO.getVerifiedUser(username, password);
    }

    private String getToken(String username, Set<String> userRoles) throws ApiException {
        return TOKEN_FACTORY.createToken(username, userRoles);
    }
}
