package sd2223.trab1.servers;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.servers.resources.FeedsResource;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

    public class FeedsServer {
    private static Logger Log = Logger.getLogger(UsersServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "feeds";
    private static final String DELIMITER = "\t";
    private static final String SERVER_IP_FMT = "http://%s:%s/rest";
    private static final String SERVER_URI_FMT = "%s:%s";

        public static void main(String[] args) {
            try {
                String domain = args[0];
                int serverBase = Integer.parseInt(args[1]);

                ResourceConfig config = new ResourceConfig();
                config.register(FeedsResource.class);
                // config.register(CustomLoggingFilter.class);

                String ip = InetAddress.getLocalHost().getHostAddress();
                String serverURI = String.format(SERVER_URI_FMT, domain, SERVICE);
                String serverIP = String.format(SERVER_IP_FMT, ip, PORT);

                String message = serverIP+DELIMITER+serverURI;
                Discovery discovery = Discovery.getInstance();
                discovery.announce(message);

                JdkHttpServerFactory.createHttpServer(URI.create(serverIP), config);

                Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

                // More code can be executed here...
            } catch (Exception e) {
                Log.severe(e.getMessage());
            }
        }


}
