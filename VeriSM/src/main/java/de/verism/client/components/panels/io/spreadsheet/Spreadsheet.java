package de.verism.client.components.panels.io.spreadsheet;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.shapes.visitors.DeleteValidationService;
import de.verism.client.canvas.shapes.visitors.Visitor;
import de.verism.client.components.contextMenu.BasicContextMenuHandler;
import de.verism.client.components.contextMenu.DataGridContextMenu;
import de.verism.client.components.contextMenu.SpreadsheetContextMenu;
import de.verism.client.components.dialog.edit.EditorDialog;
import de.verism.client.components.dialog.editSignal.EditSignalPanel;
import de.verism.client.components.dialog.login.ResetView;
import de.verism.client.components.panels.io.spreadsheet.cell.VectorEditTextCell;
import de.verism.client.components.panels.io.spreadsheet.cell.render.BitsCellStrategy;
import de.verism.client.components.panels.io.spreadsheet.cell.render.CellStrategy;
import de.verism.client.components.panels.io.spreadsheet.cell.render.ConditionCellStrategy;
import de.verism.client.components.panels.io.spreadsheet.cell.render.NameCellStrategy;
import de.verism.client.components.panels.io.spreadsheet.data.DataCallback;
import de.verism.client.components.panels.io.spreadsheet.util.KeyboardSelectionFix;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.Signal;
import de.verism.client.domain.data.Cache;
import de.verism.client.domain.data.SpreadsheetDataProvider;
import de.verism.client.util.BooleanKeyCodes;
import de.verism.client.util.ClassNameResolver;
import de.verism.client.util.PopupHelper;
import de.verism.client.util.UiHelper;
import de.verism.client.util.UniqueName;
import de.verism.client.validation.ValidationError;

//maybe later ranem to "SignalEditor" if old implementation is deleted
public class Spreadsheet extends Composite implements DataCallback, ResetView {
	interface Binder extends UiBinder<Widget, Spreadsheet> {}

	@UiField(provided = true)
	DataGrid<Signal> dataGrid;
	
	//wrapper panel around the spreadsheet to receive doubleclicks
	@UiField
	FlowPanel panel;
	
	@UiField(provided = true)
	Footer footer;
	
	//wrapper for the data. should not be accessible from outside the spreadsheet
	private SpreadsheetDataProvider dataProvider;
	
	//if condition field should be shown (only for outputs)
	private boolean showCondition;
	
