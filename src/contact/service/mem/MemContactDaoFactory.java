package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.ContactDaoFactory;

/**
 * Manage instances of Memory-Based DAO used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class MemContactDaoFactory extends ContactDaoFactory {

	public static final String EXTERNAL_FILE_PATH = "ContactsSevicePersistence.xml";
	private MemContactDao daoInstance;

	/**
	 * Suppose to call only once by its abstract factory.
	 */
	public MemContactDaoFactory() {
		initFileIfnotExisted();
		daoInstance = new MemContactDao();
	}

	/**
	 * Get the instance of ContactDao.
	 * @return the instance of ContactDao.
	 */
	public MemContactDao getContactDao() {
		return daoInstance;
	}

	@Override
	public void shutdown() {
		List<Contact> contacts = daoInstance.findAll();
		writeContactListToFile( contacts );
		
	}
	
	public void writeContactListToFile( List<Contact> contacts ) {
		Contacts exportContacts = new Contacts();
		exportContacts.setContacts( contacts );
		try {
			JAXBContext context = JAXBContext.newInstance( Contacts.class );
			File outputFile = new File( EXTERNAL_FILE_PATH );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( exportContacts, outputFile );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
	}

	private void initFileIfnotExisted() {
		File file = new File( EXTERNAL_FILE_PATH );
		if ( ! file.exists() ) {
			System.out.println("CREATE NEW FILE");
			List<Contact> contacts = new ArrayList<Contact>();
			writeContactListToFile( contacts );
		}
	}
	
}