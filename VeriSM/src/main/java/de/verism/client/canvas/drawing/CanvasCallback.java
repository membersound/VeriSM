package de.verism.client.canvas.drawing;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.IsDrawable;
import de.verism.client.domain.State;

/**
 * Callback to handle inplace input on the canvas.
 * @author Daniel Kotyk
 *
 */
public interface CanvasCallback {
	/**
	 * Adds a new state to the canvas view.
	 */
	void addState();
	
	/**
	 * Creates edit dialog for the state name.
	 * @param y 
	 * @param x 
	 */
	void editDrawable(Drawable drawable, int x, int y);
	
	/**
	 * Gets the current selected {@link Drawable}.
	 * @return
	 */
	IsDrawable getSelection();

	/**
	 * Removes the drawable from canvas.
	 * @param selection
	 */
	void remove(Drawable drawable);

	/**
	 * Changes the initial status of a state.
	 * @param state the state to be modified
	 * @param initial if the state should be the initial state of the FSM
	 */
	void changeInitialState(State state, boolean initial);

	/**
	 * Refresh the canvas drawing area.
	 */
	void refresh();

	/**
	 * Shows are hides all conditions on all lines.
	 * @param showAllConditions
	 */
	void showLineConditions(boolean showAllConditions);
}
