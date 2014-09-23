package contact.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;
import contact.entity.Contact;
import contact.service.mem.Contacts;
import contact.service.mem.MemContactDao;

public class ContactDaoTest {

	private static final int TEST_PORT = 28080;
	private static URI uri;
	MemContactDao dao;
	Contact contact1;
	Contact contact2;
	Contact contact3;
	private ContactDao contactDao;

	@Before
	public void setUp() {
		
		contactDao = DaoFactory.getInstance().getContactDao();
		contactDao.removeAll();
		
		contact1 = new Contact( "contact1", "Joe Contact", "joe@microsoft.com", "0123456789" );
		contact2 = new Contact( "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contact3 = new Contact( "contact3", "Foo Bar", "foo@barclub.com", "0553456789" );
	}

	@BeforeClass
	public static void doFirst( ) {
		uri = JettyMain.startServer( TEST_PORT );
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
	  public void testPost() {
		
		HttpClient httpClient = new HttpClient();
		try {
			httpClient.start();
			
			StringContentProvider content = new StringContentProvider( marshal( contact1 ) );
			
			try {
				ContentResponse res = httpClient.newRequest( uri ).content( content, "application/xml" ).method( HttpMethod.POST ).send();
//				assertEquals( "Should return 200 OK", res.getStatus() + "", "200" );
				
				ContentResponse get = httpClient.GET("http://localhost:" + TEST_PORT + "/contacts/1342302");
				assertEquals( "Should return 200 OK", get.getStatus() + "", "200" );
				
				List<Contact> contacts = convertBytesToContactList( get.getContent() );
				assertEquals( "Should be the same Contact", contacts.get(0), contact1 );
//				assertEquals( "Should return empty list", contacts.size(), 0 );
				
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			
//			List<Contact> contacts = convertBytesToContactList( get.getContent() );
//			assertEquals( "Should return empty list", contacts.size(), 0 );
			
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
//	  
//	  @Test
//	  public void testDelete() {
//	    System.out.println("DELETE");
//	    HttpClient client = new HttpClient();
//	    try {
//	      client.start();
//	    } catch (Exception e) {
//	      e.printStackTrace();
//	    }
//	    String path = uri + "102";
//	    Request req = client.newRequest(path);
//	    req = req.method(HttpMethod.DELETE);
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
//	    try {
//	      client.stop();
//	    } catch (Exception e) {
//	      e.printStackTrace();
//	    }
//	  }
//	
//	@Test
//	public void testGet() {
//		HttpClient httpClient = new HttpClient();
//		try {
//			httpClient.start();
//			ContentResponse get = httpClient.GET("http://localhost:" + TEST_PORT + "/contacts");
//			assertEquals( "Should return 200 OK", get.getStatus() + "", "200" );
//			
//			List<Contact> contacts = convertBytesToContactList( get.getContent() );
//			assertEquals( "Should return empty list", contacts.size(), 0 );
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//	}

	

}