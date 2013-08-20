package de.verism.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * Helper to display a popup menu at a specific position.
 * @author Daniel Kotyk
 *
 */
public class PopupHelper {
	/**
	 * Take care that contextmenu popup is always inside the browser window.
	 * @param panel the panel to show
	 * @param left the x coordinate
	 * @param top the y coordinate
	 */
	public static void show(final PopupPanel panel, final int left, final int top) {
		panel.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int x = left;
				int y = top;
				
				if (left + offsetWidth > Window.getClientWidth()) {
					x = Window.getClientWidth() - offsetWidth;
				}
				if (top + offsetHeight > Window.getClientHeight()) {
					y = Window.getClientHeight() - offsetHeight;
				}
				
				panel.setPopupPosition(x, y);
			}
		});
	}
}
