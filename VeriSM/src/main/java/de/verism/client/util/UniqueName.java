package de.verism.client.util;

import de.verism.client.validation.rules.input.UniqueVariableRule;

/**
 * Creates a new unique name for a signal.
 * @author Daniel Kotyk
 */
public class UniqueName {

	//initial name prefix placeholder for new signals
	private static final String NEW_SIGNAL = "sig";
	private static final String NEW_STATE = "S";
	private static final String NEW_TRANSITION = "t_";
	
	/**
	 * Creates a new name with structure "newX", where X is a number > 0.
	 * @return
	 */
	public static String forSignal() {
		return create(NEW_SIGNAL);
	}
	
	/**
	 * Creates a new state name as "SX".
	 * @return
	 */
	public static String forState() {
		return create(NEW_STATE);
	}
	
	
	/**
	 * Creates a new transition name as "tX".
	 * @return
	 */
	public static String forTransition(String from, String to) {
		return create(NEW_TRANSITION + from + "_" + to);
	}
	
	/**
	 * Helper to create a new name by prefix with uniqueness validation.
	 * @param prefix
	 * @return
	 */
	private static String create(String prefix) {
		int i = 0;
		String newName = prefix;
		//validate against present data and continue counting up the number if name is not unique
		while (!(new UniqueVariableRule()).isInputValid(newName)) {
			newName = prefix + i++;
		}
		return newName;
	}
	
}
