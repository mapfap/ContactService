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
		contact2 = new Contact( "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contact3 = new Contact( "contact3", "Foo Bar", "foo@barclub.com", "0553456789" );
	}

	@AfterClass
	public static void doLast( ) {
		JettyMain.stopServer();
	}

	private List<Contact> convertBytesToContactList( byte[] bs ) {
		InputStream bodyStream = new ByteArrayInputStream( bs );
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
	
	private Contact convertBytesToContact( byte[] bs ) {
		InputStream bodyStream = new ByteArrayInputStream( bs );
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

			System.out.println( location );

			ContentResponse get = client.GET( uri + "contacts/" + id);
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), get.getStatus() );

			Contact contact = convertBytesToContact( get.getContent() );
			assertEquals( "Should be the same title", contact1.getTitle(), contact.getTitle() );
			assertEquals( "Should be the same name", contact1.getName(), contact.getName() );
			assertEquals( "Should be the same email", contact1.getEmail(), contact.getEmail() );
			assertEquals( "Should be the same telephone", contact1.getPhoneNumber(), contact.getPhoneNumber() );


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//	  @Test
	//	  public void testPut() {
	//	    System.out.println("PUT");
	//	    HttpClient client = new HttpClient();
	//	    try {
	//	      client.start();
	//	    } catch (Exception e1) {
	//	      e1.printStackTrace();
	//	    }
	//	    String path = uri + "102";
	//	    Request req = client.newRequest(path);
	//	    req = req.method(HttpMethod.PUT);
	//	    StringContentProvider content = new StringContentProvider("<contact id=\"102\">" +
	//	        "<title>contact nickname or title edited</title>" +
	//	        "<name>contact's full name edited</name>" +
	//	        "<email>contact's email address edited</email>" +
	//	        "<phoneNumber>contact's telephone number edited</phoneNumber>"+
	//	        "</contact>");
	//	    req = req.content(content, "application/xml");
	//	    try {
	//	      req.send();
	//	    } catch (InterruptedException | TimeoutException | ExecutionException e) {
	//	      e.printStackTrace();
	//	    }
	//	    ContentResponse res = null;
	//	    try {
	//	      res = client.GET(path);
	//	    } catch (InterruptedException | ExecutionException | TimeoutException e) {
	//	      e.printStackTrace();
	//	    }
	//	    System.out.println(res.getContentAsString());
	//	    System.out.println(res.getHeaders());
	//	    try {
	//	      client.stop();
	//	    } catch (Exception e) {
	//	      e.printStackTrace();
	//	    }
	//	  }

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
			ContentResponse response = client.GET( uri + "contacts" );
			assertEquals( "Should return 200 OK", Status.OK.getStatusCode(), response.getStatus() );
			List<Contact> contacts = convertBytesToContactList( response.getContent() );
			System.out.println(contacts.get(0));
			assertEquals( "Should return empty list", 0, contacts.size() );
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