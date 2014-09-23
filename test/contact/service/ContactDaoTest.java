package contact.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.JettyMain;
import contact.entity.Contact;
import contact.service.mem.MemContactDao;

public class ContactDaoTest {

	private static final int TEST_PORT = 28080;
	MemContactDao dao;
	Contact contact1;
	Contact contact2;
	Contact contact3;


	@BeforeClass
	public static void doFirst( ) {
		JettyMain.startServer( TEST_PORT );
	}

	@AfterClass
	public static void doLast( ) {
		JettyMain.stopServer();
	}

	@Test
	public void testGet() {
		HttpClient httpClient = new HttpClient();

		try {
			httpClient.start();
			ContentResponse get = httpClient.GET( "http://localhost:" + TEST_PORT + "/contacts");
			System.out.println(get.getContentAsString());
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