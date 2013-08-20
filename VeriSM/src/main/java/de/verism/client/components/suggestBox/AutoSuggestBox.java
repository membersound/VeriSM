package de.verism.client.components.suggestBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.domain.data.Query.SuggestionStrategy;
import de.verism.client.util.BooleanKeyCodes;
import de.verism.client.util.UiHelper;

/**
 * A {@link SuggestBox} with special behavior:
 * - displays all available inputs and states in a suggest popup
 * - can display DTO objects and present them as Strings for the input box.
 *   Anyhow keeps object reference on the backend (which is necessary when eg states or inputs are renamed later on)
 * - detects boolean logic chars like ( ) & | ! as separators
 * 
 * So it is very comfortable to write quick conditions for outputs and states.
 * @author Daniel Kotyk
 *
 */
public class AutoSuggestBox extends Composite implements HasText {
	interface Binder extends UiBinder<Widget, AutoSuggestBox> {}
	
	@UiField
    FlowPanel panel;
	
	@UiField(provided = true)
	SuggestBox suggestBox;

	private MultiValueTextBox multiTextBox; 
	private RelativeSuggestionDisplay display;

    //indicates the shift key status for tracking if the user types allowed special chars from the boolean alphabet above
    private boolean isShiftDown = false;
	
    /**
     * @param query the strategy to fetch suggestions for this suggestBox.
     */
    @UiConstructor
	public AutoSuggestBox(SuggestionStrategy query) {
    	display = new RelativeSuggestionDisplay();
        multiTextBox = new MultiValueTextBox();
        
        //prepare the suggest box
        ConditionMultiWordSuggestOracle oracle = new ConditionMultiWordSuggestOracle();
        oracle.addAll(query.getSuggestions());
        
        suggestBox = new SuggestBox(oracle, multiTextBox, display);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		//trim the condition value on focus loss
		multiTextBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				//read the condition from autobox and replace any linesbreaks or drailing whitespaces
				String condition = getText().trim().replace("\n", "").replace("\r", "");
				((MultiValueTextBox) getTextBox()).overrideText(condition);
			}
		});

		//key handler must be directly attached to the edit box directly. Attaching to the suggestBox itself does not work
		multiTextBox.addKeyUpHandler(new KeyUpHandler() {
			/**
			 * Removes the last typed character if suggestion display is not shown anymore (which means it was invalid).
			 */
			@Override
			public void onKeyUp(KeyUpEvent evt) {
				if(evt.getNativeKeyCode() == BooleanKeyCodes.KEY_SHIFT) {
					isShiftDown = false;
				}
			}
		});
		
		multiTextBox.addKeyDownHandler(new KeyDownHandler() {
		    /**
		     * Makes key_backspace delete a whole entry up to the last separator before the entry.
		     * Makes key_delete delete a whole entry up to the next separator after the entry.
		     * @param evt
		     */
			@Override
			public void onKeyDown(KeyDownEvent evt) {
				if(!isShiftDown && evt.getNativeKeyCode() == BooleanKeyCodes.KEY_SHIFT) {
					isShiftDown = true;
				}
				
			    int key = evt.getNativeKeyCode();
	
			    if(display.isSuggestionListShowing() && (isShiftDown && BooleanKeyCodes.booleanChars.contains(key) || BooleanKeyCodes.SPACEBAR == key) ) {
		        	//events should be scheduled to not get lost during event bubbeling
		        	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							//move cursor in front of the boolean separator sign
							multiTextBox.setCursorPos(multiTextBox.getCursorPos() - 1);
							//commit the token
				        	DomEvent.fireNativeEvent(UiHelper.keyDownEvent(KeyCodes.KEY_TAB), multiTextBox); //could also use KEY_ENTER
				        	multiTextBox.setCursorPos(multiTextBox.getCursorPos() + 2);
						}
					});
		        }
			}
		});
    }

    /**
     * Public accessor to return the box content.
     */
	@Override
	public String getText() {
    	return multiTextBox.getFullText();
	}

    /**
     * Initially populate the box field.
     */
	@Override
	public void setText(String text) {
    	multiTextBox.setText(text);
	}

	public ValueBoxBase<String> getTextBox() {
		return multiTextBox;
	}
}
