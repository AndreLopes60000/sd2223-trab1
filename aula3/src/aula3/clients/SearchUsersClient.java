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
		
		if (args.length != 1) {
			System.err.println("Use: java aula3.clients.SearchUsersClient pattern ");
			return;
		}
		Discovery discovery = aula1.Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(initServiceName, 1);
		String serverUrl = uris[0].toString();
		//String serverUrl = args[0];
		String pattern = args[0];


		Log.info("Sending request to server.");

		new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);

	}

}
