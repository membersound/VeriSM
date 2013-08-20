package de.verism.client.components.contextMenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.editTextBox.EditTextBox;

/**
 * Context menu for the project name.
 * @author Daniel Kotyk
 *
 */
public class ProjectNameContextMenu extends Composite implements ProvidesContextMenuHandler {
	interface Binder extends UiBinder<Widget, ProjectNameContextMenu> {}

	@UiField
	MenuItem editMenu;
	
	private BasicContextMenuHandler handler;
	
	@UiConstructor
	public ProjectNameContextMenu(final EditTextBox textBox) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		handler = new BasicContextMenuHandler(this);
		
		editMenu.setScheduledCommand(new Command() {
			public void execute() {
				textBox.toEdit();
			}
	    });
	}

	@Override
	public BasicContextMenuHandler getHandler() { return handler; }
}