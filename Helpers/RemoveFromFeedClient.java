package sd2223.trab1.clients.Feeds;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.clients.RestFeedClient;
import sd2223.trab1.servers.FeedsServer;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class RemoveFromFeedClient {
    private static Logger Log = Logger.getLogger(RemoveFromFeedClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if( args.length != 3) {
            System.err.println( "Use: java sd2223.trab1.clients.Feeds.RemoveFromFeedClient user mid pwd");
            return;
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(FeedsServer.SERVICE, 1);
        String serverUrl = uris[0].toString();

        String user = args[0];
        long mid = Long.parseLong(args[1]);
        String pwd = args[2];

        Log.info("Sending request to server.");

       new RestFeedClient(URI.create(serverUrl)).removeFromPersonalFeed(user, mid, pwd);

    }
}