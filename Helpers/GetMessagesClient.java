package sd2223.trab1.clients.Feeds;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.clients.RestFeedClient;
import sd2223.trab1.servers.FeedsServer;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetMessagesClient {
    private static Logger Log = Logger.getLogger(GetMessagesClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if( args.length != 2) {
            System.err.println( "Use: java sd2223.trab1.clients.Feeds.GetMessagesClient user time");
            return;
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(FeedsServer.SERVICE, 1);
        String serverUrl = uris[0].toString();

        String user = args[0];
        long time = Long.parseLong(args[1]);

        Log.info("Sending request to server.");

        var result = new RestFeedClient(URI.create(serverUrl)).getMessages(user, time);
        for(Message m : result)
            System.out.println("Result: " + m.getId());

    }
}
