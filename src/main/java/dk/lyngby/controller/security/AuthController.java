package dk.lyngby.controller.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.lyngby.TokenFactory;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.dao.AuthDao;
import dk.lyngby.dto.LoginDto;
import dk.lyngby.dto.RegisterDto;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.exceptions.TokenException;
import dk.lyngby.model.ClaimBuilder;
import dk.lyngby.model.User;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class AuthController {

    private final AuthDao dao;

    public AuthController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = AuthDao.getInstance(emf);
    }


    public void login(Context ctx) throws AuthorizationException, TokenException {
        // TODO: validate login request and credentials
        LoginDto loginDto = ctx.bodyAsClass(LoginDto.class);
        User user = dao.verifyUser(loginDto.username(), loginDto.password());

        // TODO: get roles from user and transform to a String like this: "[ADMIN, USER]"
        String roles = user.getRoleList().stream().map(role -> role.getRoleName().toString()).collect(Collectors.joining(", ", "[", "]"));

        System.out.println(roles);
        System.out.println(user.getUsername());
        // TODO: create token
        ClaimBuilder claimBuilder = ClaimBuilder.builder()
                .issuer("lyngby")
                .audience("datamatiker")
                .claimSet(Map.of("username", user.getUsername(), "roles", roles))
                .expirationTime(3600000L)
                .issueTime(3600000L)
                .build();

        String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");

        ctx.status(200);
        ctx.json(createResponseObject(user.getUsername(), token));
    }

    public void register(Context ctx) throws ApiException, TokenException {

        // TODO: validate user
        RegisterDto registerDto = validateUser(ctx);
        // TODO: check if user exists
        dao.checkUser(registerDto.username());
        // TODO: check if role exists
        dao.checkRoles(registerDto.roleList());
        // TODO: register user
        dao.registerUser(registerDto.username(), registerDto.password(), registerDto.roleList());

        // TODO: get roles from user and transform to a String like this: "[ADMIN, USER]"
        String roles = registerDto.roleList().stream().collect(Collectors.joining(", ", "[", "]"));

        // TODO: create token
        ClaimBuilder claimBuilder = ClaimBuilder.builder()
                .issuer("lyngby")
                .audience("datamatiker")
                .claimSet(Map.of("username", registerDto.username(), "roles", roles))
                .expirationTime(3600000L)
                .issueTime(3600000L)
                .build();

        String token = TokenFactory.createToken(claimBuilder, "841D8A6C80CBA4FCAD32D5367C18C53B");

        ctx.status(201);
        ctx.json(createResponseObject(registerDto.username(), token));
    }

    private ObjectNode createResponseObject(String userName, String token) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode respondNode = mapper.createObjectNode();
        respondNode.put("username", userName);
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
