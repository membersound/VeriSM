package de.verism.client.validation.rules.verilog;

import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Verify that each state has at least either an incoming or outgoing transition.
 * @author Daniel Kotyk
 *
 */
public class HasTransitionRule extends BaseValidationRule<Rectangle> {

	@Override
	public void validate(Rectangle value) {
		if (value.getConnections().size() < 1) {
			addMessage("'" + value.getText() + "' must have at least one transition");
		}
	}
}
