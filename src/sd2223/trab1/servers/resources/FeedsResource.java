package sd2223.trab1.servers.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.api.Discovery;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.rest.FeedsService;
import sd2223.trab1.clients.RestFeedClient;
import sd2223.trab1.clients.RestUsersClient;
import sd2223.trab1.servers.UsersServer;


import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

@Singleton
public class FeedsResource implements FeedsService {

    private static int num_seq = 0;
    private static final String SERVER_URI_FMT = "%s:%s";
    private static final String USERS_SERVICE = "users";
    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());
    private final Map<String, List<Message>> usersMessages = new HashMap<>();
    private final Map<String,List<String>> usersSubs = new HashMap<>();

    //private final Map<String, Pair> cachedMessages= new HashMap<>();
    //private final List<Long> messagesIDs = new ArrayList<Long>();
    public FeedsResource() {
    }
    @Override
    public long postMessage(String user, String pwd, Message msg) {
        System.out.println("ai ui");

        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        //User domain n esta a ser utilizado
        String messageDomain = msg.getDomain();
        Discovery discovery = Discovery.getInstance();
        URI uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
        var result = new RestUsersClient(uri).getUser(name, pwd);
        if(result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if(result.error().equals(Result.ErrorCode.FORBIDDEN)){
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }
        if(user == null || pwd == null || msg == null){
            Log.info("Null input");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        List<Message> messages = usersMessages.get(name);
        if(messages == null){
            messages = new ArrayList<>();
            messages.add(msg);
            usersMessages.put(name, messages);
        }
        else
            messages.add(msg);
        msg.setId(this.getId());

        return msg.getId();
    }

    private long getId() {
        num_seq++;
        return (long) num_seq * 256;
        //return (long) num_seq * 256 + serverBase;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (!domain.equals(userDomain)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
        if(result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if(result.error().equals(Result.ErrorCode.FORBIDDEN)){
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }

        List<Message> messages = usersMessages.get(name);
        if (messages == null) {
            Log.info("Message does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Message m = getMsgFromList(mid, messages);
        if (m == null) {
            Log.info("Message does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        messages.remove(m);

    }
    private Message getMsgFromList(long mid, List<Message> messages) {
        for (Message m : messages) {
            if (mid == m.getId()){
                return m;
            }
        }
        return null;
    }


    @Override
    public Message getMessage(String user, long mid) {
        String name = user.split("@")[0];
        String userDomain = user.split("@")[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        if(domain.equals(userDomain)){
            List<Message> messages = usersMessages.get(name);
            if (messages == null) {
                Log.info("Message does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            Message m = getMsgFromList(mid, messages);
            if (m == null) {
                Log.info("Message does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return m;
        }

        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + userDomain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestFeedClient(URI.create(serverUrl)).getMessage(user, mid);

        return result.value();
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Discovery discovery = Discovery.getInstance();

        if (!domain.equals(userDomain)) {
            URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + userDomain, 1);
            String serverUrl = uris[0].toString();

            //TODO
            //Checkar caso dê merda

            return new RestFeedClient(URI.create(serverUrl)).getMessages(user, time).value();
        }

        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, "");
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<String> subbedUsers = usersSubs.get(name);
        //List<Message> userFeed = this.getPersonalFeed(user);
        List<Message> result2;
        for (String sub:subbedUsers) {
            domain = sub.split("@")[1];
            uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
            serverUrl = uris[0].toString();
            result2 = new RestFeedClient(URI.create(serverUrl)).getPersonalFeed(user, time).value();
            //userFeed.addAll(result2);
        }
        //return userFeed;
        return null;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (!domain.equals(userDomain)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (result.error().equals(Result.ErrorCode.FORBIDDEN)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        String[] nameAndDomainSub = userSub.split("@");
        String nameSub = nameAndDomainSub[0];
        String userSubDomain = nameAndDomainSub[1];
        uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + userSubDomain, 1);
        serverUrl = uris[0].toString();
        result = new RestUsersClient(URI.create(serverUrl)).getUser(nameSub, "");
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User to be subscribed does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<String> subs = usersSubs.get(name);
        if(subs == null){
            subs = new ArrayList<>();
            subs.add(userSub);
            usersSubs.put(name, subs);
        }
        else if (!subs.contains(userSub))
            subs.add(userSub);

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        if (!domain.equals(userDomain)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, pwd);
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (result.error().equals(Result.ErrorCode.FORBIDDEN)) {
            Log.info("Password is incorrect.");
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

        String[] nameAndDomainSub = userSub.split("@");
        String nameSub = nameAndDomainSub[0];
        String userSubDomain = nameAndDomainSub[1];
        uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + userSubDomain, 1);
        serverUrl = uris[0].toString();
        result = new RestUsersClient(URI.create(serverUrl)).getUser(nameSub, "");
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User to be unsubscribed does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<String> subs = usersSubs.get(name);
        if(subs != null){
            subs.remove(userSub);
            if(subs.isEmpty())
                usersSubs.remove(name);
        }

    }

    @Override
    public List<String> listSubs(String user) {
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        if (!domain.equals(userDomain)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Discovery discovery = Discovery.getInstance();
        URI[] uris = discovery.knownUrisOf(UsersServer.SERVICE + "/" + domain, 1);
        String serverUrl = uris[0].toString();

        var result = new RestUsersClient(URI.create(serverUrl)).getUser(name, "");
        if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        List<String> subbedUsers = usersSubs.get(name);
        if(subbedUsers == null)
            return new ArrayList<>();
        else
            return subbedUsers;
    }

    /*
    @Override
    public List<Message> getPersonalFeed(String user) {
        return usersMessages.get(user);
    }

     */


}
