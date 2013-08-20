package de.verism.client.domain;

import java.io.Serializable;

import com.google.gwt.user.client.DOM;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.canvas.shapes.visitors.CanvasVisitor;
import de.verism.client.canvas.shapes.visitors.Visitable;
import de.verism.client.canvas.shapes.visitors.Visitor;
import de.verism.client.components.contextMenu.HasContextMenu;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.dialog.editSignal.EditSignalPanel;

/**
 * This class represents the input and output ports/signals.
 * @author Daniel Kotyk
 *
 */
public class Signal implements Serializable, HasTextId, HasContextMenu, HasCondition, Visitable {
	//the vector name
	private String name = "";
	
	//if no value is entered, then a 1-Bit signal will be created
	private int bits = 1;
	
	//unique identifier used from spreadsheet do differ between the entries.
	//transient will skip this field on serialization, as the should only be used for spreadsheet internals.
	private transient String id = "";
	
	//the condition is not cached as a string representation of the id's
	//as then renaming would not be instantly reflected in the spreadsheets.
	private Condition condition = new Condition();
	
	/**
	 * Validates the user input for the new vector.
	 * @param input
	 * @return
	 */
	private boolean isValidInput(String input) {
		return input != null && !input.isEmpty();
	}
	
	/**
	 * Generate unique id for element tracking in spreadsheet.
	 * Must be called after project import.
	 */
	public void generateId() {
		id = DOM.createUniqueId();
	}

	/**
	 * Emptry constructor for serialization.
	 */
	private Signal() {}

	/**
	 * Constructor for 1-Bit signals.
	 * @param name2
	 */
	public Signal(String name) {
		generateId();
		this.name = name.trim();
	}
	
	/**
	 * Constructor for n-Bit signals.
	 * @param name
	 * @param bits
	 */
	public Signal(String name, String bits) {
		//call the overloaded constructor
		this(name); 
		if (isValidInput(bits.trim())) {
			this.bits = Integer.valueOf(bits.trim());
		}
	}
	

	/**
	 * 8 bit vector will render as 'name[7:0]', 1-bit just as 'name'
	 */
	public String toString() {
		return isVector() ? getPrettyVector() : getName();
	}
	
	/**
	 * Returns a pretty formatted vector as 'name[7:0]' if bits are > 1.
	 * @return
	 */
	private String getPrettyVector() {
		return "[" + (bits - 1) + ":0] " + name;
	}
	
	/**
	 * Helper if signal is a vector.
	 * @return
	 */
	public boolean isVector() {
		return bits > 1;
	}
	
	@Override
	public String getText() {
		return getName();
	}

	@Override
	public void setText(String text) {
		setName(text);
	}
	
	@Override
	public String getId() { return id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getBits() { return bits; }
	public void setBits(int bits) { this.bits = bits; }
	public void setCondition(Condition condition) { this.condition = condition; }
	@Override
	public Condition getCondition() { return condition; }

	@Override
	public IsInputProvider getContextMenu() {
		return new EditSignalPanel(this);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}
	@Override
	public <T> void accept(CanvasVisitor<T> visitor) {
		new UnsupportedOperationException("Not supported.");
	}
}
