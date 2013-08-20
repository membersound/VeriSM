package de.verism.server.database;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Database object to store the FSM projects.
 * @author Daniel Kotyk
 *
 */
@Embeddable
public class Project {
	private String projectName;
	
//	@Lob //normally would use @Lob here instead of @Column, but blocked by issue HHH-7541.
	@Column(length = 2147483647)
	private String content;
	
	/**
	 * Empty default constructor for DB.
	 */
	protected Project() {}

	public Project(String projectName, String content) {
		this.projectName = projectName;
		this.content = content;
	}

	public String getProjectName() { return projectName; }
	public void setProjectName(String projectName) { this.projectName = projectName; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}
