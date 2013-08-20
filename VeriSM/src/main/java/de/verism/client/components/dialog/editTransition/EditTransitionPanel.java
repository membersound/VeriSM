package de.verism.client.components.dialog.editTransition;

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

import de.verism.client.components.dialog.edit.BaseEditor;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.components.panels.io.spreadsheet.cell.render.ConditionCellStrategy;
import de.verism.client.components.suggestBox.AutoSuggestBox;
import de.verism.client.domain.Transition;
import de.verism.client.domain.data.Query;
import de.verism.client.util.FocusHelper;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.ConditionRule;
import de.verism.client.validation.rules.input.KeywordRule;
import de.verism.client.validation.rules.input.NameRule;
import de.verism.client.validation.rules.input.UniqueVariableRule;

public class EditTransitionPanel extends Composite implements IsInputProvider {
	interface Binder extends UiBinder<Widget, EditTransitionPanel> {}

	@UiConstructor
	public EditTransitionPanel(Transition transition) {
		autoSuggestBox = new AutoSuggestBox(Query.TRANSITION);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		ConditionCellStrategy condition = new ConditionCellStrategy();
		autoSuggestBox.setText(condition.getValue(transition));
		nameBox.setText(transition.getText());
		checkbox.setValue(transition.isShowText());
		this.transition = transition;
		this.oldValue = transition.getText();
		
		FocusHelper.focus(nameBox);
	}
	
	//the ui components
    @UiField TextBox nameBox;
    @UiField(provided = true) AutoSuggestBox autoSuggestBox;
    @UiField CheckBox checkbox;
    
    //the signal under edit
	private Transition transition;
	//old value of the signal
	private String oldValue;
	
    /**
     * Makes TextBoxes submit the edit dialog on ENTER key.
     * @param evt
     */
	@UiHandler("nameBox")
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
		
		validator.validate(autoSuggestBox.getText(), new ConditionRule(Query.TRANSITION), autoSuggestBox.getTextBox());
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
    	transition.setText(nameBox.getValue().trim());
    	transition.setShowText(checkbox.getValue());
    	transition.getCondition().setValue(autoSuggestBox.getText());
	}

	@Override
	public void clearErrorStyles() {
		nameBox.removeStyleName(ErrorField.ERROR_STYLES);
		autoSuggestBox.getTextBox().removeStyleName(ErrorField.ERROR_STYLES);
	}
}
