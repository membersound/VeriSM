package de.verism.server.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Database object for storing user accounts.
 * @author Daniel Kotyk
 *
 */
@Entity
public class Account {
	@Id
	private String username;
	private String password;

	@ElementCollection
	private Map<String, Project> projects = new HashMap<String, Project>();
	
	/**
	 * Empty default constructor for DB.
	 */
	protected Account() {}
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public Map<String, Project> getProjects() { return projects; }
	public void setProjects(HashMap<String, Project> projects) { this.projects = projects; }
}
