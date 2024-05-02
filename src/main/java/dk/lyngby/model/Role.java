package dk.lyngby.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @ManyToMany(mappedBy = "roleList")
    @ToString.Exclude
    private Set<User> userList = new HashSet<>();

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }

    public enum RoleName implements io.javalin.security.RouteRole{
        USER, SUPERVISOR, ADMIN, MANAGER, ANYONE;

    }
}
