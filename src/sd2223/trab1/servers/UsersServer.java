package sd2223.trab1.servers;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.servers.resources.UsersResource;
import sd2223.trab1.api.Discovery;

public class UsersServer {

	private static Logger Log = Logger.getLogger(UsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	public static final String SERVICE = "users";
	private static final String DELIMITER = "\t";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		try {

			ResourceConfig config = new ResourceConfig();
			config.register(UsersResource.class);
			// config.register(CustomLoggingFilter.class);

			String domain = args[0];
			int num = Integer.parseInt(args[1]);

			String ip = InetAddress.getLocalHost().getHostAddress();
			String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

			String message = domain+":"+SERVICE+DELIMITER+serverURI;
			Discovery discovery = Discovery.getInstance();
			discovery.announce(message);

			JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);


			Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

			// More code can be executed here...
		} catch (Exception e) {
			Log.severe(e.getMessage());
		}
	}
}
