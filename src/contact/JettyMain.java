package contact;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

/**
 * 
 * RESTful web service using Jetty server on the specified port.
 * 
 * @author mapfap - Sarun Wongtanakarn
 *
 */
public class JettyMain {

	static final int PORT = 8080;
	private static Server server;

	/**
	 * Start server.
	 * @param args not used
	 */
	public static void main( String[] args ) {
		startServer( PORT );
	}

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * 
	 * @param port running port of server.
	 */
	public static void startServer( int port ) {
		try {
			server = new Server( port );
	
			// Options are: SESSIONS, NO_SESSIONS, SECURITY, NO_SECURITY
			ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
			context.setContextPath("/");
	
			ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
	
			holder.setInitParameter( ServerProperties.PROVIDER_PACKAGES, "contact.resource" );
			context.addServlet( holder, "/*" );
	
			server.setHandler( context );
	
			System.out.println( "Starting Jetty server on port " + port );
			server.start();
		} catch( Exception ex ) {
			ex.printStackTrace();
		}

	}
	
	public static void waitForExit() {
		System.out.println("Server started.  Press ENTER to exit.");
		try {
			System.in.read();
			System.out.println("Stopping server.");
			stopServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopServer() {
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

