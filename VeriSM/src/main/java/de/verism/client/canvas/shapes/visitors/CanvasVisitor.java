package de.verism.client.canvas.shapes.visitors;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;

/**
 * Provides double dispatch for acting on a list of {@link Drawable}s
 * without having to know the instanceof at compiler time.
 * 
 * That's the only way global methods can be executed against any
 * instance of {@link Drawable} and provide implementation-specific action.
 * 
 * This is VisitorPattern, which is used any time when behavior of
 * {@link Rectangle} and {@link Line} should differ for a certain action (like delete()).
 * 
 * @see http://en.wikipedia.org/wiki/Double_dispatch
 * @author Daniel Kotyk
 *
 * @param <T>
 */
public interface CanvasVisitor<T> {
	/**
	 * Delegates to the {@link Rectangle} implementation.
	 * @param state
	 */
	void visit(Rectangle rectangle);
	
	/**
	 * Delegates to the {@link Line} implementation.
	 * @param transition
	 */
	void visit(Line line);
}
