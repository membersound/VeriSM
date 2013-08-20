package de.verism.client.domain;

import de.verism.client.util.UniqueName;

/**
 * The state domain object.
 * @author Daniel Kotyk
 *
 */
public class State extends Figure {

	//if the state is the starting state
	private boolean isInitial = false;
	
	public State() {}
	
	@Override
	String createUniqueName() {
		return UniqueName.forState();
	}
	
	public boolean isInitial() { return isInitial; }
	public void setInitial(boolean isInitial) { this.isInitial = isInitial; }
}
