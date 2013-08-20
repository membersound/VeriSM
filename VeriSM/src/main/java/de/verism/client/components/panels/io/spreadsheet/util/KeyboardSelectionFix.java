package de.verism.client.components.panels.io.spreadsheet.util;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.SingleSelectionModel;

import de.verism.client.domain.Signal;
import de.verism.client.domain.data.SpreadsheetDataProvider;

/**
 * Fixes the linking between keyboard and mouse selection on a {@link CellTable}.
 * The better way would be {@link KeyboardSelectionPolicy.BOUND_TO_SELECTION}, but has a bug. @see GWT issue #6310.
 * @author Daniel Kotyk
 *
 */
public class KeyboardSelectionFix implements KeyDownHandler {
	private DataGrid<Signal> dataGrid;
	private SpreadsheetDataProvider dataProvider;

	public KeyboardSelectionFix(DataGrid<Signal> dataGrid, SpreadsheetDataProvider dataProvider) {
		dataGrid.sinkEvents(Event.ONKEYDOWN);
		this.dataGrid = dataGrid;
		this.dataProvider = dataProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onKeyDown(KeyDownEvent event) {
		SingleSelectionModel<Signal> selectionModel = (SingleSelectionModel<Signal>) dataGrid.getSelectionModel();
		Signal object = null;
		
		//if there is a selection, modify keyboard selection up or down
		if (selectionModel.getSelectedObject() != null) {
			//the position of current selection
			int pos = dataGrid.getVisibleItems().indexOf(selectionModel.getSelectedObject());
			
			//switch according to the key pressed
			switch (event.getNativeEvent().getKeyCode()) {
				case KeyCodes.KEY_UP:
					if (pos > 0) {
						object = dataGrid.getVisibleItems().get(pos - 1);
					}
					break;
				case KeyCodes.KEY_DOWN:
					if (pos != dataGrid.getVisibleItems().size() - 1) {
						object = dataGrid.getVisibleItems().get(pos + 1);
						dataGrid.setKeyboardSelectedRow(dataProvider.getList().indexOf(object) - 1);
					}
					break;
			}
		//if there is no selection, set keyboard selection to the first entry
		} else {
			if (dataGrid.getVisibleItems().size() > 0) {
				object = dataProvider.getList().get(0);
//				dataGrid.setKeyboardSelectedRow(0);
			}
		}
		
		//if anything was modified: cancel the event
		if (object != null) {
			selectionModel.setSelected(object, true);
			event.stopPropagation();
			event.preventDefault();
		}
	}
}