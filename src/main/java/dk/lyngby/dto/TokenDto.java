package dk.lyngby.dto;

import dk.lyngby.model.Role;

import java.util.Set;

public record TokenDto(String username, Role.RoleName[] roles) {
}
