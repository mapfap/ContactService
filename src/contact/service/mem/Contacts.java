package contact.service.mem;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import contact.entity.Contact;

/**
 * This is the wrapper class that wrap a collection of Contact class.
 * for JAXB to be able to marshal it to xml text file.
 * 
 * @author mapfap - Sarun Wongtanakarn
 * 
 */
@XmlRootElement(name="contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts {
	
	@XmlElement(name="contact")
    private List<Contact> contacts;

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts( List<Contact> contacts ) {
		this.contacts = contacts;
	}
	
}