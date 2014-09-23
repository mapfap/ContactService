package contact.service;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Server;
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
	private static Server server;
	MemContactDao dao;
	Contact contact1;
	Contact contact2;
	Contact contact3;


	@BeforeClass
	public static void doFirst( ) {
		server = JettyMain.startServer( TEST_PORT );
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
	
	@Test
	public void testGet() {
		HttpClient httpClient = new HttpClient();
		try {
			httpClient.start();
			System.out.println( server.getURI() );
			ContentResponse get = httpClient.GET("http://localhost:" + TEST_PORT + "/contacts");
			assertEquals( "Should return 200 OK", get.getStatus() + "", "200" );
			
//			List<Contact> contacts = convertBytesToContactList( get.getContent() );
//			assertEquals( "Should return empty list", contacts.size(), 0 );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//	@Test
	//	public void testPost() {
	////		try {
	////			Thread.sleep(1000000);
	////		} catch (InterruptedException e) {
	////			// TODO Auto-generated catch block
	////			e.printStackTrace();
	////		}
	////		System.out.println("b");
	////		System.out.println("dfvdv");

	//	}

	@Test
	public void testPost() {
		//        send a POST request to the serviceURL. Test the response.
	}

	@Before
	public void setUp() {
		contact1 = new Contact( "contact1", "Joe Contact", "joe@microsoft.com", "0123456789" );
		contact2 = new Contact( "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contact3 = new Contact( "contact3", "Foo Bar", "foo@barclub.com", "0553456789" );
	}

}