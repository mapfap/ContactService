package contact.service.mem;

import contact.service.UserDao;
import contact.service.UserDaoFactory;

public class MemUserDaoFactory extends UserDaoFactory {

	@Override
	public UserDao getUserDao() {
		return new MemUserDao();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