	@UiConstructor
	public Spreadsheet(String panelName, boolean showCondition) {
		this.showCondition = showCondition;
		dataGrid = new DataGrid<Signal>(Integer.MAX_VALUE, CellTableResource.INSTANCE, KEY_PROVIDER);
		dataProvider = new SpreadsheetDataProvider(dataGrid);
		footer = new Footer(this);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));

		//init the grid
		initStyles(panelName);
		initSelection();
		initSorting();
		initContextMenu();
	}
	
	/**
	 * Initially creates the context menu linking.
	 */
	private void initContextMenu() {
		//register context menu for datagrid entries
		BasicContextMenuHandler contextMenuHandler = new SpreadsheetContextMenu(this).getHandler();
		dataGrid.addCellPreviewHandler((Handler<Signal>) contextMenuHandler);
		dataGrid.sinkEvents(Event.ONCONTEXTMENU);

		//handle clicks on the datagrid when not clicking on an existing entry
		BasicContextMenuHandler gridContextMenuHandler = new DataGridContextMenu(this).getHandler();
		panel.addDomHandler(gridContextMenuHandler, ContextMenuEvent.getType());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addNewSignal() {
		Signal signal = new Signal(UniqueName.forSignal());
		dataProvider.add(signal);
		dataGrid.getRowElement(dataProvider.indexOf(signal)).scrollIntoView();

		//change new signal to edit mode
		toEditMode(dataProvider.indexOf(signal), 0);
	}

	/**
	 * Sets a cell into edit mode. Uses scheduler to prevent click interference with other events.
	 * 1st click selects the cell, 2nd click changes it to edit mode
	 * @param row
	 * @param col
	 */
	private void toEditMode(final int row, final int col) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				clickEntry(row, col);
				clickEntry(row, col);
			}
		});
	}

	/**
	 * Helper to click on an entry in the spreadsheet.
	 * @param row
	 * @param col
	 */
	private void clickEntry(int row, int col) {
		dataGrid.getRowElement(row).getCells().getItem(col).dispatchEvent(UiHelper.clickEvent());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeSelectedSignal() {
		if (getSelection() == null) return;

		//prevent focus loss when footer controls are clicked
		if (dataGrid.getVisibleItems().size() > 0) {
			dataGrid.setFocus(true);
		}
		
		Visitor<ValidationError> visitor = new DeleteValidationService();
		ValidationError error = getSelection().accept(visitor);
		
		//stop deletion on error
		if (error.hasErrors()) {
			new NotifierPopup(error.getMessage(), NotifierState.ERROR);
			return;
		}
		
		dataProvider.remove(getSelection());
		SingleSelectionModel model = ((SingleSelectionModel<Signal>) dataGrid.getSelectionModel());
		model.clear();
	}
	

	/**
	 * Returns the selected object.
	 * @return
	 */
	private Signal getSelection() {
		//return dataGrid.getKeyboardSelectedRow();
		return ((SingleSelectionModel<Signal>) dataGrid.getSelectionModel()).getSelectedObject();
	}
	
	/**
	 * Initialize the cell table styles for spreadsheet/excel like appearance.
	 * @param panelName
	 */
	private void initStyles(String panelName) {
		//default column width
		int nameWidth = 75, bitsWidth = 100 - nameWidth, conditionWidth = 0;
		
		//modify col width dynamically for output sheet
		if (showCondition) {
			nameWidth = 45;
			bitsWidth = 20;
			conditionWidth = 100 - nameWidth - bitsWidth;
		}
		
		initColumn(nameColumn, panelName, nameWidth);
		initColumn(bitsColumn, "Bits", bitsWidth);
		
		//render condition as last column (for outputs)
		if (showCondition) {
			initColumn(conditionColumn, "Condition", conditionWidth);
		}

		// header + footer do not depend on the content, thus disabling refresh improves performance
		dataGrid.setAutoHeaderRefreshDisabled(true);
		dataGrid.setAutoFooterRefreshDisabled(true);
	}
	
	/**
	 * Helper to initialize the columns.
	 * @param col the column to attach to the table
	 * @param name the header name of the column
	 * @param size the width of the column (in %)
	 */
	private void initColumn(Column<Signal, String> col, String name, int size) {
		dataGrid.addColumn(col, name);

		// apply custom column width
		dataGrid.setColumnWidth(col, size + "%");
		
		// text alignment
		col.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	}
	
	/**
	 * Populate initial data.
	 */
	private void initSelection() {
		dataGrid.setSelectionModel(new SingleSelectionModel<Signal>());
		dataGrid.setEmptyTableWidget(new Label("No Signals yet."));
		
		//bind keyboard to mouse selection (exchange if gwt issue #6310 is solved)
		//dataGrid.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		dataGrid.addDomHandler(new KeyboardSelectionFix(dataGrid, dataProvider), KeyDownEvent.getType());
	}
	
	/**
	 * Initialize sorting ability for the columns.
	 * @return
	 */
	private ListHandler<Signal> initSorting() {
		// sorting
		ListHandler<Signal> sortHandler = new ListHandler<Signal>(dataProvider.getList());
		dataGrid.addColumnSortHandler(sortHandler);
		nameColumn.setSortable(true);
		bitsColumn.setSortable(true);
		
		sortHandler.setComparator(nameColumn, NameCellStrategy.comparator);
		sortHandler.setComparator(bitsColumn, BitsCellStrategy.comparator);

		return sortHandler;
	}
	
	/**
	 * Fetch the whole table data for export.
	 * Provides data access to the list without exposing the {@link #dataProvider} to the outside.
	 * @return
	 */
	public List<Signal> getData() {
		return dataProvider.getList();
	}

	/**
	 * Initial feed new data from import.
	 * @param signals
	 */
	public void setData(List<Signal> signals) {
		dataProvider.add(signals);
	}
	
	/**
	 * Column displaying the vector name.
	 */
	private Column<Signal, String> nameColumn = new SpreadsheetColumn(
			new VectorEditTextCell(new NameCellStrategy()));
	
	/**
	 * Column displaying the vector bits.
	 */
	private Column<Signal, String> bitsColumn = new SpreadsheetColumn(
			new VectorEditTextCell(new BitsCellStrategy()));
	
	/**
	 * Column displaying the output transition.
	 */
	private Column<Signal, String> conditionColumn = new SpreadsheetColumn(
			new VectorEditTextCell(new ConditionCellStrategy()));
	  
	/**
	 * Sets a cell to edit mode from context menu.
	 */
	@Override
	public void editSignal(Signal value, int left, int top) {
		EditSignalPanel inputs = (EditSignalPanel) value.getContextMenu();
		inputs.setShowCondition(showCondition);
		
		DialogBox dialog = new EditorDialog(inputs);
		dialog.setText("Edit " + ClassNameResolver.getSimpleName(value) + ": " + value.toString());
		
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			/**
			 * Refreshes the datagrid when input popup is closed.
			 * @param event
			 */
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refresh();
			}
		});
		
		PopupHelper.show(dialog, left, top);
	}

	@Override
	public void setSelected(Signal signal) {
		dataGrid.getSelectionModel().setSelected(signal, true);
	}
	
	
	 /**
	   * The key provider that allows us to identify Contacts even if a field
	   * changes. We identify contacts by their unique ID.
	   */
	  private static final ProvidesKey<Signal> KEY_PROVIDER = new ProvidesKey<Signal>() {
			@Override
			public Object getKey(Signal item) {
				return item.getId();
			}
	  };
	
	/**
	 * Defines the column for the spreadsheet with custom behaviour to mouse and key events.
	 * Strategy of how to handle each entry is provided by the {@link #cellStrategy}.
	 * @author Daniel Kotyk
	 *
	 */
	private class SpreadsheetColumn extends Column<Signal, String> {
		
		private CellStrategy cellStrategy = ((VectorEditTextCell) getCell()).getCellStrategy();
		
		public SpreadsheetColumn(Cell<String> cell) {
			super(cell);
			initFieldUpdater();
		}
		
		private void initFieldUpdater() {
			//adds a field updater to be notified when the user changes values
		    //also validates the new input values.
			setFieldUpdater(new FieldUpdater<Signal, String>() {
				@Override
				public void update(int index, Signal signal, String value) {
					//prevent empty names for vector
					ValidationError error = cellStrategy.isInputValid(value);
					if (error.hasErrors()) {
						//prevent messages on simple selection of a cell, as selection will also cause validation
						if (!value.equals(cellStrategy.getOldValue(signal))) {
							//display error message
							new NotifierPopup(error.getMessage(), NotifierState.WARN);
						}
						
						//revert last entered data
						value = cellStrategy.getOldValue(signal);
					}
					//clear must be applied to viewdata, otherwise new values will not be directly updated on {@link dataGrid#redraw()}
					((EditTextCell) getCell()).clearViewData(KEY_PROVIDER.getKey(signal));
					
					//push the changes into the signal
					cellStrategy.push(signal, value);
					
					//redraw the table with the new data
					refresh();
					
					//if this impl is the input spreadsheet, update the output sheet
					if (!showCondition) {
						Cache.get().getOutputPanel().refresh();
						//also refresh the canvas as input signals may be renamed
						CanvasArea.get().refresh();
					}
				}
			});
		}
		
		@Override
		public String getValue(Signal signal) {
			return cellStrategy.getValue(signal);
		}
		
		/**
		 * Handles cell add and deletion key events.
		 */
	    @Override
		public void onBrowserEvent(Context context, Element elem,
				Signal object, NativeEvent event) {
	    	//cell delete should only happen if not in edit mode,
	    	//and only for keyup events (to prevent multiple key executions while user keeps the key pressed)
	    	if (!getCell().isEditing(context, elem, getValue(object)) && event.getType().equals(BrowserEvents.KEYUP)) {
	    		int key = event.getKeyCode();
				//for delete key, if object is the selected: delete the whole row
				if (key == KeyCodes.KEY_DELETE && dataGrid.getSelectionModel().isSelected(object)) {
					removeSelectedSignal();
					return;
				}
				
				//for add keys: add new entries
				if (key == BooleanKeyCodes.VK_ADD || key == BooleanKeyCodes.VK_ADD_NUM || key == BooleanKeyCodes.VK_PLUS) {
					addNewSignal();
				}
	    	}
			
			super.onBrowserEvent(context, elem, object, event);
		}
	}

	/**
	 * Redraws the data grid (used for updating after any names have changed eg on the canvas states, to refresh the condition names).
	 */
	public void refresh() {
		dataGrid.redraw();
	}

	@Override
	public void reset() {
		dataProvider = new SpreadsheetDataProvider(dataGrid);
	}
}
