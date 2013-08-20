package de.verism.client.validation.rules.verilog;

import java.util.List;

import com.google.gwt.regexp.shared.SplitResult;

import de.verism.client.domain.HasCondition;
import de.verism.client.domain.Signal;
import de.verism.client.validation.rules.BaseValidationRule;
import de.verism.client.validation.rules.input.ConditionRule;

/**
 * Verify that a signal is not unused, ie linked to an ouput port or a transition.
 * @author Daniel Kotyk
 *
 */
public class IsInputUsedRule extends BaseValidationRule<Signal> {
	//the conditions to validate against
	private List<HasCondition> conditions;

	public IsInputUsedRule(List<HasCondition> conditions) {
		this.conditions = conditions;
	}
	
	/**
	 * Validates if an id is contained in the transition.
	 */
	@Override
	public void validate(Signal input) {
		for (HasCondition condition : conditions) {
			SplitResult split = ConditionRule.splitForConversation(condition.getCondition().getInternalValue());
			for (int i = 0; i <= split.length(); i++) {
				String s = split.get(i);
				if (s != null && s.trim().equals(input.getId().trim())) {
					return;
				}
			}
		}
		
		addMessage("Input '" + input.getText() + "' is not used in the project");
	}
}
