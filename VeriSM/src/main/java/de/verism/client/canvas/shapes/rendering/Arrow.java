package de.verism.client.canvas.shapes.rendering;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.Drawable;

/**
 * Draw the arrow head on a bezier curve.
 * Arrow has as a static method as it will never be tracked as an object in the canvas. Similar to the text inside the rectangles.
 * @author Daniel Kotyk
 *
 */
public class Arrow {
	//used for altering the arrow point determination
	private static final int SIGN_POS = +1;
	private static final int SIGN_NEG = -1;
	
	//size of the arrow head
	private static final double ARROW_SIZE = Drawable.DEFAULT_BORDER_SIZE * 2;
	
	/**
	 * Draws an arrow head.
	 * @param ctx the context to draw on
	 * @param e the end point for the arrow tip
	 * @param e 
	 * @param angle the angle of the line where the arrow should be drawn on
	 */
	public static void draw(Context2d ctx, Point s, Point e, double angle) {
		ctx.moveTo(e.getX(), e.getY());

	    //set arrow start back towards the start point
	    Point arrowStart = new Point(
		    Math.round(e.getX() - ARROW_SIZE * Viewport.SCALE * Math.cos(angle)),
		    Math.round(e.getY() - ARROW_SIZE * Viewport.SCALE * Math.sin(angle)));
	        
	    //get the points that define the arrow corners
	    Point arrowTop = arrowPoint(arrowStart, angle, SIGN_POS);
	    Point arrowBottom = arrowPoint(arrowStart, angle, SIGN_NEG);

	    //draw the arrow head
	    ctx.lineTo(arrowTop.getX(), arrowTop.getY());
	    ctx.lineTo(arrowBottom.getX(), arrowBottom.getY());
	    ctx.lineTo(e.getX(), e.getY());
	}
	
	/**
	 * Returns one of the arrow points by subtracting the arrowHeadSize from the endpoint along the unit vector.
	 * @param e the arrow tip
	 * @param unitVec
	 * @param sign
	 * @return
	 */
	private static Point arrowPoint(Point arrowStart, double angle, double sign) {
		double px = Math.round(arrowStart.getX() - ARROW_SIZE * Viewport.SCALE * Math.cos(angle - sign * Math.PI/2));
		double py = Math.round(arrowStart.getY() - ARROW_SIZE * Viewport.SCALE * Math.sin(angle - sign * Math.PI/2));
        
        return new Point(px, py);
	}
}
