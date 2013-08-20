package de.verism.client.validation.rules.input;

import de.verism.client.domain.data.Query;
import de.verism.client.validation.rules.BaseValidationRule;


/**
 * Strategy to validate the name field.
 * Also checks if variable name does not exist already as another object like in signals or figures.
 * (needed as object names will later be converted to variable names, which must be unique for verlog code generation).
 * 
 * @author Daniel Kotyk
 */
public class UniqueVariableRule extends BaseValidationRule<String> {

	@Override
	public void validate(String value) {
		//check name uniqueness
		if (Query.getAllNames().containsKey(value.trim().toLowerCase())) {
			addMessage("The name '" + value.trim() + "' already exists");
		}
	}
}
