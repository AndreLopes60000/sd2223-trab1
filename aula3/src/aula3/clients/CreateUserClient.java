package aula3.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import aula3.api.Discovery;
import aula3.api.User;
import aula3.server.UsersServer;

public class CreateUserClient {
	
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
				
		if (args.length != 4) {
			System.err.println("Use: java aula3.clients.CreateUserClient name pwd domain displayName");
			return;
		}

		Discovery discovery = aula3.api.Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
		String serverUrl = uris[0].toString();

		String name = args[0];
		String pwd = args[1];
		String domain = args[2];
		String displayName = args[3];

		User u = new User(name, pwd, domain, displayName);

		Log.info("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).createUser(u);
		System.out.println("Result: " + result);
	}

}
