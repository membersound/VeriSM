package de.verism.client.domain.data;

import java.util.List;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import de.verism.client.domain.Signal;

/**
 * Wrapper for the whole data contained in the Spreadsheet to auto-reflect changes on the list.
 * @author Daniel Kotyk
 *
 */
public class SpreadsheetDataProvider {
	//this is the wrapper
	private ListDataProvider<Signal> dataProvider = new ListDataProvider<Signal>();
	
	/**
	 * Should only be initializable when providing data to be displayed.
	 * @param display
	 */
	public SpreadsheetDataProvider(HasData<Signal> display) {
		dataProvider.addDataDisplay(display);
	}
	
	/**
	 * Add a new signal to the {@link #dataProvider}.
	 * Should not be used to add signals iteratively. Use {@link #add(List<Signal>)} instead.
	 * @param signal
	 */
	public void add(Signal signal) {
		getList().add(signal);
		refresh();
	}
	
	/**
	 * Overloaded method for adding a bunch of signals at once without forcing refresh.
	 * Refresh is done asynchronous by gwt in the background thread.
	 * @param signals
	 */
	public void add(List<Signal> signals) {
		getList().addAll(signals);
	}
	
	/**
	 * Removes a signal from the list immediately.
	 * @param signal
	 */
	public void remove(Signal signal) {
		getList().remove(signal);
		refresh();
	}
	
	/**
	 * Force update to scroll new added item into view.
	 */
	private void refresh() {
		dataProvider.refresh();
	}
	
	/**
	 * Returns the underlying list.
	 * @return
	 */
	public List<Signal> getList() {
		return dataProvider.getList();
	}
	
	
	/**
	 * Returns the index of a signal.
	 * @param signal
	 * @return
	 */
	public int indexOf(Signal signal) {
		return getList().indexOf(signal);
	}

	/**
	 * Remove item on row index.
	 * @param keyboardSelectedRow
	 */
	public void remove(int keyboardSelectedRow) {
		dataProvider.getList().remove(keyboardSelectedRow);
	}
}
