package de.verism.client.canvas.shapes.visitors;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;

/**
 * Draw transitions connected to the state semi-transparent.
 * @author Daniel Kotyk
 *
 */
public class DrawDraggingService implements CanvasVisitor<Drawable> {
	
	private Context2d ctx;

	public DrawDraggingService(Context2d ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public void visit(Rectangle rectangle) {
		for (Line transition : rectangle.getConnections()) {
			transition.drawDraggingNotSelected(ctx);
		}
	}
	
	@Override
	public void visit(Line line) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
