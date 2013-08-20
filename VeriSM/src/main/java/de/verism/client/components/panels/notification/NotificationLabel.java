package de.verism.client.components.panels.notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Provides icon and text to be displayed in notification popup.
 * @author Daniel Kotyk
 *
 */
public class NotificationLabel extends Composite {
	interface Binder extends UiBinder<Widget, NotificationLabel> {}
	
	@UiField
	Image icon;
	
	@UiField
	InlineLabel text;
	
	@UiConstructor
	public NotificationLabel(String text, ImageResource icon) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		this.text.setText(text);
		this.icon.setResource(icon);
	}
}
