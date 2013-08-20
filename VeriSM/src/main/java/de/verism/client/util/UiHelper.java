package de.verism.client.util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

/**
 * Simulates user actions on the browser interface.
 * @author Daniel Kotyk
 *
 */
public class UiHelper {
	
	/**
	 * Creates a native click event.
	 * @return a click event
	 */
	public static NativeEvent clickEvent() {
		return Document.get().createClickEvent(-1, 0, 0, 0, 0, false, false, false, false);
	}
	
	/**
	 * Creates a native double click event.
	 * @return a double click event
	 */
	public static NativeEvent doubleClickEvent() {
		return Document.get().createDblClickEvent(-1, 0, 0, 0, 0, false, false, false, false);
	}
	
	/**
	 * Creates a key down event for a given keycode.
	 * @param keyCode the key to press
	 * @return the keydown event
	 */
	public static NativeEvent keyDownEvent(int keyCode) {
		return Document.get().createKeyDownEvent(false, false, false, false, keyCode);
		
	}
	
	public static NativeEvent mouseOutEvent(Element element, int screenX, int screenY, int clientX, int clientY) {
		return Document.get().createBlurEvent();
	}
	
	/**
	 * Simulates a left click on the element. Dispatched to fire it save after other events.
	 * @param element the element to be clicked
	 */
	public static void click(final Element element) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				element.dispatchEvent(clickEvent());
			}
		});
	}
	
	/**
	 * Simulates a doubleclick on the element.
	 * @param element the element to be clicked
	 */
	public static void dblclick(final Element element) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				element.dispatchEvent(doubleClickEvent());
			}
		});
	}
}