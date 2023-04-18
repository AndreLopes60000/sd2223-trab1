package sd2223.trab1.clients;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.servers.UsersServer;

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
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
		String serverUrl = uris[0].toString();

		String pattern = args[0];


		Log.info("Sending request to server.");

		new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);

	}

}
