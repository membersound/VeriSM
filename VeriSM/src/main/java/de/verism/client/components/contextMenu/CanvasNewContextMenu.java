package de.verism.client.components.contextMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasCallback;

/**
 * Context menu for the canvas to add new states on rightclick.
 * @author Daniel Kotyk
 *
 */
public class CanvasNewContextMenu extends Composite implements ProvidesContextMenuHandler {
	interface Binder extends UiBinder<Widget, CanvasNewContextMenu> {}

	@UiField
	MenuItem addMenu;

	private BasicContextMenuHandler handler;

	@UiConstructor
	public CanvasNewContextMenu(final CanvasCallback callback) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		handler = new CanvasNewContextMenuHandler(this, callback);

		addMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.addState();
			}
		});
	}

	@Override
	public BasicContextMenuHandler getHandler() {
		return handler;
	}
}
