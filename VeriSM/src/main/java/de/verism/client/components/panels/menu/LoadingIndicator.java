package de.verism.client.components.panels.menu;

import com.google.gwt.user.client.ui.PopupPanel;

import de.verism.client.components.panels.notification.NotificationLabel;
import de.verism.client.components.panels.notification.NotificationResources;

/**
 * Displays a loading indicator popup next to the user click position.
 * Used during project and verilog export as feedback for the user.
 * @author Daniel Kotyk
 *
 */
public class LoadingIndicator {
	private static PopupPanel popup;
	private static NotificationLabel icon = new NotificationLabel(
			"Please wait...", NotificationResources.INSTANCE.loadingIcon());
	
	/**
	 * Helper for loading inidicator initialisation.
	 */
	private static void initPopup() {
		//no auto hide, not modal
		popup = new PopupPanel(false, false);
		popup.setWidget(icon);
	}
	
	/**
	 * Displays the loading indicator at a specific position
	 * (this will mainly be the mouseclick position).
	 * @param x the xcoordinate
	 * @param y  the ycoordinate
	 */
	public static void show(int x, int y) {
		initPopup();		
		popup.setPopupPosition(x, y);
		popup.show();
	}
	
	/**
	 * Hides the popup again.
	 */
	public static void hide() {
		popup.hide();
	}
}
