package sd2223.trab1.servers.resources;

import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.clients.RestUsersClient;
import sd2223.trab1.servers.UsersServer;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedsResource implements FeedsService {

    private final Map<String, List<Message>> usersMessages = new HashMap<>();
    private final Map<String,List<String>> usersSubs = new HashMap<>();
    @Override
    public long postMessage(String user, String pwd, Message msg) {
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE, 1);
        String serverUrl = uris[0].toString();

        String name = args[0];
        String password = args[1];

        Log.info("Sending request to server.");

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, password);
        System.out.println("Result: " + result);




        return 0;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {

    }

    @Override
    public Message getMessage(String user, long mid) {
        return null;
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return null;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(String user) {
        return null;
    }
}
