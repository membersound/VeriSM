package de.verism.client.canvas.shapes.visitors;

import java.util.List;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;

/**
 * Wraps all implementation-specific delete action in this delete visitor.
 * @author Daniel Kotyk
 *
 */
public class DeleteService implements CanvasVisitor<Drawable> {
	
	private List<Drawable> drawables;

	public DeleteService(List<Drawable> drawables) {
		this.drawables = drawables;
	}
	
	/**
	 * Cleanup transitions connected to that state
	 */
	@Override
	public void visit(Rectangle rectangle) {
		for (Line transition : rectangle.getConnections()) {
			//only delete transitions from states that are not this state, as this state will be removed at the end
			Rectangle stateFrom = transition.connectedFrom();
			if (stateFrom != rectangle) {
				stateFrom.getConnections().remove(transition);
			}
			
			Rectangle stateTo = transition.connectedTo();
			if (stateTo != rectangle) {
				stateTo.getConnections().remove(transition);
			}
			
			drawables.remove(transition);
		}
	}
	
	/**
	 * Cleanup the transition to be deleted of connected states.
	 */
	@Override
	public void visit(Line line) {
		line.connectedFrom().getConnections().remove(line);
		line.connectedTo().getConnections().remove(line);
	}
	
	/**
	 * Deletes a drawable from the objects list.
	 * @param drawable
	 */
	public void delete(Drawable drawable) {
		drawable.accept(this); //delegates to #visit
		//no matter if rectangle or line, it should be deleted from the list at the end
		drawables.remove(drawable);
	}
}
