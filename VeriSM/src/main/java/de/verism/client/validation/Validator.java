package de.verism.client.validation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ValueBoxBase;

import de.verism.client.validation.rules.ValidationRule;

/**
 * A validator that validates the input against the added rules.
 * @author Daniel Kotyk
 *
 */
public class Validator<T> {
	//holds the rules added to this validator
	private List<ValidationRule<T>> rules = new ArrayList<ValidationRule<T>>();
	//holds all validation errors (suitable for single rule validation).
	private List<ValidationError> errors = new ArrayList<ValidationError>();

	/**
	 * Validates a signal against attached rules.
	 * @param value the signal to validate
	 * @return the validation error containing all erroneous objects
	 */
	public ValidationError validate(T value) {
		ValidationError validationError = new ValidationError();
		
		for (ValidationRule<T> rule : rules) {
			if (!rule.isInputValid(value)) {
				validationError.add(rule.getMessage());
			}
		}
		if (validationError.hasErrors()) {
			errors.add(validationError);
		}

		return validationError;
	}
	
	/**
	 * Validate using only one single rule.
	 * @param value
	 * @param rule
	 * @return
	 */
	public ValidationError validate(T value, ValidationRule<T> rule, ValueBoxBase<T> input) {
		rules = new ArrayList<ValidationRule<T>>();
		rules.add(rule);
		ValidationError validationError = validate(value);
		validationError.setInput(input);
		return validationError;
	}

	/**
	 * Add a new validation rule to comply against when validating.
	 * @param rule
	 */
	public void add(ValidationRule<T> rule) {
		rules.add(rule);
	}
	
	public List<ValidationError> getErrors() {
		return errors;
	}
}