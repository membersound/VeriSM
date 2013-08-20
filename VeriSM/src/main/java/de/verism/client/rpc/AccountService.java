package de.verism.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC service for account creation and validation.
 * Methods return the username for not having to distinguish between the username input fields on the client side.
 * @author Daniel Kotyk
 *
 */
@RemoteServiceRelativePath("account")
public interface AccountService extends RemoteService {
	/**
	 * Creates a new user for the application and stores him in the DB.
	 * @param username
	 * @param password
	 * @exception Exception
	 */
	String create(String username, String password) throws Exception;
	
	/**
	 * Validates a login against the existing user credentials in DB.
	 * @param username
	 * @param password
	 * @exception Exception
	 */
	String validate(String username, String password) throws Exception;
}
