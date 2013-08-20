package de.verism.client.canvas.shapes;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.shapes.rendering.Point;
/**
 * Tracks the position of any drawable element for the canvas, and thereby defines drag and drop behavior.
 * @author Daniel Kotyk
 */
public interface IsDrawable {
	/**
	 * Draw the created figure to the canvas area.
	 * @param ctx
	 */
	void draw(Context2d ctx);
	
	Point getPosStart();
	Point getPosEnd();
}
