package de.verism.client.canvas.shapes.rendering;

import java.io.Serializable;

import de.verism.client.canvas.drawing.Viewport;

/**
 * A class holding XY coordinates of a canvas point.
 * @author Daniel Kotyk
 *
 */
public class Point implements Serializable {

	//the point coordinates
	private double x, y;
	
	/**
	 * For serialization.
	 */
	private Point() {}

	/**
	 * Creates a new point in the canvas, respecting the viewport offset.
	 * (x + OFFSET * SCALE ) / SCALE = x / SCALE + OFFSET
	 * @param x
	 * @param y
	 */
	public Point(final double x, final double y) {
		this.x = x / Viewport.SCALE + Viewport.OFFSET_X;
		this.y = y / Viewport.SCALE + Viewport.OFFSET_Y;
	}
	
	/**
	 * Moves a point by offset length.
	 * @param dx the offset in x direction
	 * @param dy the offset in y direction
	 */
	public void moveBy(double dx, double dy) {
		x += dx / Viewport.SCALE;
		y += dy / Viewport.SCALE;
	}
	
	/**
	 * Moves the point to another starting point.
	 * Prevents that eg x might accidentally be moved without y.
	 * @param x
	 * @param y
	 */
	public void moveTo(double x, double y) {
		this.x = x / Viewport.SCALE + Viewport.OFFSET_X;
		this.y = y / Viewport.SCALE + Viewport.OFFSET_Y;
	}
	
	/**
	 * @see #moveTo(int, int)
	 * @param p
	 */
	public void moveTo(Point p) {
		moveTo(p.getX(), p.getY());
	}
	
	/**
	 * As any calculations in the app are done using these pointXY accessors,
	 * this is the perfect place to return the point coordinates relative to the initial canvas.
	 * 
	 * Example: if the canvas viewport offset is (-300, -300), and a rectangle has point (-100, -100).
	 * Normally this point would be outside the canvas and not be drawn. But due to the vieport offset,
	 * the point for the actual canvas is (200, 200). At this point the rectangle now has to be drawn
	 * for the moment.
	 * @return
	 */
	public double getX() {
		return (x - Viewport.OFFSET_X) * Viewport.SCALE;
	}
	
	public double getY() {
		return (y - Viewport.OFFSET_Y) * Viewport.SCALE;
	}
	
	public Point clone() {
		return new Point(x, y);
	}

	public boolean equals(final Point toCompare) {
		return x == toCompare.x && y == toCompare.y;
	}

	@Override
	public String toString() {
		return "(x="+x+", y="+y+")";
	}
}

