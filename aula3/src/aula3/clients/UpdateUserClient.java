package aula3.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import aula3.api.User;
public class UpdateUserClient {
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	public static void main(String[] args) throws IOException {
		
		if( args.length != 6) {
			System.err.println( "Use: java aula2.clients.UpdateUserClient url userId oldpwd fullName email password");
			return;
		}
		
		String serverUrl = args[0];
		String userId = args[1];
		String oldpwd = args[2];
		String fullName = args[3];
		String email = args[4];
		String password = args[5];
		
		var u = new User( userId, fullName, email, password);

		Log.info("Sending request to server.");
		//TODO complete this client code
		var result = new RestUsersClient(URI.create(serverUrl)).updateUser(userId,oldpwd,u);
		System.out.println("Result: " + result);

	}
	
}
