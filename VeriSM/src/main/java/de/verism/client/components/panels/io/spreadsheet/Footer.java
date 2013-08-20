package de.verism.client.components.panels.io.spreadsheet;

import java.util.ArrayList;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.panels.io.spreadsheet.data.DataCallback;

public class Footer extends Composite {
	interface Binder extends UiBinder<Widget, Footer> {}
	
	//footer will provide control elments for the spreadsheet.
	//must be in a single grid as otherwise it would additionally sort the columns on click, which should only happen on headers.
	@UiField
	DataGrid<String> footer;
	
	//the callback for the footer actions
	private DataCallback callback;
	
	/**
	 * Footer should only be constructible providing a callback.
	 * @param callback
	 */
	@UiConstructor
	public Footer(DataCallback callback) {
		this.callback = callback;
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		initFooter();
	}
	
	/**
	 * Construct the footer.
	 * We cannot add the footer as a {@link Header} element to the {@link #dataGrid} as this
	 * would result in sorting actions when clicking on the footer.
	 * The footer itself should only provide CRUD actions on the list.
	 * 
	 * Therefore a dummy datagrid {@link #footer} is created, which remains an empty {@link DataGrid}
	 * and only shows its footer. This way the {@link #dataProvider} can be updated independently from the footer.
	 */
	private void initFooter() {
		//create grid columns
		Column<String, String> colFooterAdd = createEmptyColumn();
		Column<String, String> colFooterEmpty = createEmptyColumn();
		Column<String, String> colFooterRemove = createEmptyColumn();
		
		//define sized to prevent clicks on empty space of the footer labels.
		//by using a big empty colFooterEmpty without a footer element, it pushes the "+" "-" footer
		//elements to the edges, making them small and thus only take actions onto the text itself.
		footer.setColumnWidth(colFooterAdd, "15%");
		footer.setColumnWidth(colFooterEmpty, "70%");
		footer.setColumnWidth(colFooterRemove, "15%");
		
		ClickableTextCell cellAddItem = new ClickableTextCell() {
			@Override
			protected void onEnterKeyDown(Context context, Element parent,
					String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
				callback.addNewSignal();
			}
		};
		
		ClickableTextCell cellRemoveItem = new ClickableTextCell() {
			@Override
			protected void onEnterKeyDown(Context context, Element parent,
					String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
				callback.removeSelectedSignal();
			}
		};

		//create footers by adding them as 2nd element. 'null' means no header here
		footer.addColumn(colFooterAdd, null, createFooter(cellAddItem, "+"));
		footer.addColumn(colFooterEmpty);
		footer.addColumn(colFooterRemove, null, createFooter(cellRemoveItem, "-"));
		
		//define empty data
		footer.setAutoHeaderRefreshDisabled(true);
		footer.setAutoFooterRefreshDisabled(true);
		footer.setRowData(new ArrayList<String>());
	}
	
	/**
	 * Create a footer element.
	 * @param cell the cell for which the footer element should be created
	 * @param text the text to display for the footer element
	 * @return the header/footer element (a footer is just a header, but added as 2nd param on the datagrid)
	 */
	private Header<?> createFooter(Cell<String> cell, final String text) {
		return new Header<String>(cell) {
			@Override
			public String getValue() {
				return text;
			}

			//apply hand curser for footers
			{	
				setHeaderStyleNames("footer");
			}
		};
	}

	/**
	 * Create column which never renders a value.
	 * Used for creating the footer.
	 * @return
	 */
	private Column<String, String> createEmptyColumn() {
		return new Column<String, String>(new TextCell()) {
			@Override
			public String getValue(String object) {
				return "";
			}
		};
	}
}
