package de.verism.client.canvas.drawing;

import com.google.gwt.user.client.Window;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.rendering.Point;

/**
 * Helper class to manage the Viewport.
 * The "Viewport" is defined as the portion of the canvas area that is currently visible to the user.
 * It has an offset to indicate the current object position relative to the initial canvas starting coordinates.
 * @author Daniel Kotyk
 *
 */
public class Viewport {
	//tracks the global viewport offset. it is always the unscaled value!
	public static double OFFSET_X = 0;
	public static double OFFSET_Y = 0;
	
	//initial no scaling
	public static double SCALE = 1;
	
	//zoom level +- 2%
	public static double ZOOM_LEVEL = 0.02;
	
	
	/**
	 * Returns if the object to be drawn is inside the visible canvas viewport.
	 * It is sufficient for the object to be inside with any corner to be drawn.
	 * @return
	 */
	public static boolean isInside(Drawable drawable) {
		return isPointInWindow(drawable.getPosStart()) || isPointInWindow(drawable.getPosEnd());
	}
	
	/**
	 * Returns if the point is inside the current client window.
	 * @param p
	 * @return
	 */
	private static boolean isPointInWindow(Point p) {
		return p.getX() >= 0 && p.getX() <= Window.getClientWidth()
			&& p.getY() >= 0 && p.getY() <= Window.getClientHeight();
	}

	/**
	 * Zooms in or out of the canvas
	 * @param delta the amount to zoom
	 * @param mouseX the mouse x coordinate
	 * @param mouseY the mouse y coordinate
	 */
	public static double zoom(double delta, int mouseX, int mouseY) {
		//negating the expression as moving the wheel in north direction should zoom in
		double amount = - ZOOM_LEVEL * delta;

		//limit min and max zooming levels.
		//the numbers were found iteratively by trying out up to which zoom level it makes sense to zoom in this app
		if (SCALE + amount > ZoomHandler.MIN && SCALE + amount < ZoomHandler.MAX) {
			//zoom would by default be relative to P(0,0).
			//to simulate zooming to the mouse pointer, an offset has to be applied on every zoom, so that the canvas is centered under the mouse pointer.
			double offFactor = amount / (SCALE * (SCALE + amount));
			OFFSET_X += mouseX * offFactor;
			OFFSET_Y += mouseY * offFactor;
			
			SCALE += amount;
		}
		
		return SCALE;
	}

	public static void reset() {
		CanvasArea.get().getStateViewport().updateViewport(0, 0);
		OFFSET_X = 0;
		OFFSET_Y = 0;
		SCALE = 1;
		ZOOM_LEVEL = 0.02;
	}
}
