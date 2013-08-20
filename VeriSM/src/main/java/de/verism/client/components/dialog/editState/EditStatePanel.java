package de.verism.client.components.dialog.editState;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasCallback;
import de.verism.client.components.dialog.edit.BaseEditor;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.domain.State;
import de.verism.client.util.FocusHelper;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.KeywordRule;
import de.verism.client.validation.rules.input.NameRule;
import de.verism.client.validation.rules.input.UniqueVariableRule;

/**
 * Provides edit support for the canvas elements (states, transitions).
 * @author Daniel Kotyk
 *
 */
public class EditStatePanel extends Composite implements IsInputProvider {
	interface Binder extends UiBinder<Widget, EditStatePanel> {}

	@UiConstructor
	public EditStatePanel(State state) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		nameBox.setText(state.getText());
		checkbox.setValue(state.isInitial());
		
		this.state = state;
		this.oldValue = state.getText();
		
		FocusHelper.focus(nameBox);
	}

	//the ui components
    @UiField TextBox nameBox;
    @UiField CheckBox checkbox;
    
    //the signal under edit
	private State state;
	//old value of the figure
	private String oldValue;
	
	//callback used for save action
	private CanvasCallback callback;
	
	
    /**
     * Makes TextBoxes submit the form on ENTER key.
     * @param evt
     */
	@UiHandler({"nameBox", "checkbox"})
	void onEnter(KeyUpEvent evt) {
		if(evt.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			((BaseEditor) getParent().getParent().getParent()).onSubmitBtn(null);
		}
	}
    
	@Override
    public List<ValidationError> validateInputFields() {
    	Validator<String> validator = new Validator<String>();
		validator.validate(nameBox.getValue(), new NameRule(), nameBox);
		if (isNameChanged()) {
			validator.validate(nameBox.getValue(), new KeywordRule(), nameBox);
			validator.validate(nameBox.getValue(), new UniqueVariableRule(), nameBox);
		}
		return validator.getErrors();
    }
    
	/**
	 * Returns if the name property has been changed, which means a validation on 
	 * the variable name has to be done.
	 * If not changed, then the variable validation has to be skipped, as this
	 * would then fail because the name (that is unchanged) of course still exists.
	 * @return 
	 */
	private boolean isNameChanged() {
		return !nameBox.getValue().trim().equals(oldValue);
	}

	@Override
    public void save() {
    	state.setText(nameBox.getValue().trim());
		callback.changeInitialState(state, checkbox.getValue());
    }
    
	@Override
    public void clearErrorStyles() {
    	nameBox.removeStyleName(ErrorField.ERROR_STYLES);
    }
	
	public void setCanvasCallback(CanvasCallback callback) { this.callback = callback; }
}
