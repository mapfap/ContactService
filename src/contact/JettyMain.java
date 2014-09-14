package contact;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

/**
 * 
 * 
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class JettyMain {
	
	static final int PORT = 8080;

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * 
	 * @param args not used
	 * @throws Exception if Jetty server encounters any problem
	 */
	public static void main(String[] args) throws Exception {
		int port = PORT;
		Server server = new Server( port );
		
		// Options are: SESSIONS, NO_SESSIONS, SECURITY, NO_SECURITY
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		context.setContextPath("/");
		
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "contact.resource");
		context.addServlet( holder, "/*" );
		
		server.setHandler( context );
		
		System.out.println("Starting Jetty server on port " + port);
		server.start();
		
		System.out.println("Server started.  Press ENTER to exit.");
		
		System.in.read();
		System.out.println("Stopping server.");
		server.stop();
	}
	
}

