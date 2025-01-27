package aula3.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import aula3.api.Discovery;
import aula3.api.User;
import aula3.server.UsersServer;

public class UpdateUserClient {
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	public static void main(String[] args) throws IOException {
		
		if( args.length != 6) {
			System.err.println( "Use: java aula3.clients.UpdateUserClient name oldpwd displayName domain password");
			return;
		}
		Discovery discovery = aula3.api.Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
		String serverUrl = uris[0].toString();

		String name = args[0];
		String oldpwd = args[1];
		String displayName = args[2];
		String domain = args[3];
		String pwd = args[4];
		
		var u = new User( name, pwd, domain, displayName);

		Log.info("Sending request to server.");
		//TODO complete this client code
		var result = new RestUsersClient(URI.create(serverUrl)).updateUser(name,oldpwd,u);
		System.out.println("Result: " + result);

	}
	
}
