/**
 * 
 */
package de.verism.client.rpc;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.verism.client.domain.JsonDTO;

/**
 * Handles the projects from server db.
 * The username is the identifier parameter.
 * @author Daniel Kotyk
 * @generated generated asynchronous callback interface to be used on the client side
 *
 */
public interface ProjectServiceAsync {

	/**
	 * Saves the content to webserver DB.
	 * @param content
	 * @param  callback the callback that will be called to receive the return value
	 * @return
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void saveProject(String username, JsonDTO jsonDTO,
			AsyncCallback<Boolean> callback);

	/**
	 * Fetches the project from DB.
	 * @param name
	 * @param  callback the callback that will be called to receive the return value
	 * @return
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void getProject(String username, String projectname,
			AsyncCallback<JsonDTO> callback);

	/**
	 * Fetches a list of all available projects.
	 * To reduce data load, this only returns a named list of projects, not all full projects.
	 * @return
	 * @param  callback the callback that will be called to receive the return value
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void getProjects(String username, AsyncCallback<ArrayList<String>> callback);

	/**
	 * Deletes a project.
	 * @param username
	 * @param projectname
	 * @param  callback the callback that will be called to receive the return value
	 * @return
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void deleteProject(String username, String projectname,
			AsyncCallback<Boolean> callback);

}
