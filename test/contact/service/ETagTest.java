package contact.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.net.URI;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;
import contact.entity.Contact;

public class ETagTest {
	
	private static final int TEST_PORT = 28080;
	private static URI uri;
	private static Contact contact1;
	private static Contact contact2;
	private static Contact contact3;
	private ContactDao contactDao = DaoFactory.getInstance().getContactDao();
	private HttpClient client;

	/**
	 * Start the service.
	 */
	@BeforeClass
	public static void doFirst( ) {
		uri = JettyMain.startServer( TEST_PORT );
		contact1 = new Contact( "contact1", "Joe Contact", "joe@microsoft.com", "0123456789" );
		contact2 = new Contact( 1456, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contact3 = new Contact( 4455, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
	}

	/**
	 * Stop the service.
	 */
	@AfterClass
	public static void doLast( ) {
		JettyMain.stopServer();
	}

	/**
	 * Clear persistence data and instantiate a new client before every testcases.
	 * so each test are absolutely independent.
	 */
	@Before
	public void beforeTest() {
		contactDao.removeAll();

		client = new HttpClient();
		try {
			client.start();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the client,
	 * help the server load on abundant client connections.
	 */
	@After
	public void afterTest() {
		try {
			client.stop();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Marshal the specified contact to XML String.
	 * @param contact contact to be parse.
	 * @return String of XML contact.
	 */
	private String marshal( Contact contact ) {
		StringWriter writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance( Contact.class );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( contact, writer );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}

		return writer.toString();
	}

	private long postContact( Contact contact ) {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse response = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), response.getStatus() );
			String location = response.getHeaders().get("Location");
			String[] splited = location.split("/");
			String id = splited[ splited.length - 1 ];
			return Long.parseLong( id );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Test
	public void testETagFromGet() {
		long id = postContact( contact1 );
		
		try {
			ContentResponse response = client.GET( uri + "contacts/" + id);
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
			String etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag != null);
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testETagFromPost() {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact1 ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse response = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), response.getStatus() );
			
			String etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag != null);
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testETagFromPut() {
		long id = postContact( contact2 );
		
		contact2.setName("CHANGED!");
		
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact2 ) );	
			Request request = client.newRequest( uri + "contacts/" + id ).content( content, "application/xml" ).method( HttpMethod.PUT );
			ContentResponse response = request.send();
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
			
			String etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag != null);
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testIfNonMatch() {
		long id = postContact( contact1 );
		
		try {
			ContentResponse response = client.GET( uri + "contacts/" + id);
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
			String etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag != null);
			
			Request request2 = client.newRequest( uri + "contacts/" + id ).header( HttpHeader.IF_NONE_MATCH , etag).method( HttpMethod.GET );
			ContentResponse response2 = request2.send();
			assertEquals( "Get the old one, should return NOT MODIFIED", Status.NOT_MODIFIED.getStatusCode(), response2.getStatus() );
			
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
