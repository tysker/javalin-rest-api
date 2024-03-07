package dk.lyngby.dao;

import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

public class AuthDao {

    private static AuthDao instance;
    private final EntityManagerFactory emf;

    private AuthDao(EntityManagerFactory _emf) {
        this.emf = _emf;
    }

    public static AuthDao getInstance(EntityManagerFactory _emf) {
        synchronized (AuthDao.class) {
            if (instance == null) {
                instance = new AuthDao(_emf);
            }
            return instance;
        }
    }

    public void registerUser(String username, String password, Set<String> roleList) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // TODO: create user and add roles
            User user = new User(username, password);
            Set<Role> roles = user.getRoleList();

            for (String roleName : roleList) {
                Role role = em.find(Role.class, Role.RoleName.valueOf(roleName));
                if (role == null) {
                    role = new Role(Role.RoleName.valueOf(roleName));
                    em.persist(role);
                }
                roles.add(new Role(Role.RoleName.valueOf(roleName)));
            }

            user.setRoleList(roles);

            em.persist(user);
            em.getTransaction().commit();
        }
    }

    public void checkUser(String username) throws ApiException {

        try (var em = emf.createEntityManager()) {
            User user = em.find(User.class, username);

            if (user != null) throw new ApiException(400, "User already exists");
        }
    }

    public void checkRoles(Set<String> roleList) throws ApiException {
        Role.RoleName[] roleNames = Role.RoleName.values();

        for (String roleName : roleList) {
            boolean roleExists = false;
            for (Role.RoleName role : roleNames) {
                if (roleName.equals(role.toString())) {
                    roleExists = true;
                    break;
                }
            }
            if (!roleExists) throw new ApiException(400, "Role does not exist");
        }
    }

    public User verifyUser(String username, String password) throws AuthorizationException {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, username);

            if (user == null || !user.verifyPassword(password)) {
                throw new AuthorizationException(401, "Invalid user name or password");
            }
            em.getTransaction().commit();
            return user;
        }
    }

    public User getUser(String username) throws ApiException {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, username);

            if (user == null) {
                throw new ApiException(401, "Invalid user");
            }
            em.getTransaction().commit();
            return user;
        }
    }
}
