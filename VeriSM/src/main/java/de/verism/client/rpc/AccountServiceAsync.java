/**
 * 
 */
package de.verism.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC service for account creation and validation.
 * Methods return the username for not having to distinguish between the username input fields on the client side.
 * @author Daniel Kotyk
 * @generated generated asynchronous callback interface to be used on the client side
 *
 */
public interface AccountServiceAsync {

	/**
	 * Creates a new user for the application and stores him in the DB.
	 * @param username
	 * @param password
	 * @param  callback the callback that will be called to receive the return value
	 * @exception Exception
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void create(String username, String password, AsyncCallback<String> callback);

	/**
	 * Validates a login against the existing user credentials in DB.
	 * @param username
	 * @param password
	 * @param  callback the callback that will be called to receive the return value
	 * @exception Exception
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void validate(String username, String password,
			AsyncCallback<String> callback);

}
