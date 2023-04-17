package aula2.clients;

import java.io.IOException;

import aula2.api.User;

public class UpdateUserClient {

	public static void main(String[] args) throws IOException {
		
		if( args.length != 6) {
			System.err.println( "Use: java aula2.clients.UpdateUserClient url userId oldpwd fullName email password");
			return;
		}
		
		String serverUrl = args[0];
		String userId = args[1];
		String oldpwd = args[2];
		String fullName = args[3];
		String email = args[4];
		String password = args[5];
		
		var u = new User( userId, fullName, email, password);

		System.out.println("Sending request to server.");
		//TODO complete this client code
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);

		WebTarget target = client.target( serverUrl ).path( RestUsers.PATH );

		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, oldpwd).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(u, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
			System.out.println("Success, updated user with id: " + r.readEntity(String.class) );
		else
			System.out.println("Error, HTTP error status: " + r.getStatus() );



	}
	
}
