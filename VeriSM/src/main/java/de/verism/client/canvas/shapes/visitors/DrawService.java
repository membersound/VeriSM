package de.verism.client.canvas.shapes.visitors;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.shapes.DrawStates;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.canvas.shapes.DrawStates.DrawState;

/**
 * Drawing rectangles must be handled differently than drawing line connections
 * during 'buffer to back canvas': lines connected to a rectangle should not be
 * static as they should be moved along with the dragged rectangle.
 * 
 * Thus they are to be excluded from buffering to static back canvas and are
 * drawing separately within this visitor.
 * @author Daniel Kotyk
 *
 */
public class DrawService implements CanvasVisitor<Drawable> {
	
	private Context2d ctx;

	public DrawService(Context2d ctx) {
		this.ctx = ctx;
	}
	
	/**
	 * Draws lines that are connected to a selected rectangle.
	 */
	@Override
	public void visit(Rectangle rectangle) {
		for (Line line : rectangle.getConnections()) {
			line.draw(ctx);
		}
	}
	
	@Override
	public void visit(Line line) {
		//visual of a line not to be changed so far
	}
	
	/**
	 * Draws the object according to provided {@link DrawStrategy}.
	 * @param drawable
	 */
	public void draw(Drawable drawable) {
		//if a state is selected, the connected lines should not be drawn selected, thus having to change the draw context
		DrawState oldState = CanvasArea.DRAW_STATE;
		if (CanvasArea.DRAW_STATE == DrawStates.DRAW_SELECT) {
			CanvasArea.DRAW_STATE = DrawStates.DRAW_DEFAULT;
		}
		
		drawable.accept(this); //double delegate to visit() and draws possibly connected lines
		CanvasArea.DRAW_STATE = oldState;
		
		//important to draw the selected state after all connected lines,
		//to make the selection always the topmost object
		drawable.draw(ctx);
	}
}
