package sd2223.trab1.clients.Feeds;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.clients.RestFeedClient;
import sd2223.trab1.servers.FeedsServer;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class ListSubsClient {
    private static Logger Log = Logger.getLogger(ListSubsClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if( args.length != 1) {
            System.err.println( "Use: java sd2223.trab1.clients.Feeds.ListSubsClient user");
            return;
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(FeedsServer.SERVICE, 1);
        String serverUrl = uris[0].toString();

        String user = args[0];

        Log.info("Sending request to server.");

        var result = new RestFeedClient(URI.create(serverUrl)).listSubs(user);
        for(String u : result)
            System.out.println("Result: " + u);

    }

}
