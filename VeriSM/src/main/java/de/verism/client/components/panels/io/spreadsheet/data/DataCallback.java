package de.verism.client.components.panels.io.spreadsheet.data;

import de.verism.client.domain.Signal;


/**
 * Callback for the footer to access crud operations of the spreadsheet.
 * @author Daniel Kotyk
 *
 */
public interface DataCallback {
	/**
	 * Add a new signal to the spreadsheet.
	 */
	void addNewSignal();
	
	/**
	 * Remove currently selected signal from the spreadsheet.
	 */
	void removeSelectedSignal();

	/**
	 * Changes selected signal into edit mode.
	 * @param y 
	 * @param x 
	 */
	void editSignal(Signal value, int x, int y);

	void setSelected(Signal value);
}
