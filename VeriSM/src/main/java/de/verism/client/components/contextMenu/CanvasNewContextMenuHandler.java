package de.verism.client.components.contextMenu;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasCallback;

/**
 * Context menu handler for the canvas.
 * The purpose in addition to {@link BasicContextMenuHandler} is the prevention of a context menu for empty space in the canvas.
 * @author Daniel Kotyk
 *
 */
public class CanvasNewContextMenuHandler extends BasicContextMenuHandler {
	private CanvasCallback callback;
	
	public CanvasNewContextMenuHandler(Widget widget, CanvasCallback callback) {
		super(widget);
		this.callback = callback;
	}
	
	/**
	 * Prevents context add-menu if a figure was selected in the canvas.
	 */
	@Override
	public void showContextMenu(NativeEvent evt) {
		if (evt.getButton() == NativeEvent.BUTTON_RIGHT && callback.getSelection() != null) {
			super.cancelEvent(evt);
			return;
		}
		super.showContextMenu(evt);
	}
}
