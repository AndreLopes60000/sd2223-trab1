package aula3.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;


public class SearchUsersClient {
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 2) {
			System.err.println("Use: java aula3.clients.SearchUsersClient url userId ");
			return;
		}

		String serverUrl = args[0];
		String userId = args[1];


		Log.info("Sending request to server.");

		new RestUsersClient(URI.create(serverUrl)).searchUsers(userId);

	}

}
