package de.verism.client.components.panels.io.spreadsheet.cell;

import static com.google.gwt.dom.client.BrowserEvents.DBLCLICK;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

import de.verism.client.components.panels.io.spreadsheet.cell.render.CellStrategy;
import de.verism.client.util.UiHelper;

/**
 * A cell providing an {@link EditTextCell} that changes into edit mode on doubleclick (default is singleclick).
 * Displays a single editable label.
 * @author Daniel Kotyk
 *
 */
public class LabelEditTextCell extends VectorEditTextCell {
	public LabelEditTextCell(CellStrategy cellStrategy) {
		super(cellStrategy);
		initEvents();
	}
	
	//holds the events registred for this cell
	private static Set<String> consumedEvents;
	
	/**
	 * Adds a doubleclick event to the {@link EditTextCell}.
	 * This is the only way as {@link EditTextCell}. is designed very restrictively by GWT.
	 */
	private void initEvents() {
		consumedEvents = new HashSet<String>();
		consumedEvents.add(DBLCLICK);
		consumedEvents.addAll(super.getConsumedEvents());
	}
	
	/**
	 * Redirects the method to {@link #consumedEvents} to provide dblclick events.
	 */
	@Override
	public Set<String> getConsumedEvents() {
		return consumedEvents;
	}

	/**
	 * EditTextCell goes into edit mode on single click by default.
	 * As this application has a {@link Spreadsheet} component that marks by singleclick and goes to edit by doubleclick,
	 * the {@link LabelEditTextCell} is forced to change its behavior accordingly  to be consistent.
	 */
	@Override
	public void onBrowserEvent(Context context,
			Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		String type = event.getType();
		
		//change this to switch(type) when GWT supports JRE7
		if (!isEditing(context, parent, type)) {
			//prevent single clicks in non-edit mode
			if (type.equals(BrowserEvents.CLICK)) {
				return;
			}
			//on doubleclick: fire a single click event, as only single clicks can trigger edit mode on EditTextBox.
			//this way a dblclick is mapped to singleclick behaviour for the cell.
			if (type.equals(BrowserEvents.DBLCLICK)) {
				//here I do not dispatch the event (which would cause again an onBrowserEvent and thus prevent the single click),
				//but create a new event that is just handed to super.onBrowserEvent.
				event = UiHelper.clickEvent();
			}
		}

		super.onBrowserEvent(context, parent, value, event, valueUpdater);
	}
}