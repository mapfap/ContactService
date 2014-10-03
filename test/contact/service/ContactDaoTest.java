package contact.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;
import contact.entity.Contact;
import contact.entity.Contacts;

/**
 * Test ContactDao, (test upon the interface)
 * Actually test this service through the HTTP request.
 * @author mapfap - Sarun Wongtakakarn
 *
 */
public class ContactDaoTest {

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
	 * Convert byte[] of HTTP Response to List of Contact entity. 
	 * @param byteArray array of byte data recieved from HTTP Response.
	 * @return List of Contact entity parsed from byteArray.
	 */
	private List<Contact> convertBytesToContactList( byte[] byteArray ) {
		InputStream bodyStream = new ByteArrayInputStream( byteArray );
		try {
			Contacts contacts = new Contacts();
			JAXBContext context = JAXBContext.newInstance( Contacts.class ) ;
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			contacts = (Contacts) unmarshaller.unmarshal( bodyStream );
			return contacts.getContacts();
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convert byte[] of HTTP Response to Contact entity. 
	 * @param byteArray array of byte data recieved from HTTP Response.
	 * @return Contact entity parsed from byteArray.
	 */
	private Contact convertBytesToContact( byte[] byteArray ) {
		InputStream bodyStream = new ByteArrayInputStream( byteArray );
		try {
			Contact contact = new Contact();
			JAXBContext context = JAXBContext.newInstance( Contact.class ) ;
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			contact = (Contact) unmarshaller.unmarshal( bodyStream );
			return contact;
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		return null;
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

	/**
	 * Get contact with specified ID.
	 * @param id ID of contact to find.
	 * @return contact with specified ID.
	 */
	private Contact getContact( long id ) {
		try {
			ContentResponse response2 = client.GET( uri + "contacts/" + id);
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response2.getStatus() );
			Contact contact = convertBytesToContact( response2.getContent() );
			return contact;
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Post to save a contact through service.
	 * @param contact contact to be saved.
	 * @return ID of contact generated from service.
	 */
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

	/**
	 * Put an update through service.
	 * @param contact contact to be updated.
	 */
	private void putContact( Contact contact ) {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact ) );
			Request request = client.newRequest( uri + "contacts/" + contact.getId() ).content( content, "application/xml" ).method( HttpMethod.PUT );
			ContentResponse response = request.send();
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete a contact with specified ID.
	 * @param id ID of contact to be delete.
	 */
	private void deleteContact( long id ) {
		try {
			Request request = client.newRequest( uri + "contacts/" + id ).method( HttpMethod.DELETE );
			ContentResponse response = request.send();
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * POST a contact, and then try GET it and check its attributes.
	 */
	@Test
	public void testSuccessPost() {
		try {
			long id = postContact( contact1 );
			Contact contact = getContact( id );
			assertEquals( "Should be the same title", contact1.getTitle(), contact.getTitle() );
			assertEquals( "Should be the same name", contact1.getName(), contact.getName() );
			assertEquals( "Should be the same email", contact1.getEmail(), contact.getEmail() );
			assertEquals( "Should be the same telephone", contact1.getPhoneNumber(), contact.getPhoneNumber() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * POST a contact with specified ID twice, so it should return 409 CONFLICT.
	 */
	@Test
	public void testFailPost() {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact2 ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse response = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), response.getStatus() );

			ContentResponse response2 = request.send();
			assertEquals( "Add Contact with existing ID, should return 409 CONFLICT", Status.CONFLICT.getStatusCode(), response2.getStatus() );

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * POST a contact, PUT some update, and then GET to check whether attributed are updated correctly.
	 */
	@Test
	public void testSuccessPut() {
		try {
			long id = postContact( contact3 );
			contact1.setId( id );
			putContact( contact1 );
			Contact contact = getContact( id );
			assertEquals( "Should be the title of contact1", contact1.getTitle(), contact.getTitle() );
			assertEquals( "Should be the name of contact1", contact1.getName(), contact.getName() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * PUT update to non-existing contact's ID.
	 */
	@Test
	public void testFailPut() {
		try {
			long id = 810101;
			StringContentProvider content = new StringContentProvider( marshal( contact1 ) );
			Request request = client.newRequest( uri + "contacts/" + id ).content( content, "application/xml" ).method( HttpMethod.PUT );
			ContentResponse response = request.send();
			assertEquals( "PUT update to non-existing contact's ID, should return 404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * DELETE a contact.
	 */
	@Test
	public void testSuccessDelete() {
		long id = postContact( contact1 );
		deleteContact( id );
	}

	/**
	 * Try to DELETE an non-existing contact's ID.
	 */
	@Test
	public void testFailDelete() {
		try {
			long id = 75423543;
			Request request = client.newRequest( uri + "contacts/" + id ).method( HttpMethod.DELETE );
			ContentResponse response = request.send();
			assertEquals( "Should return 404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * GET a contact, and check its attributes.
	 */
	@Test
	public void testSuccessGet() {
		try {
			postContact( contact3 );
			
			ContentResponse response2 = client.GET( uri + "contacts?title=" + contact3.getTitle().charAt( 0 )  );
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response2.getStatus() );

			List<Contact> contacts = convertBytesToContactList( response2.getContent() );
			assertEquals( "Should be the same title", contact3.getTitle(), contacts.get( 0 ).getTitle() );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GET non-existing contact's ID.
	 */
	@Test
	public void testFailGet() {
		try {
			long nonExistedId = 384734L;
			ContentResponse response = client.GET( uri + "contacts/" + nonExistedId );
			assertEquals( "Should return 404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), response.getStatus() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}