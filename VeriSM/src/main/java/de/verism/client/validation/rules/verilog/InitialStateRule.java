package de.verism.client.validation.rules.verilog;

import java.util.List;

import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Verify that an inital state has been defined.
 * @author Daniel Kotyk
 *
 */
public class InitialStateRule extends BaseValidationRule<List<Rectangle>> {

	@Override
	public void validate(List<Rectangle> values) {
		boolean hasInitial = false;
		
		for (Rectangle rectanlge : values) {
			if (rectanlge.getFigure().isInitial()) {
				hasInitial = true;
				break;
			}
		}
		
		if (!hasInitial) {
			addMessage("No state has been defined as initial state");
		}
	}
}
