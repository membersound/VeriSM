package de.verism.client.canvas.shapes.visitors;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.Signal;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.ValidationRule;
import de.verism.client.validation.rules.deletion.RectangleRule;
import de.verism.client.validation.rules.deletion.SignalRule;

/**
 * Validates an object if it has any dependencies and might therefore get a veto for deletion.
 * @author Daniel Kotyk
 *
 */
public class DeleteValidationService implements Visitor<ValidationError> {

	@Override
	public ValidationError visit(Rectangle rectangle) {
		return validate(rectangle, new RectangleRule());
	}

	@Override
	public ValidationError visit(Line line) {
		//lines should always just be deleted, thus return empty validation error
		return new ValidationError();
	}

	@Override
	public ValidationError visit(Signal signal) {
		return validate(signal, new SignalRule());
	}
	
	/**
	 * Helper to create the validator and inspect the value with given rule.
	 * @param value the object to validate
	 * @param rule the rule to be applied during validation
	 * @return
	 */
	private ValidationError validate(HasTextId value, ValidationRule<HasTextId> rule) {
		Validator<HasTextId> validator = new Validator<HasTextId>();
		validator.add(rule);
		return validator.validate(value);
	}
}
