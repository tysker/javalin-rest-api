package dk.lyngby.controller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.lyngby.TokenFactory;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.AuthDao;
import dk.lyngby.dto.LoginDto;
import dk.lyngby.dto.RegisterDto;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.io.IOException;
import java.util.List;

public class AuthController {

    private final AuthDao dao;

    public AuthController(){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory(false);
        this.dao = AuthDao.getInstance(emf);
    }

    public void login(Context ctx) throws AuthorizationException, TokenException, IOException {
        // TODO: validate login request and credentials
        LoginDto loginDto = ctx.bodyAsClass(LoginDto.class);
        User user = dao.verifyUser(loginDto.username(), loginDto.password());
        // TODO: Create a array of roles
        List<Role.RoleName> roleList = user.getRoleList().stream().map(Role::getRoleName).toList().stream().toList();
        // TODO: create token
        ClaimBuilder claimBuilder = ApplicationConfig.getClaimBuilder(user, roleList.toString());

        String token = TokenFactory.createToken(claimBuilder, ApplicationConfig.getProperty("secret.key"));

        ctx.status(200);
        ctx.json(createResponseObject(user.getUsername(), roleList, token));
    }

    public void register(Context ctx) throws ApiException, TokenException, IOException {

        // TODO: validate user
        RegisterDto registerDto = validateUser(ctx);
        // TODO: check if user exists
        dao.checkUser(registerDto.username());
        // TODO: check if role exists
        dao.checkRoles(registerDto.roleList());
        // TODO: register user
        dao.registerUser(registerDto.username(), registerDto.password(), registerDto.roleList());
        // TODO: Create a array of roles
        List<Role.RoleName> roleList = registerDto.roleList().stream().map(Role.RoleName::valueOf).toList().stream().toList();
        // TODO: create token
        ClaimBuilder claimBuilder = ApplicationConfig.getClaimBuilder(new User(registerDto.username(), registerDto.password()), roleList.toString());

        String token = TokenFactory.createToken(claimBuilder, ApplicationConfig.getProperty("secret.key"));

        ctx.status(201);
        ctx.json(createResponseObject(registerDto.username(), roleList, token));
    }

    private ObjectNode createResponseObject(String userName, List<Role.RoleName> roles, String token) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode respondNode = mapper.createObjectNode();
        respondNode.put("username", userName);
        respondNode.putPOJO("roles", roles);
        respondNode.put("token", token);
        return respondNode;
    }

    public RegisterDto validateUser(Context ctx) {
        return ctx.bodyValidator(RegisterDto.class)
                .check(user -> user.username() != null && user.password() != null, "Username and password must be set")
                .check(user -> user.username().length() >= 5, "Username must be at least 5 characters")
                .check(user -> user.password().length() >= 5, "Password must be at least 5 characters")
                .check(user -> !user.roleList().isEmpty(), "User must have at least one role")
                .get();
    }
}
