package de.verism.client.util;

import java.util.Arrays;
import java.util.List;

/**
 * Provides constants for keycodes not defined in gwt yet.
 * @author Daniel Kotyk
 *
 */
public class BooleanKeyCodes {
	//"! & ( )" are actually only the number codes, not the virtual keys
	public static final int SPACEBAR = 32;
	public static final int KEY_SHIFT = 16;
	
	private static final int AND = 54;
	private static final int LEFT_PARENTHESIS = 56;
	private static final int RIGHT_PARENTHESIS = 57;
	private static final int EXCLAMATION_MARK = 49;
	private static final int OR = 60;
	private static final int SUM = 48;
	private static final int VK_LEFT_PARENTHESIS = 519;
	private static final int VK_EXCLAMATION_MARK = 517;
	
	//virtual keys for plus keys
	public static final int VK_ADD = 107;
	public static final int VK_ADD_NUM = 171;
	public static final int VK_PLUS = 512;
	
	/**
	 * List of keycodes for which keypress should not be canceled.
	 */
	public static final List<Integer> booleanChars = Arrays.asList(
		SUM, AND, EXCLAMATION_MARK, OR, LEFT_PARENTHESIS, RIGHT_PARENTHESIS, VK_LEFT_PARENTHESIS, VK_EXCLAMATION_MARK
	);
}
