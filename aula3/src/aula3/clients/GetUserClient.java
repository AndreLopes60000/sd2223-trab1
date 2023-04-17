package aula2.clients;

import java.io.IOException;

import org.glassfish.jersey.client.ClientConfig;
import aula2.api.User;
import aula2.api.service.RestUsers;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

public class GetUserClient {


	public static void main(String[] args) throws IOException {
		
		if( args.length != 3) {
			System.err.println( "Use: java aula2.clients.GetUserClient url userId password");
			return;
		}
		
		String serverUrl = args[0];
		String userId = args[1];
		String password = args[2];
		
		System.out.println("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).getUser(userId, password);
		System.out.println("Result: " + result);

	}
	
}
