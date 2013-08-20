package de.verism.client.components.panels.notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 * Resource provider for the notification popup.
 * @author Daniel Kotyk
 * 
 */
public interface NotificationResources extends ClientBundle {
	public static final NotificationResources INSTANCE = GWT.create(NotificationResources.class);

	//the css file containing the style
	@Source("notification.css")
	Style css();
	
	//the class inside the css file defining the style of the error textbox
	interface Style extends CssResource {
		@ClassName("bubble")
		String bubbleStyle();
		
		@ClassName("bubble-success")
		String success();
		
		@ClassName("bubble-warn")
		String warn();
		
		@ClassName("bubble-error")
		String error();
	}
	
	//link the icons to include them in the JS ClientBundle, and make them referencable by program code
	@Source("successIcon.png")
	@ImageOptions(width = 16)
	ImageResource successIcon();
	
	@Source("warnIcon.png")
	@ImageOptions(width = 16)
	ImageResource warnIcon();
	
	@Source("errorIcon.png")
	@ImageOptions(width = 16)
	ImageResource errorIcon();
	
	@Source("loadingIcon.gif")
	@ImageOptions(width = 16)
	ImageResource loadingIcon();
}
