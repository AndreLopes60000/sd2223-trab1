package sd2223.trab1.clients;

import sd2223.trab1.servers.UsersServer;
import sd2223.trab1.api.Discovery;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetUserClient {
	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java aula2.clients.GetUserClient name password");
			return;
		}
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
		String serverUrl = uris[0].toString();

		String name = args[0];
		String password = args[1];

		Log.info("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, password);
		System.out.println("Result: " + result);

	}
	
}
