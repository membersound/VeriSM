package de.verism.client.canvas.scrolling.arrow;

import com.google.gwt.touch.client.Point;

/**
 * Represents one of 8 possible directions on the canvas edge to provide scrolling on MouseOver.
 * @author Daniel Kotyk
 *
 */
public enum Direction {
	//unit vectors defining the scrolling direction
	NORTH_WEST(-1,-1),	NORTH(0,-1), 	NORTH_EAST(1,-1),
	WEST(-1,0),			DEFAULT(0,0),	EAST(1,0),
	SOUTH_WEST(-1,1), 	SOUTH(0,1),	SOUTH_EAST(1,1);
	
	//initial constructor for the enum values
	private Direction(int x, int y) {
		this.point = new Point(x, y);
	}
	
	private Point point; 
	public Point getPoint() { return point; }
}