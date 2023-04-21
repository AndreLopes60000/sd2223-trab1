package sd2223.trab1.clients;

import jakarta.ws.rs.client.WebTarget;
import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
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

public class RestFeedClient extends RestClient implements RestFeed {
    final WebTarget target;

    public RestFeedClient(URI serverURI) {
        super(serverURI);
        target =  client.target( serverURI ).path( FeedsService.PATH);
    }

    private Result<Long> clt_postMessage(String user, String pwd, Message msg) {
        Response r = target.path(user)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(msg, MediaType.APPLICATION_JSON));

        return toJavaResult(r, Long.class);
    }

    private Result<Void> clt_removeFromPersonalFeed(String user, long mid, String pwd) {
        Response r = target.path(user+"/"+mid)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        return toJavaResult(r, Void.class);
    }

    private Result<Message> clt_getMessage(String user, long mid) {
        Response r = target.path( user+"/"+mid ).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        return toJavaResult(r, Message.class);
    }

    private Result<List<Message>> clt_getMessages(String user, long time) {
        Response r = target.path(user+"/"+time).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        return toJavaResult(r, new GenericType<List<Message>>() {});
    }

    private Result<Void> clt_subUser(String user, String userSub, String pwd) {
        Response r = target.path("/sub/"+user+"/"+userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(userSub, MediaType.APPLICATION_JSON));

        return toJavaResult(r, Void.class);
    }

    private Result<Void> clt_unsubscribeUser(String user, String userSub, String pwd) {
        Response r = target.path("/sub/"+user+"/"+userSub)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        return toJavaResult(r, Void.class);
    }

    private Result<List<String>> clt_listSubs(String user) {
        Response r = target.path("/sub/list/+"+user).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
        return toJavaResult(r, new GenericType<List<String>>(){});
    }

    private Result<List<Message>> clt_getPersonalFeed(String user, long time) {
            Response r = target.path("/"+user+"/"+time)
                    .request().accept(MediaType.APPLICATION_JSON)
                    .get();

        return toJavaResult(r, new GenericType<List<Message>>(){});
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        return super.reTry( () -> clt_postMessage(user,pwd,msg));
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
        return super.reTry( () -> clt_removeFromPersonalFeed(user,mid,pwd));
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        return super.reTry( () -> clt_getMessage(user,mid) );
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        return super.reTry( () -> clt_getMessages(user,time) );
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return super.reTry( () -> clt_subUser(user,userSub, pwd));
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return super.reTry(() -> clt_unsubscribeUser(user,userSub,pwd));
    }

    @Override
    public  Result<List<String>> listSubs(String user) {
        return super.reTry( () -> clt_listSubs(user) );
    }

    @Override
    public Result<List<Message>> getPersonalFeed(String user, long time) {
        return super.reTry( () -> clt_getPersonalFeed(user, time));
    }

}
