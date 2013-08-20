package de.verism.client.validation.rules.verilog;

import de.verism.client.domain.HasCondition;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Verify that an output has a condition defined.
 * @author Daniel Kotyk
 *
 */
public class HasConditionRule extends BaseValidationRule<HasCondition> {
	
	@Override
	public void validate(HasCondition value) {
		if (value.getCondition().getValue().trim().isEmpty()) {
			addMessage("Output '" + value.getText() + "' has no condition defined");
		}
	}
}
