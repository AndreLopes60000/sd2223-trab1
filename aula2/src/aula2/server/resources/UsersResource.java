package aula2.server.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import aula2.api.User;
import aula2.api.service.RestUsers;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class UsersResource implements RestUsers {

	private final Map<String,User> users = new HashMap<>();

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());
	
	public UsersResource() {
	}
		
	@Override
	public String createUser(User user) {
		Log.info("createUser : " + user);
		
		// Check if user data is valid
		if(user.getUserId() == null || user.getPassword() == null || user.getFullName() == null || 
				user.getEmail() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		
		// Insert new user, checking if userId already exists
		if( users.putIfAbsent( user.getUserId(), user) != null ) {
			Log.info("User already exists.");
			throw new WebApplicationException( Status.CONFLICT );
		}
		return user.getUserId();
	}

	@Override
	public User getUser(String userId, String password) {
		Log.info("getUser : user = " + userId + "; pwd = " + password);
		
		// Check if user is valid
		if(userId == null || password == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		
		var user = users.get(userId);
		
		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		
		//Check if the password is correct
		if( !user.getPassword().equals( password)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		
		return user;
	}

	@Override
	public User updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; user = " + user);
		// TODO Complete method

		// Check if user data is valid
		if(user.getUserId() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		// Check if user data is valid
		if( user.getPassword() == null && user.getFullName() == null &&
				user.getEmail() == null) {
			Log.info("Empty User object.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(userId);
		if(storedUser == null){
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPassword().equals(password)){
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		String email = user.getEmail();
		String pass =  user.getPassword();
		String fName = user.getFullName();
		if (email != null)
			storedUser.setEmail(email);
		if (pass != null)
			storedUser.setPassword(pass);
		if (fName != null)
			storedUser.setFullName(fName);

		return storedUser;
	}

	@Override
	public User deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);
		// TODO Complete method
		// Check if user is valid
		if(userId == null || password == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(userId);
		if(storedUser == null){
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPassword().equals(password)){
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		return users.remove(userId);
	}


	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		// TODO Complete method
		if(pattern == null){
			Log.info("Pattern null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		List<User> usersFound = new ArrayList<>();
		if(users.isEmpty())
			return usersFound;
		User[] allUsers = users.values().toArray(new User[users.size()]);
		if(pattern.equals("")){
			for (User u: allUsers) {
				User newUser = new User(u.getUserId(), u.getFullName(), u.getEmail(), "");
				usersFound.add(newUser);
			}
			return usersFound;
		}
		for (User u: allUsers) {
			if(u.getFullName().toLowerCase().contains(pattern.toLowerCase())) {
				User newUser = new User(u.getUserId(), u.getFullName(), u.getEmail(), "");
				usersFound.add(newUser);
			}
		}
		return usersFound;
	}

}
