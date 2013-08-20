package de.verism.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ValueBoxBase;

import de.verism.client.util.export.VerilogBuilder;

/**
 * Collects all validation errors as a list.
 * @author Daniel Kotyk
 *
 */
public class ValidationError {
	//the input box for which the error should be displayed
	private ValueBoxBase input;
	
	//list of all errors that occured
	private List<String> errorList = new ArrayList<String>();

	/**
	 * Add a new error string.
	 * @param text
	 */
	public void add(String text) {
		errorList.add(text);
	}

	/**
	 * Get all errors contained as a linebreak separated string.
	 * Mainly used for UI user feedback.
	 * @return
	 */
	public String getMessage() {
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		for (String error : errorList) {
			if (first) {
				first = false;
			} else {
			  	buffer.append(VerilogBuilder.LF);
			}
			buffer.append(error);
		}
		//return trailing the last separator
		return buffer.toString();
	}
	
	/**
	 * Returns if any validation errors occured during object validation.
	 * @return
	 */
	public boolean hasErrors() {
		return !errorList.isEmpty();
	}

	public void setInput(ValueBoxBase input) { this.input = input; }
	public ValueBoxBase getInput() { return input; }
}