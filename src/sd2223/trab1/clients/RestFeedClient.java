package sd2223.trab1.clients;

import jakarta.ws.rs.client.WebTarget;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.FeedsService;

import java.net.URI;
import java.util.List;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.rest.FeedsService;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import sd2223.trab1.api.rest.UsersService;
import sd2223.trab1.clients.Users.GetUserClient;

public class RestFeedClient extends RestClient implements FeedsService {
    final WebTarget target;

    public RestFeedClient(URI serverURI) {
        super(serverURI);
        target =  client.target( serverURI ).path( FeedsService.PATH);
    }

    private long clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));


        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
            return r.readEntity(long.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return 0;
    }

    private void clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path(user+"/"+mid)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
            System.out.println("Message deleted: " + r.readEntity(Message.class));
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private Message clt_getMessage(String user, long mid) {

        Response r = target.path( user+"/"+mid ).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
            return r.readEntity(Message.class);
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    private List<Message> clt_getMessages(String user, long time) {
    }

    private List <String> clt_listSubs(String user) {
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry( () -> clt_postMessage(user,pwd,msg) );
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        super.reTry( () -> clt_removeFromPersonalFeed(user,mid,pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {
        return super.reTry( () -> clt_getMessage(user,mid) );
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return super.reTry( () -> clt_getMessages(user,time) );
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<String> listSubs(String user) {
        return super.reTry( () -> clt_listSubs(user) );
    }
}
