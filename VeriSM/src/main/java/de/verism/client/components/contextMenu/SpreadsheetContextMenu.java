package de.verism.client.components.contextMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.panels.io.spreadsheet.data.DataCallback;
import de.verism.client.domain.Signal;

/**
 * Context menu for the tables.
 * @author Daniel Kotyk
 *
 */
public class SpreadsheetContextMenu extends Composite implements ProvidesContextMenuHandler {
	interface Binder extends UiBinder<Widget, SpreadsheetContextMenu> {}

	@UiField
	Label label;
	
	@UiField
	MenuItem editMenu, deleteMenu, addMenu;

	private Signal signal;
	
	private SpreadsheetContextMenuHandler handler;
	
	@UiConstructor
	public SpreadsheetContextMenu(final DataCallback callback) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		handler = new SpreadsheetContextMenuHandler(this, callback);
		
		editMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.editSignal(signal, ((PopupPanel) getParent()).getPopupLeft(), ((PopupPanel) getParent()).getPopupTop());
			}
	    });
		
		deleteMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.removeSelectedSignal();
			}
	    });
		
		addMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.addNewSignal();
			}
	    });
	}
	
	public void setSignal(Signal signal) {
		this.signal = signal;
		setText(signal.getName());
	}
	
	private void setText(String text) {
		label.setText(text + ":");
	}
	
	@Override
	public SpreadsheetContextMenuHandler getHandler() { return handler; }
}
