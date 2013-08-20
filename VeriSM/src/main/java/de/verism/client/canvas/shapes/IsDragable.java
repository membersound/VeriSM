package de.verism.client.canvas.shapes;



/**
 * Defines an object being able to drag and drop on the canvas.
 * Eg a Text is not {@link IsDragable}, but {@link Drawable}. Thus a text cannot be dragged alone and cannot be selected.
 * @author Daniel Kotyk
 *
 */
public interface IsDragable {
	
	/**
	 * Determine if a click is inside or outside of the figure.
	 * @param x the mouse X coordinate
	 * @param y the mouse Y coordinate
	 * @return if click was inside the figure
	 */
	public abstract boolean isInside(double x, double y);
	
	/**
	 * Moves the Dragable to a new point.
	 * @param x the new x point
	 * @param y the new y point
	 */
	public abstract void moveTo(double x, double y);
}
