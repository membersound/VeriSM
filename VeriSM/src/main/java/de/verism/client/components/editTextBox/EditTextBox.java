package de.verism.client.components.editTextBox;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

import de.verism.client.components.panels.io.spreadsheet.cell.render.NameCellStrategy;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.util.UiHelper;

/**
 * 
 * Provides inplace editing for a single line text label. Trigger by doubleclick. Also has a context menu.
 * Implements {@link HasText} for providing uibinder a text="" text setter, and also for easy jsonDTO (de)serialisation.
 * Implements {@link HasContextMenuHandlers} to provide rightclick menu on the label.
 * @author Daniel Kotyk
 */
public class EditTextBox extends Composite implements HasText, HasContextMenuHandlers {
	interface Binder extends UiBinder<Widget, EditTextBox> {};
	
	//size of the input field
	public static final int BOX_SIZE = 140;
	
	@UiField(provided = true)
    CellWidget<String> cell;
	
	/**
	 * The key provider that allows us to identify Contacts even if a field
	 * changes. We identify contacts by their unique ID.
	 */
	private static final ProvidesKey<String> KEY_PROVIDER = new ProvidesKey<String>() {
		@Override
		public Object getKey(String item) {
			return item;
		}
	};
	
	/**
	 * Takes a Cell to render the edited text.
	 * @param editTextCell
	 */
	@UiConstructor
	public EditTextBox(EditTextCell editTextCell) {
		cell = new CellWidget<String>(editTextCell, NameCellStrategy.EMPTY, KEY_PROVIDER);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		initValueChangeHandler();
	}
	
	/**
	 * Change Handler to prevent empty project names (as this would result in never again editable empty label).
	 */
	private void initValueChangeHandler() {
		ValueChangeHandler<String> vch = new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				//simple not empty validation
				if (event.getValue().trim().isEmpty()) {
					new NotifierPopup("Name must not be empty", NotifierState.WARN);
					setText(NameCellStrategy.EMPTY);
				}
			}
		};
		cell.addValueChangeHandler(vch);
	}

	/**
	 * Changes the label to edit mode.
	 */
	public void toEdit() {
		UiHelper.dblclick(cell.getElement());
	}
	
	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return addDomHandler(handler, ContextMenuEvent.getType());
	}

	@Override
	public String getText() {
		return cell.getValue();
	}

	@Override
	public void setText(String text) {
		// setting the text programmatically must force an update and refresh of the input field.
		// this is similar to {@link Spreadsheet.SpreadsheetColumn}
		((EditTextCell) cell.getCell()).clearViewData(KEY_PROVIDER.getKey(text));
		cell.getCell().setValue(new Context(0, 0, KEY_PROVIDER.getKey(text)), cell.getElement(), text);
		cell.setValue(text, false, true);
	}
}