package de.verism.client.canvas.shapes;

import com.google.gwt.user.client.ui.HasText;

/**
 * Each class that implements this interface provides an id for identifying the object on the client side.
 * Id is a string as {@link #DOM.createUniqueId()} will be used, which returns a unique string for the time of the client session.
 * UUID cannot be used as GWT does not support it.
 * 
 * This class is needed for bidirectional queries: from id's to names, and vice versa.
 * Therefore an interface is needed which provides both the id and the text.
 * Though inheritance may be not the right term here (as {@link #HasTextId} "is not a" {@link #HasText}), but as Java does not provide
 * multiple inheritance this is the only option.
 * 
 * @author Daniel Kotyk
 *
 */
public interface HasTextId extends HasText {
	/**
	 * Id's should only be retrievable, no setter.
	 * Use {@link #generateId()} to create a new id.
	 * @return
	 */
	String getId();
	
	/**
	 * Generate unique id for element tracking in spreadsheet.
	 * Must be called after project import, as objects are exported without id's.
	 * Id's only matter for visual tracking, not for the domain data itself.
	 */
	void generateId();
}
