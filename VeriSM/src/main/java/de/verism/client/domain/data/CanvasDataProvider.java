package de.verism.client.domain.data;

import java.util.ArrayList;
import java.util.List;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.canvas.shapes.visitors.DeleteService;
import de.verism.client.domain.State;
import de.verism.client.domain.Transition;

/**
 * Handles all figures within a single canvas. From the MVP point of view, this belongs to the model part.
 * @author Daniel Kotyk
 *
 */
public class CanvasDataProvider {
	//list of all elements in the canvas
	private List<Drawable> drawables = new ArrayList<Drawable>();
	
	/**
	 * Extracts all rectangles contained in the figures list.
	 * @return
	 */
	public List<Rectangle> getRectangles() {
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		for (Drawable drawable : drawables) {
			if (drawable instanceof Rectangle) {
				rectangles.add((Rectangle) drawable);
			}
		}

		return rectangles;
	}
	
	
	/**
	 * Extracts all transitions contained in the figures list.
	 * @return
	 */
	public List<Line> getLines() {
		List<Line> lines = new ArrayList<Line>();
		for (Drawable drawable : drawables) {
			if (drawable instanceof Line) {
				lines.add((Line) drawable);
			}
		}
		
		return lines;
	}
	
	/**
	 * Extracts all states from the rectangles.
	 */
	public List<State> getStates() {
		List<State> states = new ArrayList<State>();
		for (Rectangle rectangle : getRectangles()) {
			states.add(rectangle.getFigure());
		}
		
		return states;
	}
	
	/**
	 * Extracts all transitions from lines.
	 * @return
	 */
	public List<Transition> getTransitions() {
		List<Transition> transitions = new ArrayList<Transition>();
		for (Line line : getLines()) {
			transitions.add(line.getFigure());
		}
	
		return transitions;
	}

	/**
	 * Add figure on top of the list.
	 * @param drawable
	 */
	public void add(Drawable drawable) {
		drawables.add(0, drawable);
	}

	/**
	 * Removes a {@link Drawable} from the {@link Canvas}.
 	 * Also takes into account that references between {@link State} and {@link Transition} has to be cleared.
	 * @param drawable
	 */
	public void remove(Drawable drawable) {
		DeleteService visitor = new DeleteService(drawables);
		visitor.delete(drawable);
	}

	public List<Drawable> getDrawables() { return drawables; }
	public void setDrawables(List<Drawable> drawables) {this.drawables = drawables; }
}
