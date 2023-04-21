package sd2223.trab1.clients;

import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;

import java.util.List;

public interface RestUser {

    Result<String> createUser(User user);

    Result<User> getUser(String name, String pwd);

    Result<User> updateUser(String name, String pwd, User user);

    Result<User> deleteUser(String name, String pwd);

    Result<List<User>> searchUsers(String pattern);

}
