package de.verism.client.validation.rules;


/**
 * Basic strategy for the validation rules.
 * Holds the error objects in the StringBuilder.
 * 
 * @author Daniel Kotyk
 *
 */
public abstract class BaseValidationRule<T> implements ValidationRule<T> {
	//the error returned
	private StringBuilder error = new StringBuilder();
	
	@Override
	public String getMessage() {
		return error.toString();
	}
	
	/**
	 * Clears any saved errors, validates the input and returns is value was valid.
	 * Final, as it should never be overriden. Custom validation implementations are to be done in {@link #validate(Object)}.
	 * Advantage: every new rule does only have to care about simple input validation, no execution logic.
	 */
	@Override
	public final boolean isInputValid(T t) {
		error.setLength(0);
		validate(t);
		return getMessage().isEmpty();
	}
	
	/**
	 * Does the actual validation of a value.
	 * @param t the input value to be validated
	 */
	protected abstract void validate(T t);

	@Override
	public void addMessage(String string) {
		if (error.length() != 0) {
			error.append(", ");
		}
		error.append(string);
	}
}