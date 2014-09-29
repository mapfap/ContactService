package contact.entity;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test Contact
 * 
 * @author mapfap - Sarun Wongtakakarn
 *
 */
public class ContactTest {
	
	@Test
	public void testMD5() {
		Contact contact1 = new Contact( "contact1", "Joe Contact", "jobs@apple.com", "0123456789" );
		Contact contact2 = new Contact( 1456, "contact1", "Joe Contact", "gates@microsoft.com", "0123456789" );
		Contact contact3 = new Contact( 1456, "contact1", "Joe Contact", "gates@microsoft.com", "0123456789" );
		
		assertEquals( "Same contact must have same MD5", contact1.getMD5(), contact1.getMD5() );
		assertEquals( "Two contacts with same data must have same MD5", contact2.getMD5(), contact3.getMD5() );
		assertFalse( "Two different contacts should not have same MD5", contact1.getMD5().equals( contact2.getMD5() ) );
	}
}