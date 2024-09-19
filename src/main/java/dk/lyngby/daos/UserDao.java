package dk.lyngby.dao;

import dk.lyngby.exceptions.AuthorizationException;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserDao {

    private static UserDao instance;

    private static SessionFactory sessionFactory;

    private UserDao() {}

    public static UserDao getInstance(SessionFactory _sessionFactory) {
        if (instance == null) {
            sessionFactory = _sessionFactory;
            instance = new UserDao();
        }
        return instance;
    }

    public User getVerifiedUser(String username, String password) throws AuthorizationException {

        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            User user = session.get(User.class, username);
            if(user == null || !user.verifyPassword(password)){
                throw new AuthorizationException(401, "Invalid user name or password");
            }
            session.getTransaction().commit();
            return user;
        }
    }

    public User createUser(String username, String password, String user_role) throws AuthorizationException {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            User user = new User(username, password);
            Role role = session.get(Role.class, user_role);

            if(role == null){
                role = createRole(user_role);
            }

            user.addRole(role);
            session.persist(user);
            session.getTransaction().commit();
            return user;
        } catch (Exception e) {
            throw new AuthorizationException(400, "Username already exists");
        }
    }

    public Role createRole(String role){
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            Role newRole = new Role(role);
            session.persist(newRole);
            session.getTransaction().commit();
            return newRole;
        }
    }
}
