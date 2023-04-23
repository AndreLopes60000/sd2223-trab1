package sd2223.trab1.servers.resources;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;
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

		
		// Check if user data is valid
		if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		// Insert user, checking if name already exists
		if (users.putIfAbsent(user.getName(), user) != null) {
			throw new WebApplicationException(Status.CONFLICT);
		}

		return user.getName()+"@"+user.getDomain();
	}
	
	@Override
	public User getUser(String name, String pwd) {
			
			// Check if user is valid
			if(name == null || pwd == null) {
				throw new WebApplicationException( Status.BAD_REQUEST );
			}
			
			User user = users.get(name);			
			// Check if user exists 
			if( user == null ) {
				throw new WebApplicationException( Status.NOT_FOUND );
			}
			
			//Check if the password is correct
			if( !user.getPwd().equals( pwd)) {
				throw new WebApplicationException( Status.FORBIDDEN );
			}
			
			return user;
		}

	@Override
	public User updateUser(String name, String pwd, User user) {

		// Check if user data is valid
		if(!user.getName().equals(name)) {
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		// Check if user data is valid
		if( user.getPwd() == null && user.getDisplayName() == null &&
				user.getName() == null) {
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(name);
		if(storedUser == null){
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPwd().equals(pwd)){
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		String pass =  user.getPwd();
		String dName = user.getDisplayName();
		String sName = user.getName();

		if (pass != null)
			storedUser.setPwd(pass);
		if (dName != null)
			storedUser.setDisplayName(dName);
		if (sName != null)
			storedUser.setName(sName);


		return storedUser;
	}

	@Override
	public User deleteUser(String name, String pwd) {
		// Check if user is valid
		if(name == null || pwd == null) {
			throw new WebApplicationException( Status.BAD_REQUEST );
		}
		// Check if user exists
		User storedUser = users.get(name);
		if(storedUser == null){
			throw new WebApplicationException( Status.NOT_FOUND );
		}
		//Check if the password is correct
		if(!storedUser.getPwd().equals(pwd)){
			throw new WebApplicationException( Status.FORBIDDEN );
		}
		return users.remove(name);
	}

	@Override
	public List<User> searchUsers(String pattern) {
		if(pattern == null){
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		List<User> usersFound = new ArrayList<>();
		if(users.isEmpty())
			return usersFound;
		Predicate<User> isNull = obj -> obj == null;
		Collection<User> usersCollection = users.values();
		usersCollection.removeIf(isNull);
		User[] allUsers = usersCollection.toArray(new User[usersCollection.size()]);
		if(pattern.equals("")){
			for (User u: allUsers) {
				if(u != null) {
					User newUser = new User(u.getName(), "", u.getDomain(), u.getDisplayName());
					usersFound.add(newUser);
				}
			}
			return usersFound;
		}

		for (User u: allUsers) {
			if(u != null)
			if(u.getName().toLowerCase().contains(pattern.toLowerCase())) {
				User newUser = new User(u.getName(),"",u.getDomain(), u.getDisplayName());
				usersFound.add(newUser);
			}
		}
		return usersFound;
	}
}
