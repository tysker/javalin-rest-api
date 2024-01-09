package dk.lyngby.routes;

public enum RouteRoles implements io.javalin.security.RouteRole {
    ANYONE("anyone"), USER("user"), ADMIN("admin");

    private final String role;

    RouteRoles(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

}
