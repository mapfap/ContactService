package contact.entity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the wrapper class that wrap a collection of Contact class.
 * for JAXB to be able to marshal it to xml text file.
 * 
 * @author mapfap - Sarun Wongtanakarn
 * 
 */
@XmlRootElement(name="contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts extends MD5Digestable implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="contact")
    private List<Contact> contacts;

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts( List<Contact> contacts ) {
		this.contacts = contacts;
	}
	
	/**
	 * @see contact.entity.MD5Digestable#getMD5()
	 */
	@Override
	public String getMD5() {
		StringBuilder data = new StringBuilder();
		for ( Contact c : getContacts() ) {
			data.append( c.getMD5() );
		}
		return super.digest( data.toString() );
	}
	
}