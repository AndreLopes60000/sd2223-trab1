package aula3.server.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import aula3.api.User;
import aula3.api.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class UsersResource implements UsersService {

	private final Map<String,User> users = new HashMap<>();

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());
	
	public UsersResource() {
	}

	@Override
	public String createUser(User user) {
		Log.info("createUser : " + user);
		
		// Check if user data is valid
		if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		
		// Insert user, checking if name already exists
		if( users.putIfAbsent(user.getName(), user) != null ) {
			Log.info("User already exists.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		return user.getName();
	}
	
	@Override
	public User getUser(String name, String pwd) {
			Log.info("getUser : user = " + name + "; pwd = " + pwd);
			
			// Check if user is valid
			if(name == null || pwd == null) {
				Log.info("Name or Password null.");
				throw new WebApplicationException( Status.BAD_REQUEST );
			}
			
			User user = users.get(name);			
			// Check if user exists 
			if( user == null ) {
				Log.info("User does not exist.");
				throw new WebApplicationException( Status.NOT_FOUND );
			}
			
			//Check if the password is correct
			if( !user.getPwd().equals( pwd)) {
				Log.info("Password is incorrect.");
				throw new WebApplicationException( Status.FORBIDDEN );
			}
			
			return user;
		}

	@Override
	public User updateUser(String name, String pwd, User user) {
		// TODO Auto-generated method stub
		Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + user);
		// TODO Complete method

		// Check if user data is valid
		if(user.getName() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		// Check if user data is valid
		if( user.getPwd() == null && user.getDisplayName() == null &&
				user.getDomain() == null) {
			Log.info("Empty User object.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(name);
		if(storedUser == null){
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPwd().equals(pwd)){
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		String domain = user.getDomain();
		String pass =  user.getPwd();
		String dName = user.getDisplayName();
		if (domain != null)
			storedUser.setDomain(domain);
		if (pass != null)
			storedUser.setPwd(pass);
		if (dName != null)
			storedUser.setDisplayName(dName);

		return storedUser;
	}

	@Override
	public User deleteUser(String name, String pwd) {
		// TODO Auto-generated method stub
		Log.info("deleteUser : user = " + name + "; pwd = " + pwd);
		// TODO Complete method
		// Check if user is valid
		if(name == null || pwd == null) {
			Log.info("name or password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(name);
		if(storedUser == null){
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPwd().equals(pwd)){
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		return users.remove(name);
	}

	@Override
	public List<User> searchUsers(String pattern) {
		// TODO Auto-generated method stub
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
				User newUser = new User(u.getName(),"",u.getDomain(), u.getDisplayName());
				usersFound.add(newUser);
			}
			return usersFound;
		}
		for (User u: allUsers) {
			if(u.getDisplayName().toLowerCase().contains(pattern.toLowerCase())) {
				User newUser = new User(u.getName(),"",u.getDomain(), u.getDisplayName());
				usersFound.add(newUser);
			}
		}
		return usersFound;
	}
	
}
