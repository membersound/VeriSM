package de.verism.client.validation.rules.deletion;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.domain.Signal;
import de.verism.client.domain.data.Query;

/**
 * Strategy to validate the deletion of a {@link Signal}.
 * @author Daniel Kotyk
 *
 */
public class SignalRule extends BaseDeletionRule {

	/**
	 * Validate that no output signals or transitions depend on the input signal to be deleted.
	 */
	@Override
	public void validate(HasTextId value) {
		super.contains(value, Query.getAllConditions());
	}
}