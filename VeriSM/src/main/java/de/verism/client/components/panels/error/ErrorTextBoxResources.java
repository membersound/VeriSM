package de.verism.client.components.panels.error;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resource provider for the error TextBox styles and images.
 * @author Daniel Kotyk
 * 
 */
public interface ErrorTextBoxResources extends ClientBundle {
	public static final ErrorTextBoxResources INSTANCE = GWT.create(ErrorTextBoxResources.class);

	//the error icon displayed to the left of the textbox
	@Source("errorIcon.gif")
	ImageResource errorIcon();
	
	//the css file containing the style
	@Source("ErrorTextBox.css")
	Style css();
	
	//the class inside the css file defining the style of the error textbox
	interface Style extends CssResource {
		@ClassName("gwt-TextBox-error")
		String errorStyle();
	}
}
