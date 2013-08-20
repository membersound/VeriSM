
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

import de.verism.client.canvas.drawing.CanvasCallback;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.canvas.shapes.visitors.CanvasVisitor;
import de.verism.client.components.icons.IconMenuItem;
import de.verism.client.components.icons.IconResources;
import de.verism.client.domain.State;

/**
 * Provides the context menu for all canvas objects, like states(rectangles) and transitions(lines).
 * All in one class as the object instance is chosen by visitor pattern {@link CanvasVisitor}.
 * @author Daniel Kotyk
 *
 */
public class CanvasContextMenu extends Composite implements ProvidesContextMenuHandler, CanvasVisitor<Drawable> {
	interface Binder extends UiBinder<Widget, CanvasContextMenu> {}

	@UiField
	Label label;
	
	@UiField
	MenuItem editMenu, deleteMenu, startMenu, straightenLineMenu, toggleAllText;

	//the object to edit
	private Drawable drawable;
	
	//the handler belonging to this context menu.
	private CanvasContextMenuHandler handler;
	
	/**
	 * Initializes the context menu actions.
	 * @param callback
	 */
	@UiConstructor
	public CanvasContextMenu(final CanvasCallback callback) {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		handler = new CanvasContextMenuHandler(this, callback);
		
		//any drawable can be edited
		editMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.editDrawable(drawable, ((PopupPanel) getParent()).getPopupLeft(), ((PopupPanel) getParent()).getPopupTop());
			}
	    });
		
		//any drawable can be deleted
		deleteMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.remove(drawable);
			}
	    });
		
		//state is rectangle-specific
		startMenu.setScheduledCommand(new Command() {
			public void execute() {
				callback.changeInitialState((State) drawable.getFigure(), true);
			}
	    });
		
		//straightening a line is of course line specific
		straightenLineMenu.setScheduledCommand(new Command() {
			public void execute() {
				drawable.center();
				callback.refresh();
			}
    	});
		
		//shows or hides all conditions of all lines
		toggleAllText.setScheduledCommand(new Command() {
			public void execute() {
				showAllConditions = !showAllConditions;
				
				String menuText = showAllConditions ? "Hide" : "Show";
				toggleAllText.setHTML(IconMenuItem.prepareMenu(menuText + " All", IconResources.INSTANCE.showAll()));
				
				callback.showLineConditions(showAllConditions);
			}
	    });
	}
	
	private boolean showAllConditions = false;
	
	/**
	 * Sets the object that is to be edited.
	 * @param drawable
	 */
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
		setHeader(drawable.getFigure().getText());
		
		//enable specific menu based on drawable instance (implicit calls #visit())
		drawable.accept(this);
	}
	
	/**
	 * Sets the header title text of the context menu popup.
	 * @param text
	 */
	public void setHeader(String text) {
		label.setText(text + ":");
	}
	
	@Override
	public BasicContextMenuHandler getHandler() { return handler; }

	/**
	 * Enable state/rectangle dependent menu.
	 */
	@Override
	public void visit(Rectangle rectangle) {
		initMenus(true);
	}

	/**
	 * Enable line/transition dependent menu.
	 */
	@Override
	public void visit(Line line) {
		initMenus(false);
	}
	
	/**
	 * Helper to show and hide menus dependent of the object class.
	 * @param show
	 */
	private void initMenus(boolean show) {
		startMenu.setVisible(show);
		straightenLineMenu.setVisible(!show);
		toggleAllText.setVisible(!show);
	}
}
