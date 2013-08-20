package de.verism.client.util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.widget.client.TextButton;

/**
 * Fixes GWT issue #1849.
 * 
 * Helper for focusing a focusable widget, like a button.
 * It fixes focus problems due to gwt bug issue #1849;
 * 
 * This is an own class as it is only introduced due to the gwt bug.
 * When it is fixed, it may be safely removed without affecting other classes.
 * 
 * @author Daniel Kotyk
 */
public class FocusHelper {

	/**
	 * Focuses the widget.
	 * @param widget
	 */
	public static void focus(final FocusWidget widget) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				widget.setFocus(true);
			}
		});
	}
	
	/**
	 * Overloads to also provide the same access to {@link Focusable}s, like {@link TextButton}.
	 * @param focusable
	 */
	public static void focus(final Focusable focusable) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				focusable.setFocus(true);
			}
		});
	}
}
