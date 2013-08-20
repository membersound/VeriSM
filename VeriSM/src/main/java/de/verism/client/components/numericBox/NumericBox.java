package de.verism.client.components.numericBox;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.TextBox;

import de.verism.client.components.panels.io.spreadsheet.cell.render.BitsCellStrategy;

/**
 * TextBox restricted to numeric inputs.
 * @author Daniel Kotyk
 *
 */
public class NumericBox extends TextBox {
	//the strategy used to validate the inputs before their events are handed to the TextBox itself
	private static final BitsCellStrategy strategy = new BitsCellStrategy();

	public NumericBox() {
		super();
		addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				//prevent all keys but numbers for the numeric input field
		    	int key = event.getNativeKeyCode();
		    	if (strategy.isPreventKey(key)) {
		    		event.preventDefault();
		    		event.stopPropagation();
		    		return;
		    	}
			}
		});
	}
}
