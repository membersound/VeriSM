package de.verism.server.database;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.verism.client.domain.JsonDTO;
import de.verism.client.rpc.ProjectService;
import de.verism.server.json.JSONServiceImpl;

/**
 * RPC Service for exchanging the project data with the server.
 * @author Daniel Kotyk
 *
 */
public class ProjectServiceImpl extends RemoteServiceServlet implements ProjectService {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean saveProject(String username, JsonDTO jsonDTO) throws IOException {
		String content = JSONServiceImpl.getMapper().writeValueAsString(jsonDTO);
		
		PersistenceService ps = new PersistenceService();
		Account account = ps.getAccount(username);
		
		account.getProjects().put(jsonDTO.getProjectName(), new Project(jsonDTO.getProjectName(), content));
		ps.update(account);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonDTO getProject(String username, String projectName) throws IOException {
		PersistenceService ps = new PersistenceService();
		
		Project project = ps.getAccount(username).getProjects().get(projectName);
		if (project != null) {
			return JSONServiceImpl.getMapper().readValue(project.getContent(), JsonDTO.class);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<String> getProjects(String username) {
		PersistenceService ps = new PersistenceService();
		
		//result must be wrapped in an arraylist, as keyset is not serializable in gwt
		return new ArrayList<String>(ps.getAccount(username).getProjects().keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteProject(String username, String projectName) {
		PersistenceService ps = new PersistenceService();
		Account account = ps.getAccount(username);
		
		account.getProjects().remove(projectName);
		ps.update(account);
		return true;
	}
}
