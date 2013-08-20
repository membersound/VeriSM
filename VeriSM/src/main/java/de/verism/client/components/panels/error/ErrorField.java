package de.verism.client.components.panels.error;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


/**
 * Error label field providing custom error message for displaying validation errors..
 * @author Daniel Kotyk
 *
 */
public class ErrorField extends Composite {
	interface Binder extends UiBinder<Widget, ErrorField> {}
	
	public ErrorField() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}
	
	/**
	 * The error style to be applied to invalid input fields.
	 */
	public static final String ERROR_STYLES = ErrorTextBoxResources.INSTANCE.css().errorStyle();
	
	/**
	 * Label displaying input errors when saving a tree item signal.
	 */
    @UiField
    HTML errorLbl; //HTML extends Label
	
    /**
     * Sets the error text and optionally creates linebreaks if text contains '\n' chars.
     * @param text
     */
	public void setText(String text) {
		//a gwt label cannot display linebreaks like "\n". it needs html tags like "<br /">
		SafeHtml errorMsg = new SafeHtmlBuilder().appendEscapedLines(text).toSafeHtml();
		errorLbl.setHTML(errorMsg);
	}
}
