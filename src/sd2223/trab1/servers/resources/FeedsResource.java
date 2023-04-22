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
    private static final String FEEDS_SERVICE = "feeds";
    private static final String FOLLOWER = "follower";
    private static Logger Log = Logger.getLogger(FeedsResource.class.getName());
    private final Map<String, List<Message>> usersMessages = new HashMap<>();
    private final Map<String,List<String>> usersSubs = new HashMap<>();
    private final Map<String, List<String>> userFollowers = new HashMap<>();

    public FeedsResource() {
    }
    @Override
    public long postMessage(String user, String pwd, Message msg) {
        System.out.println("estou no post message");

        if(user == null || pwd == null || msg == null){
            Log.info("Null input");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        Discovery discovery = Discovery.getInstance();
        URI uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
        var result = new RestUsersClient(uri).getUser(name, pwd);

        if(result.error().equals(Result.ErrorCode.NOT_FOUND)) {
            Log.info("User does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        if(!pwd.equals(FOLLOWER) && result.error().equals(Result.ErrorCode.FORBIDDEN)){
            Log.info("Password is incorrect.");
            throw new WebApplicationException( Response.Status.FORBIDDEN );
        }

        List<Message> messages = usersMessages.get(name);
        if (messages == null) {
            messages = new ArrayList<>();
            messages.add(msg);
            usersMessages.put(name, messages);
        } else
            messages.add(msg);
        msg.setId(this.getId());

        if(name.equals(msg.getUser())){
            List<String> followers = userFollowers.get(name);
            if(followers != null){
                for (String follower: followers) {
                    String followerDomain = follower.split("@")[1];
                    uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, followerDomain, FEEDS_SERVICE), 1)[0];
                    new RestFeedClient(uri).postMessage(follower, FOLLOWER, msg);
                }
            }
        }

        return msg.getId();
    }

    private long getId() {
        num_seq++;
        return (long) num_seq * 256;
        //return (long) num_seq * 256 + serverBase;
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        System.out.println("estou no remove from personal feed");
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
        System.out.println("estou no get message");
        String name = user.split("@")[0];
        String userDomain = user.split("@")[1];
        String domain = "";
        URI uri = null;
        Discovery discovery = Discovery.getInstance();
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        if(domain.equals(userDomain)){
            uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
            var result = new RestUsersClient(uri).getUser(name, "");
            if(result.error().equals(Result.ErrorCode.NOT_FOUND)){
                Log.info("User does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
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
            return m;
        }

        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, FEEDS_SERVICE), 1)[0];
        var result = new RestFeedClient(uri).getMessage(user, mid);

        return result.value();
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        System.out.println("estou no get messages");
        String[] nameAndDomain = user.split("@");
        String name = nameAndDomain[0];
        String userDomain = nameAndDomain[1];
        String domain = "";
        URI uri = null;
        Discovery discovery = Discovery.getInstance();
        try {
            domain = InetAddress.getLocalHost().getHostName().split("\\.")[1];

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        if(domain.equals(userDomain)) {
            uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
            var result = new RestUsersClient(uri).getUser(name, "");
            if (result.error().equals(Result.ErrorCode.NOT_FOUND)) {
                Log.info("User does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            Set<String> usersWithSubs = usersSubs.keySet();
            Set<String> usersWithFollowers = userFollowers.keySet();
            for(String user1:usersWithSubs){
                System.out.println("Sou o user "+ user1+" e sigo:");
                List<String> subs = usersSubs.get(user1);
                for (String sub: subs)
                    System.out.println(sub);
            }
            for(String user2 : usersWithFollowers){
                System.out.println("Sou o user "+ user2+" e os meus followers sao:");
                List<String> subs = userFollowers.get(user2);
                for (String sub: subs)
                    System.out.println(sub);
            }

            return this.getMessages(usersMessages.get(name),time);
        }






        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, FEEDS_SERVICE), 1)[0];
        var result = new RestFeedClient(uri).getMessages(user, time);
        return result.value();

    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        System.out.println("estou no sub user");
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
        URI uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
        var result = new RestUsersClient(uri).getUser(name, pwd);

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
        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userSubDomain, USERS_SERVICE), 1)[0];
        result = new RestUsersClient(uri).getUser(nameSub, "");
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

        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userSubDomain, USERS_SERVICE), 1)[0];
        new RestFeedClient(uri).setFollower(nameSub, user);

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        System.out.println("estou no unsub user");
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
        URI uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];
        var result = new RestUsersClient(uri).getUser(name, pwd);

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
        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userSubDomain, USERS_SERVICE), 1)[0];
        result = new RestUsersClient(uri).getUser(nameSub, "");
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
        uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userSubDomain, USERS_SERVICE), 1)[0];
        new RestFeedClient(uri).removeFollower(nameSub, user);

    }

    @Override
    public List<String> listSubs(String user) {
        System.out.println("estou no list subs");
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
        URI uri = discovery.knownUrisOf(String.format(SERVER_URI_FMT, userDomain, USERS_SERVICE), 1)[0];

        var result = new RestUsersClient(uri).getUser(name, "");
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

    @Override
    public void setFollower(String userName, String follower) {
        System.out.println("Penis1");
        List<String> followers = userFollowers.get(userName);
        if(followers==null) {
            followers = new ArrayList<>();
            userFollowers.put(userName, followers);
        }
        followers.add(follower);
    }

    @Override
    public void removeFollower(String userName, String follower) {
        System.out.println("Penis2");
        List<String> followers = userFollowers.get(userName);
        if(followers != null)
            followers.remove(follower);
    }

    private List<Message> getMessages(List<Message> messages, long time){
        List<Message> messagesToReturn = new ArrayList<>();
        if(time == 0)
            return messages;
        if(messages == null || messages.size() == 0)
            return messagesToReturn;
        for(Message m: messages){
            if(m.getCreationTime() < time)
                messagesToReturn.add(m);
        }
        return messagesToReturn;
    }


}
