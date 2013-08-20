package de.verism.client.components.contextMenu;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.util.PopupHelper;

/**
 * A simple {@link ContextMenuHandler} displaying a widget inside a {@link PopupPanel} on rightclick.
 * Preferably the widget is a context menu with defined callback actions.
 * @author Daniel Kotyk
 *
 */
public class BasicContextMenuHandler implements ContextMenuHandler {
	//the context menu popup to be shown on rightclick
	private PopupPanel popupMenu;
	
	//veto is used to track if mouse focus is only lost due to popupmenu.
	//which then should veto the mouseout, as otherwise the mouse would be assumed outside of the canvas and reset the selection.
	private boolean vetoMouseOut = false;
	
	/**
	 * @param widget the widget to be shown in the context popup (eg a menu)
	 */
	public BasicContextMenuHandler(Widget widget) {
		popupMenu = new PopupPanel(true, false);
		popupMenu.setWidget(widget);
		
		//true would cause the edit field to immediately change to unedit mode if animation is enabled.
		//alternativeliy: use popup.clear(); popup.hide(); this will then work with animation.
		popupMenu.setAnimationEnabled(false);
		
		initClickHandler(widget);
	}

	/**
	 * Hides the popup for any clicks on the widget (eg: selecting a {@link MenuItem} entry).
	 * Auto-sinks the click event on the widget. using addHandler() would add the handler without sinking the event, which means it would be have to be sunk from outside the handler.
	 * As a context menu makes no sense without a clickhandler, using addDomHandler() is right here.
	 * Must be registered on the Widget itself. Implementing ClickHandler on {@link BasicContextMenuHandler} would not be sufficient.
	 */
	private void initClickHandler(Widget widget) {
		widget.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent evt) {
				popupMenu.hide();
			}
		}, ClickEvent.getType());
		
		//prevent context menu inside the already showing context menu
		widget.addDomHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent evt) {
				cancelEvent(evt.getNativeEvent());
			}
		}, ContextMenuEvent.getType());
		
		//detect menu close to revert veto
		popupMenu.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				vetoMouseOut = false;
			}
		});
	}
	
	/**
	 * Triggered on rightclick.
	 */
	@Override
	public void onContextMenu(ContextMenuEvent evt) {
		showContextMenu(evt.getNativeEvent());
	}

	/**
	 * Overloads {@link #showContextMenu(int, int)} to handle an {@link NativeEvent} before.
	 * @param evt
	 */
	public void showContextMenu(NativeEvent evt) {
		vetoMouseOut = true;
		cancelEvent(evt);
		
		//get the click coordinates
		int left = evt.getClientX();
	    int top = evt.getClientY();
	       
	    //show context menu at click position
		showContextMenu(left, top);
	}
	
	/**
	 * Shows the context menu at a specific position.
	 * @param left
	 * @param top
	 */
	private void showContextMenu(int left, int top) {
		PopupHelper.show(popupMenu, left, top);
	}
	
	/**
	 * Prevent context menu on rightclick, and displays a custom context menu for the spreadsheet.
	 * @param column 
	 */
	public void cancelEvent(NativeEvent evt) {
		evt.preventDefault();
		evt.stopPropagation();
	}
	
	public boolean isVetoMouseOut() { return this.vetoMouseOut; }
}
