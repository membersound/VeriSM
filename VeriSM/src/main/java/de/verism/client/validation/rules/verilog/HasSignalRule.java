package de.verism.client.validation.rules.verilog;

import java.util.List;

import de.verism.client.domain.Signal;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Verify that at least one signal is created.
 * @author Daniel Kotyk
 *
 */
public class HasSignalRule extends BaseValidationRule<List<Signal>> {

	private String type;

	public HasSignalRule(String type) {
		this.type = type;
	}

	@Override
	protected void validate(List<Signal> list) {
		if (list.size() <= 0) {
			addMessage("No " + type + " signals have been defined");
		}
	}
}
