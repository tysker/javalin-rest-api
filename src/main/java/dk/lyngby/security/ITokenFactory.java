package dk.lyngby.security;

import dk.lyngby.dtos.UserDTO;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.exceptions.AuthorizationException;

import java.util.Set;

public interface ITokenFactory {
    String createToken(String userName, Set<String> roles) throws ApiException;
    UserDTO verifyToken(String token) throws ApiException, AuthorizationException;
    String[] parseJsonObject(String jsonString, Boolean tryLogin) throws ApiException;
}
