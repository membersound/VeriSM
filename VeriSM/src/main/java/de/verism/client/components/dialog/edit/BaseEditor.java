package de.verism.client.components.dialog.edit;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.icons.IconTextButton;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.util.export.VerilogBuilder;
import de.verism.client.validation.ValidationError;

/**
 * Basic edit panel that defines the outer popup for editing objects (states, transitions, signals).
 * 
 * It displays the popup, the header, the buttons, and the error message.
 * It expects a {@link IsInputProvider} widget that must serve the TextBoxes or TextAreas for the edit.
 * This ensures that general popup handling and object editing is decoupled.
 * 
 * Further, the popup with its logic of saving, error handling etc can be reused, and only the
 * {@link IsInputProdiver} has to be exchanged. Makes it possible to use the same basic popup for
 * all kind of objects, no matter what properties are to be edited.
 * 
 * One could also think of the {@link IsInputProdiver} as a kind of StrategyPattern.
 * @author Daniel Kotyk
 *
 */
public class BaseEditor extends Composite {
	interface Binder extends UiBinder<Widget, BaseEditor> {}

    /**
     * Package protected constructor to let only {@link EditorDialog} instantiate this base panel.
     * @param inputs
     */
	@UiConstructor
    BaseEditor(IsInputProvider inputProvider) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		placeholder.add(inputProvider);
		this.inputProvider = inputProvider;
    }
    
	private IsInputProvider inputProvider;
    
	//the ui components
	@UiField ErrorField errorPanel;
    @UiField IconTextButton submitBtn, cancelBtn;
    @UiField Image loadingIndicator;
    @UiField FlowPanel placeholder;
    
    /**
     * Validate and save.
     * @param evt
     */
    @UiHandler("submitBtn")
	public void onSubmitBtn(ClickEvent evt) {
        //disable the submit button during upload and show loading icon
    	updateError("", false);

    	List<ValidationError> errors = inputProvider.validateInputFields();
		if (errors.size() > 0) {
			String errorMsg = "";
			for (ValidationError error : errors) {
				errorMsg += error.getMessage() + VerilogBuilder.LF;
				error.getInput().addStyleName(ErrorField.ERROR_STYLES);
			}
			
			updateError(errorMsg, true);
			if (evt != null) {
		    	evt.preventDefault();
				evt.stopPropagation();
			}
			
			//reposition the panel if it's out of the client window, as errors will make the dialog size higher
			PopupPanel panel = (PopupPanel) getParent().getParent();
			if (panel.getAbsoluteTop() + panel.getOffsetHeight() > Window.getClientHeight()) {
				panel.setPopupPosition(panel.getAbsoluteLeft(), Window.getClientHeight() - panel.getOffsetHeight());
			}
			
    	} else {
    		//commit
    		inputProvider.save();
    		onCancelBtn(evt);
    	}
    }
    
    /**
     * Hide the popup where this inputdialog is contained in.
     * @param evt
     */
    @UiHandler("cancelBtn")
    void onCancelBtn(ClickEvent evt) {
    	((PopupPanel) getParent().getParent()).hide();
    }

    /**
     * Shows or hides the error message, and additional things like icons accordingly.
     * @param text
     * @param showError
     */
	private void updateError(String text, boolean showError) {
		errorPanel.setText(text);
    	errorPanel.setVisible(showError);
    	loadingIndicator.setVisible(!showError);
    	submitBtn.setEnabled(showError);
    	
    	if (!showError) {
    		inputProvider.clearErrorStyles();
    	}
	}
}