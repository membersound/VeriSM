package de.verism.client.canvas.drawing;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import de.verism.client.canvas.shapes.Drawable;

/**
 * Maintain the state the visible canvas is in,
 * eg if there is a selection, if the user is currently dragging an object, or the current offset while dragging.
 * @author Daniel Kotyk
 *
 */
public class MouseContext {
	interface CanvasState {
		void onMouseDown(MouseContext mouseCtx, MouseDownEvent evt);
		void onMouseUp(MouseContext mouseCtx, MouseUpEvent evt);
		void onMouseMove(MouseContext mouseCtx, MouseMoveEvent evt);
	}
	
	private CanvasState canvasState;
	
	//default state
	public MouseContext(CanvasState canvasState) {
		this.canvasState = canvasState;
	}
	
	public CanvasState getCanvasState() {
		return canvasState;
	}

	public void setState(CanvasState canvasState) {
		this.canvasState = canvasState;
	}
	
	public void onMouseDown(MouseDownEvent evt) {
		canvasState.onMouseDown(this, evt);
	}
	public void onMouseUp(MouseUpEvent evt) {
		canvasState.onMouseUp(this, evt);
	}
	public void onMouseMove(MouseMoveEvent evt) {
		canvasState.onMouseMove(this, evt);
	}
	
	//offset while dragging
	private double offsetX;
	private double offsetY;

	//create offset if dnd click is not in the 0,0 point of the figure
	//draw selection with mouse offset (XY always represent P(0,0) of the rectangle.
	//If mouseclick is somewhere else, an offset has to be applied to the new drawn rect.
	public void updateOffset(int mouseX, int mouseY, Drawable selection) {
        offsetX = mouseX - selection.getPosStart().getX();
        offsetY = mouseY - selection.getPosStart().getY();
	}

	/**
	 * The mouse position offset in x direction from the center of the figure.
	 * @return
	 */
	public double getOffsetX() { return offsetX; }
	/**
	 * The mouse position offset in y direction from the center of the figure
	 * @return
	 */
	public double getOffsetY() { return offsetY; }
}
