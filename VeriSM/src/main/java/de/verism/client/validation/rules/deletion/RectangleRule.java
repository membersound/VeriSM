package de.verism.client.validation.rules.deletion;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.data.Query;

/**
 * Strategy to validate the deletion of a {@link Rectangle}.
 * @author Daniel Kotyk
 *
 */
public class RectangleRule extends BaseDeletionRule {
	/**
	 * Validate that no output signal depends on the state to be deleted.
	 */
	@Override
	public void validate(HasTextId value) {
		super.contains(value, Query.getPartialConditions());
	}
}