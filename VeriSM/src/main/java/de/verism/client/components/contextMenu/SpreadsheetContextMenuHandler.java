package de.verism.client.components.contextMenu;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.view.client.CellPreviewEvent;

import de.verism.client.components.panels.io.spreadsheet.data.DataCallback;
import de.verism.client.domain.Signal;

/**
 * Handler for the context menu of the signal tables on the left and right.
 * @author Daniel Kotyk
 *
 */
public class SpreadsheetContextMenuHandler extends BasicContextMenuHandler implements CellPreviewEvent.Handler<Signal> {
	//the context menu for cells
	private SpreadsheetContextMenu contextMenu;
	private DataCallback callback;
	
	public SpreadsheetContextMenuHandler(SpreadsheetContextMenu contextMenu, DataCallback callback) {
		super(contextMenu);
		this.contextMenu = contextMenu;
		this.callback = callback;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCellPreview(CellPreviewEvent<Signal> evt) {
		if (evt.getNativeEvent().getType().equals(BrowserEvents.CONTEXTMENU)) {
			callback.setSelected(evt.getValue());
			contextMenu.setSignal(evt.getValue());
			
			super.showContextMenu(evt.getNativeEvent());
		}
	}
}
