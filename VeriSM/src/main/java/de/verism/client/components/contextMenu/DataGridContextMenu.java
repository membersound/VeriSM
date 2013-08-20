package de.verism.client.components.contextMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.panels.io.spreadsheet.data.DataCallback;

/**
 * Context menu for clicks in the datagrid when not selecting an existing entry.
 * Thus this should simply provide the option to add a new entry.
 * @author Daniel Kotyk
 *
 */
public class DataGridContextMenu extends Composite implements ProvidesContextMenuHandler {
	interface Binder extends UiBinder<Widget, DataGridContextMenu> {}

	@UiField
	MenuItem addMenu;
	
	private BasicContextMenuHandler handler;
	
	@UiConstructor
	public DataGridContextMenu(final DataCallback callback) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		handler = new BasicContextMenuHandler(this);
		
		addMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.addNewSignal();
			}
	    });
	}

	@Override
	public BasicContextMenuHandler getHandler() { return handler; }
}