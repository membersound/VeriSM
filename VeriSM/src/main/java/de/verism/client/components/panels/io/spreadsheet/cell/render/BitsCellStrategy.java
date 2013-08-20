package de.verism.client.components.panels.io.spreadsheet.cell.render;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyCodes;

import de.verism.client.components.numericBox.NumericBox;
import de.verism.client.components.panels.io.spreadsheet.Spreadsheet;
import de.verism.client.domain.Signal;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.BitRule;

/**
 * Strategy for the bits cell input field.
 * Using strategy here has the advantage to use the key validation on several application points,
 * like: {@link Spreadsheet} cells, {@link NumericBox} inputs. 
 * @author Daniel Kotyk
 *
 */
public class BitsCellStrategy implements CellStrategy {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTooltip(InputElement input) {
		input.setTitle("Must be a digit > 0.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPreventKey(int key) {
		//the input keycode
		char ch = (char) key;
		
		//cancel the event for non-digits or non-navigation keys, or between numpad range
		return !(Character.isDigit(ch) || navKeyCodes.contains(key) || (key >= 96 && key <= 105));
	}
		
	/**
	 * List of keycodes for which keypress should not be cancelled in the {@link Signal#getBits()} field.
	 */
	private static final List<Integer> navKeyCodes = Arrays.asList(
		KeyCodes.KEY_LEFT,
		KeyCodes.KEY_RIGHT,
		KeyCodes.KEY_TAB,
		KeyCodes.KEY_BACKSPACE,
		KeyCodes.KEY_DELETE,
		KeyCodes.KEY_ENTER,
		KeyCodes.KEY_HOME,
		KeyCodes.KEY_END
	);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValidationError isInputValid(String input) {
		Validator<String> validator = new Validator<String>();
		validator.add(new BitRule());
		return validator.validate(input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Signal signal) {
		return String.valueOf(signal.getBits());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOldValue(Signal oldSignal) {
		 return String.valueOf(oldSignal.getBits());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(Signal signal, String value) {
		signal.setBits(Integer.valueOf(value));
	}
	
	/**
	 * Compares two signals by bits count.
	 * @author Daniel Kotyk
	 *
	 */
	public static Comparator<Signal> comparator = new Comparator<Signal>() {
		@Override
		public int compare(Signal s1, Signal s2) {
			return ((Integer) s1.getBits()).compareTo((Integer) s2.getBits());
		}
	};
}
