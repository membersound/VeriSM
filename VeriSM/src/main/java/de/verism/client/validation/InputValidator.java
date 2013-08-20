package de.verism.client.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.google.gwt.validation.client.impl.Validation;

/**
 * Validator for any objects.
 * <T> value objects must be registered in {@link ValidationFactory} for validation to execute.
 * @author Daniel Kotyk
 */
public class InputValidator {
	
	/**
	 * For validation of the form input fields when creating a new or editing existing signals.
	 */
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	
	/**
	 * Holds the current error message.
	 */
	private StringBuilder errorMessage = new StringBuilder();
	
	/**
	 * Validates a new signal and displays resulting errors on screen.
	 * @param value the object to validate
	 * @return if has errors
	 */
	public <T> boolean isInputValid(T value) {
		//validate the provided value
		Set<ConstraintViolation<T>> violations = validator.validate(value);
		
		return isValid(violations);
	}
	
	/**
	 * Validates a new signal and displays resulting errors on screen, but only the specific property.
	 * @param value the object to validate
	 * @param property the property to validate
	 * @return if property is valid
	 */
	public <T> boolean isPropertyValid(T value, String property) {
		//validate the property
		Set<ConstraintViolation<T>> violations = validator.validateProperty(value, property);

		return isValid(violations);
	}
	
	/**
	 * Helper to evaluate the results of the validation.
	 * @param violations
	 * @return
	 */
	private <T> boolean isValid(Set<ConstraintViolation<T>> violations) {
		//clear stringbuffer
		errorMessage.setLength(0);
		
		//check for error messages
		if (!violations.isEmpty()) {
			for (ConstraintViolation<T> constraintViolation : violations) {
				errorMessage.append(constraintViolation.getMessage());
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return the error message to be displayed on screen if any errors ocurred in validation process.
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage.toString();
	}
}
