package de.verism.client.validation.rules.input;

import de.verism.client.components.suggestBox.MultiValueTextBox;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Strategy to validate the bits field.
 * @author Daniel Kotyk
 *
 */
public class BitRule extends BaseValidationRule<String> {
	private static final int MIN = 1;
	//could be used to restrict bits to a maxmimum value
	private static final int MAX = 1024;
	
	@Override
	public void validate(String value) {
		//check empty
		if (value.isEmpty()) {
			addMessage("Bits must not be empty");
		}
		
		//check whitespace
		else if (value.trim().contains(MultiValueTextBox.WHITESPACE)) {
			addMessage("Bits must not contain whitespaces");
		} else {
			
			//check integer
			try {
				int bits = (Integer.valueOf(value));
				if (bits < MIN) { // || bits > MAX
					addMessage("Bits must be a digit > 0"); // between " + MIN + " and " + MAX
				}
			} catch (Exception e) {
				addMessage("Bits must be a valid digit");
			}
		}
	}
}