package de.verism.client.components.panels.io.spreadsheet.cell.render;

import java.util.Comparator;

import com.google.gwt.dom.client.InputElement;

import de.verism.client.domain.Signal;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.KeywordRule;
import de.verism.client.validation.rules.input.NameRule;
import de.verism.client.validation.rules.input.UniqueVariableRule;

/**
 * Strategy for the name cell input field.
 * @author Daniel Kotyk
 *
 */
public class NameCellStrategy implements CellStrategy {
	public static final String EMPTY = "notDefined";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTooltip(InputElement input) {
		input.setTitle("Must be a non-empty name.");
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
		Validator validator = new Validator();
		validator.add(new NameRule());
		validator.add(new KeywordRule());
		validator.add(new UniqueVariableRule());
		return validator.validate(input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Signal signal) {
		return signal.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOldValue(Signal oldSignal) {
		return oldSignal.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(Signal signal, String value) {
		signal.setName(value);
	}
	
	
	/**
	 * Compares two signals by name.
	 * @author Daniel Kotyk
	 *
	 */
	public static Comparator<Signal> comparator = new Comparator<Signal>() {
		@Override
		public int compare(Signal s1, Signal s2) {
			return s1.getName().compareTo(s2.getName());
		}
	};
}
