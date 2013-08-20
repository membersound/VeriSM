package de.verism.client.components.suggestBox;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.SuggestBox;

/**
 * Overrides the default display behavior to show the suggestion popup at cursor position instead of below the textarea.
 * @author Daniel Kotyk
 *
 */
public class RelativeSuggestionDisplay extends SuggestBox.DefaultSuggestionDisplay {
	
    /**
     * Workaround for GWT bug issue #3253:
     * Prevents the suggestbox navigation keys to be bubbled up if used inside the suggestion popup.
     * Otherwise gwt will create linebreaks inside the textarea if suggested item is selected using ENTER.
     * Also, UP and DOWN keys will move the cursor to the front and end of the text string during navigation in suggestbox.
     * @param evt
     */
	@Override
	protected void moveSelectionDown() {
		if (isSuggestionListShowing()) {
			super.moveSelectionDown();
			cancelCurrentEvent();
		}
	}
	@Override
	protected void moveSelectionUp() {
		if (isSuggestionListShowing()) {
			super.moveSelectionUp();
			cancelCurrentEvent();
		}
	}
	@Override
	public void hideSuggestions() {
		super.hideSuggestions();
		cancelCurrentEvent();
	}
	
	/**
	 * Terminates the current event, which is always a keydown for ARROW, ENTER or TAB.
	 * This prevents the cursor from moving inside the textbox while navigating in suggest popup.
	 */
	private void cancelCurrentEvent() {
		Event event = Event.getCurrentEvent();
		if (event != null) {
			event.preventDefault();
			event.stopPropagation();
		}
	}
}
