package de.verism.client.rpc;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.verism.client.domain.JsonDTO;

/**
 * Handles the projects from server db.
 * The username is the identifier parameter.
 * @author Daniel Kotyk
 *
 */
@RemoteServiceRelativePath("project")
public interface ProjectService extends RemoteService{
	/**
	 * Saves the content to webserver DB.
	 * @param content
	 * @return
	 * @throws IOException
	 */
	boolean saveProject(String username, JsonDTO jsonDTO) throws IOException;
	
	/**
	 * Fetches the project from DB.
	 * @param name
	 * @return
	 * @throws IOException
	 */
	JsonDTO getProject(String username, String projectname) throws IOException;
	
	/**
	 * Fetches a list of all available projects.
	 * To reduce data load, this only returns a named list of projects, not all full projects.
	 * @return
	 */
	ArrayList<String> getProjects(String username);
	
	/**
	 * Deletes a project.
	 * @param username
	 * @param projectname
	 * @return
	 */
	boolean deleteProject(String username, String projectname);
}
