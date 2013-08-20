package de.verism.client.components.panels.notification;

import com.google.gwt.resources.client.ImageResource;

/**
 * Represents a notification style with specific color and icon.
 * @author Daniel Kotyk
 *
 */
public enum NotifierState {
	SUCCESS(NotificationResources.INSTANCE.css().success(), NotificationResources.INSTANCE.successIcon()),
	WARN(NotificationResources.INSTANCE.css().warn(), NotificationResources.INSTANCE.warnIcon()),
	ERROR(NotificationResources.INSTANCE.css().error(), NotificationResources.INSTANCE.errorIcon());
	
	private NotifierState(String style, ImageResource icon) {
		this.style = style;
		this.icon = icon;
	}

	private String style;
	private ImageResource icon;
	
	public String getStyle() { return style; }
	public ImageResource getIcon() { return icon; }
}