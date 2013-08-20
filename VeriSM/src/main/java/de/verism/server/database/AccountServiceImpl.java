package de.verism.server.database;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.verism.client.rpc.AccountService;

/**
 * Service handling all account managament.
 * @author Daniel Kotyk
 *
 */
public class AccountServiceImpl extends RemoteServiceServlet implements AccountService {
	@Override
	public String create(String username, String password) throws Exception {
		PersistenceService ps = new PersistenceService();
		
		if (ps.getAccount(username) != null) {
			throw new Exception("The username is already taken");
		}
		
		ps.create(new Account(username, password));
		return username;
	}

	@Override
	public String validate(String username, String password) throws Exception {
		PersistenceService ps = new PersistenceService();
	
		if (ps.getAccount(username) == null) {
			throw new Exception("Username does not exist");
		}
		
		if (!ps.getAccount(username).getPassword().equals(password)) {
			throw new Exception("Username and password do not match");
		}
		return username;
	}
}
