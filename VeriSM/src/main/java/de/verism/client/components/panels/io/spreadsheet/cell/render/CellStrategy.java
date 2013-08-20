package de.verism.client.components.panels.io.spreadsheet.cell.render;

import com.google.gwt.dom.client.InputElement;

import de.verism.client.domain.Signal;
import de.verism.client.validation.ValidationError;

/**
 * Strategy template for a render strategy to be placed into cells.
 * @author Daniel Kotyk
 *
 */
public interface CellStrategy {

	/**
	 * Set cell-specific tooltip for an editable cell.
	 * @param input the editable input element
	 */
	void setTooltip(InputElement input);

	/**
	 * Cell-specific prevent certain keys to be entered in the input field.
	 * @param key the keycode of an event
	 * @return if the key should be prevented
	 */
	boolean isPreventKey(int key);

	/**
	 * Determine if the given input is valid for a specific cell.
	 * @param input the input string
	 * @return if input is valid to be commited
	 */
	ValidationError isInputValid(String input);
	
	/**
	 * The value to be displayed as label in a cell representing the cell property.
	 * @param signal the signal to be displayed
	 * @return the value representing a property
	 */
	String getValue(Signal signal);
	
	/**
	 * The value to which invalid user inputs should be reverted to.
	 * @param oldSignal the old signal before edit
	 * @return the reverted value
	 */
	String getOldValue(Signal oldSignal);
	
	/**
	 * Push changes into the spreadsheet.
	 * @param signal the signal to edit
	 * @param value the new value
	 */
	void push(Signal signal, String value);
}
