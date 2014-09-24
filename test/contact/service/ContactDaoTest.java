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

public class ContactDaoTest {

	private static final int TEST_PORT = 28080;
	private static URI uri;
	private static Contact contact1;
	private static Contact contact2;
	private static Contact contact3;
	private ContactDao contactDao = DaoFactory.getInstance().getContactDao();
	private HttpClient client;

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

	@After
	public void afterTest() {
		try {
			client.stop();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void doFirst( ) {
		uri = JettyMain.startServer( TEST_PORT );
		contact1 = new Contact( "contact1", "Joe Contact", "joe@microsoft.com", "0123456789" );
		contact2 = new Contact( 1456, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contact3 = new Contact( 4455, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
	}

	@AfterClass
	public static void doLast( ) {
		JettyMain.stopServer();
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

	@Test
	public void testSuccessPost() {
		try {

			StringContentProvider content = new StringContentProvider( marshal( contact1 ) );	

			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse res = request.send();

			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), res.getStatus() );

			String location = res.getHeaders().get("Location");
			String[] splited = location.split("/");
			String id = splited[ splited.length - 1 ];

			ContentResponse response = client.GET( uri + "contacts/" + id);
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );

			Contact contact = convertBytesToContact( response.getContent() );
			assertEquals( "Should be the same title", contact1.getTitle(), contact.getTitle() );
			assertEquals( "Should be the same name", contact1.getName(), contact.getName() );
			assertEquals( "Should be the same email", contact1.getEmail(), contact.getEmail() );
			assertEquals( "Should be the same telephone", contact1.getPhoneNumber(), contact.getPhoneNumber() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFailPost() {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact2 ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse response = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), response.getStatus() );
			
			ContentResponse response2 = request.send();
			assertEquals( "Add Contact with existing ID, should return 409 CONFLICT", Status.CONFLICT.getStatusCode(), response2.getStatus() );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSuccessPut() {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact3 ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse res = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), res.getStatus() );
			String location = res.getHeaders().get("Location");
			String[] splited = location.split("/");
			String id = splited[ splited.length - 1 ];
			
			contact1.setId( Long.parseLong( id ) );
			StringContentProvider content2 = new StringContentProvider( marshal( contact1 ) );
			Request request2 = client.newRequest( uri + "contacts/" + id ).content( content2, "application/xml" ).method( HttpMethod.PUT );
			ContentResponse response2 = request2.send();
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response2.getStatus() );

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
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

	@Test
	public void testSuccessDelete() {
		try {
			long id = 75423543;
			Request request = client.newRequest( uri + "contacts/" + id ).method( HttpMethod.DELETE );
			ContentResponse response = request.send();
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFailDelete() {
		try {
			Request request = client.newRequest( uri + "contacts" ).method( HttpMethod.DELETE );
			ContentResponse response = request.send();
			assertEquals( "Delete without specify ID, Should return 405 METHOD NOT ALLOWED", Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSuccessGet() {
		try {
			StringContentProvider content = new StringContentProvider( marshal( contact3 ) );	
			Request request = client.newRequest( uri + "contacts" ).content( content, "application/xml" ).method( HttpMethod.POST );
			ContentResponse res = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), res.getStatus() );
			String location = res.getHeaders().get("Location");
			String[] splited = location.split("/");
			String id = splited[ splited.length - 1 ];
			
			ContentResponse response = client.GET( uri + "contacts?title=" + contact3.getTitle().charAt( 0 )  );
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );

			List<Contact> contacts = convertBytesToContactList( response.getContent() );
			assertEquals( "Should be the same title", contact3.getTitle(), contacts.get( 0 ).getTitle() );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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