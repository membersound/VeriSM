package de.verism.client.components.panels.io.spreadsheet.cell.render;

import com.google.gwt.dom.client.InputElement;

import de.verism.client.domain.HasCondition;
import de.verism.client.domain.Signal;
import de.verism.client.domain.data.Query;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.ConditionRule;

/**
 * Strategy for the condnition cell input field.
 * @author Daniel Kotyk
 *
 */
public class ConditionCellStrategy implements CellStrategy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTooltip(InputElement input) {
		input.setTitle("May depend on states and inputs.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPreventKey(int key) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValidationError isInputValid(String input) {
		Validator<String> validator = new Validator<String>();
		// only used from spreadsheet, thus signal query needed
		validator.add(new ConditionRule(Query.SIGNAL));
		return validator.validate(input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Signal signal) {
		return signal.getCondition().getValue();
	}
	
	public String getValue(HasCondition obj) {
		return obj.getCondition().getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOldValue(Signal oldSignal) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(Signal signal, String value) {
		signal.getCondition().setValue(value);
	}
}
