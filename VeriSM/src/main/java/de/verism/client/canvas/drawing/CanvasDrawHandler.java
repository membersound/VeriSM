package de.verism.client.canvas.drawing;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Window;

import de.verism.client.canvas.shapes.DrawStates;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.canvas.shapes.visitors.DrawService;

/**
 * Handles all the drawíng on the canvas.
 * @author Daniel Kotyk
 *
 */
public class CanvasDrawHandler {
	//all static objects get once added to this canvas/context while dragging
	private Canvas dragCanvas;
	private CanvasArea canvasArea;
	
	public CanvasDrawHandler(CanvasArea canvasArea) {
		dragCanvas = CanvasFactory.getCanvas();
		this.canvasArea = canvasArea;
	}

	/**
	 * Draw all unselected elements temporary on the {@link #dragCanvas}, without the selected object that is to be moved.
	 * Advantage here is that all temporary drawings of the static content is done
	 * onto the invisible dragCanvas, thus done in the background not noticeable for the user.
	 * 
	 * One could also draw the content on the main canvas and then save the image to {@link #dragCanvas},
	 * but this might cause flickerings on the user side.
	 * @param selection
	 * @param list
	 */
	public void bufferUnselected(Drawable selection, List<Drawable> list) {
		CanvasArea.DRAW_STATE = DrawStates.DRAW_DEFAULT;
		
		Context2d bufferCtx = dragCanvas.getContext2d();
		//clearing existing buffer is more efficient than creating a new empty canvas every time
		bufferCtx.clearRect(0, 0, Window.getClientWidth(), Window.getClientHeight());

		ListIterator<Drawable> itr = list.listIterator(list.size());
		
		//draw figures using backwards iteration, as newest elements are always at the bottom of the list
		while(itr.hasPrevious()) {
			Drawable drawable = itr.previous();
			
			//skip drawables outside the actual viewport
			if (!Viewport.isInside(drawable)) {
				continue;
			}
			
			//exclude the selected element for the back buffer
			if (selection == null || !drawable.equals(selection)) {
				
				//do not buffer transitions that are connected to the selected state
				if (selection != null && drawable != null
				 && selection instanceof Rectangle && drawable instanceof Line) {
					Rectangle rectangle = (Rectangle) selection;
					Line line = (Line) drawable;
					if (rectangle.getConnections().contains(line)) {
						continue;
					}
				}
				
				drawable.draw(bufferCtx);
			}
		}
	}


	/**
	 * Make the selected {@link Drawable} the topmost element after DnD is over.
	 * @param selection
	 * @param list
	 */
	public void moveToFront(Drawable selection, List<Drawable> list) {
		if (selection != null) {
			while (list.indexOf(selection) > 0) {
				int i = list.indexOf(selection);
			    Collections.swap(list, i, i - 1);
			}
		}
	}

	/**
	 * Repaint the front canvas using the backing image and the current selection.
	 * @param ctx the context to draw on
	 * @param selection the element to draw as selected
	 */
	public void repaint(Context2d ctx, Drawable selection) {
		//clear front canvas
		ctx.clearRect(0, 0, Window.getClientWidth(), Window.getClientHeight());
		
		//paint static buffered canvas
		ctx.drawImage(dragCanvas.getCanvasElement(), 0, 0);
		
		//paint selection
		if (selection != null) {
			//init the drawing strategy
			CanvasArea.DRAW_STATE = canvasArea.isDragState() ? DrawStates.DRAW_DRAG_NOT_SEL : DrawStates.DRAW_SELECT;
			DrawService service = new DrawService(ctx);
			service.draw(selection);
		}
	}
}
