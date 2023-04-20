package sd2223.trab1.clients.Users;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.clients.RestUsersClient;
import sd2223.trab1.servers.UsersServer;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class DeleteUserClient {
	private static Logger Log = Logger.getLogger(DeleteUserClient.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	public static void main(String[] args) throws IOException {
		
		if( args.length != 2) {
			System.err.println( "Use: java sd2223.trab1.clients.Users.DeleteUserClient name pwd");
			return;
		}
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
		String serverUrl = uris[0].toString();

		String name = args[0];
		String password = args[1];
		
		Log.info("Sending request to server.");
		//TODO complete this client code
		var result = new RestUsersClient(URI.create(serverUrl)).deleteUser(name, password);
		System.out.println("Result: " + result);

	}
	
}
