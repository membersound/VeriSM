package de.verism.client.components.dialog.editSignal;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.dialog.edit.BaseEditor;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.components.panels.io.spreadsheet.cell.render.CellStrategy;
import de.verism.client.components.panels.io.spreadsheet.cell.render.ConditionCellStrategy;
import de.verism.client.components.suggestBox.AutoSuggestBox;
import de.verism.client.domain.Signal;
import de.verism.client.domain.data.Query;
import de.verism.client.util.FocusHelper;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.input.BitRule;
import de.verism.client.validation.rules.input.ConditionRule;
import de.verism.client.validation.rules.input.KeywordRule;
import de.verism.client.validation.rules.input.NameRule;
import de.verism.client.validation.rules.input.UniqueVariableRule;

public class EditSignalPanel extends Composite implements IsInputProvider {
	interface Binder extends UiBinder<Widget, EditSignalPanel> {}

	@UiConstructor
	public EditSignalPanel(Signal signal) {
		autoSuggestBox = new AutoSuggestBox(Query.SIGNAL);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		CellStrategy condition = new ConditionCellStrategy();
		autoSuggestBox.setText(condition.getValue(signal));
		nameBox.setText(signal.getName());
		bitBox.setText(String.valueOf(signal.getBits()));
		this.signal = signal;
		this.oldValue = signal.getText();
		
		FocusHelper.focus(nameBox);
	}
	
	/**
	 * Shows the condition field for output signals.
	 * @param showCondition
	 */
	public void setShowCondition(boolean showCondition) {
		autoSuggestBox.setVisible(showCondition);
		
		//textboxes should only take half of the space if condition field is shown (for input signals)
		if (showCondition) {
			nameBox.setWidth(BOX_SIZE);
			bitBox.setWidth(BOX_SIZE);
		}
	}
	
	//width of the input boxes
	private static final String BOX_SIZE = "50%";
	
	//the ui components
    @UiField TextBox nameBox, bitBox;
    @UiField(provided = true) AutoSuggestBox autoSuggestBox;
    
    //the signal under edit
	private Signal signal;
	//old value of the signal
	private String oldValue;
	
    /**
     * Makes TextBoxes submit the edit dialog on ENTER key.
     * @param evt
     */
	@UiHandler({"nameBox", "bitBox"})
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
		validator.validate(bitBox.getValue(), new BitRule(), bitBox);
		validator.validate(autoSuggestBox.getText(), new ConditionRule(Query.SIGNAL), autoSuggestBox.getTextBox());
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
    	signal.setText(nameBox.getValue().trim());
		signal.setBits(Integer.valueOf(bitBox.getValue()));
		
		CellStrategy condition = new ConditionCellStrategy();
		condition.push(signal, autoSuggestBox.getText().trim());
	}

	@Override
	public void clearErrorStyles() {
		nameBox.removeStyleName(ErrorField.ERROR_STYLES);
		bitBox.removeStyleName(ErrorField.ERROR_STYLES);
		autoSuggestBox.getTextBox().removeStyleName(ErrorField.ERROR_STYLES);
	}
}
