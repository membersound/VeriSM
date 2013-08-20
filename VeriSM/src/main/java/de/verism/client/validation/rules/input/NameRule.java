package de.verism.client.validation.rules.input;

import de.verism.client.components.suggestBox.MultiValueTextBox;
import de.verism.client.validation.rules.BaseValidationRule;


/**
 * Strategy to validate the name field.
 * Also checks if variable name does not exist already as another object like in signals or figures.
 * (needed as object names will later be converted to variable names, which must be unique for verlog code generation).
 * 
 * @author Daniel Kotyk
 */
public class NameRule extends BaseValidationRule<String> {

	@Override
	public void validate(String value) {
		//check empty
		if (value.isEmpty() || value.trim().isEmpty()) {
			addMessage("Name must not be empty");
		}
		
		//check whitespace
		else if (value.trim().contains(MultiValueTextBox.WHITESPACE)) {
			addMessage("Name must not contain whitespaces");
		} 
	}
}
