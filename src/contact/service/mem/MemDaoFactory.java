package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.DaoFactory;

/**
 * Manage instances of Memory-Based DAO used in the app.
 * Easier to change the implementation of ContactDao.
 * 
 * @author mapfap - Sarun Wongtanakarn
 */
public class MemDaoFactory extends DaoFactory {

	public static final String EXTERNAL_FILE_PATH = "/tmp/ContactsSevicePersistence.xml";
	private static MemDaoFactory factory;
	private MemContactDao daoInstance;

	private MemDaoFactory() {
		initFileIfnotExisted();
		daoInstance = new MemContactDao();
	}

	/**
	 * Get the instance of DaoFactory.
	 * @return instance of DaoFactory.
	 */
	public static MemDaoFactory getInstance() {
		if ( factory == null ) {
			factory = new MemDaoFactory();
		}
		return factory;
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