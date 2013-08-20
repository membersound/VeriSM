package de.verism.client.validation.rules;


/**
 * Validation template strategy.
 * @author Daniel Kotyk
 *
 */
public interface ValidationRule<T> {
	/**
	 * Performs the validation
	 * @param value
	 * @return
	 */
	boolean isInputValid(T value);
	
	/**
	 * Get all errors.
	 */
	String getMessage();
	
	/**
	 * Add a new error object.
	 */
	void addMessage(String text);
}
