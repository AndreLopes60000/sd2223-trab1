package aula1;

import java.net.* ;
import java.util.*;
import aula1.Discovery;

/**
 * Basic TCP client... 
 *
 */
public class TcpClient {
    
	private static final String QUIT = "!quit";
	private static final String initServiceName = "juan";

	public static void main(String[] args) throws Exception {
        
    	// Use Discovery to obtain the hostname and port of the server;
    	var port = -1;
    	var hostname = "";
		Discovery discovery = aula1.Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(initServiceName, 1);
		String uri = uris[0].toString();
		String [] url = uri.split("/");
		String [] subUrl = url[3].split(":");
		hostname = subUrl[0];
		port = Integer.parseInt(subUrl[1]);

    	try( var cs = new Socket( hostname, port); var sc = new Scanner(System.in)) {
    		String input;
    		do {
    			input = sc.nextLine();
    			cs.getOutputStream().write( (input + System.lineSeparator()).getBytes() );
    		} while( ! input.equals(QUIT));
    		
    	}
    }  
}
