package de.verism.client.canvas.shapes.visitors;

import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.Figure;
import de.verism.client.domain.Signal;

/**
 * Defines the visitor to be passed into a {@link Figure#accept()}.
 * Makes use of generics to enable return values, eg a EditDialog Widget.
 * @author Daniel Kotyk
 *
 */
public interface Visitor<T> {
	/**
	 * Delegates to the {@link Rectangle} implementation.
	 * @param state
	 */
	public T visit(Rectangle rectangle);
	
	/**
	 * Delegates to the {@link Signal} implementation.
	 * @param state
	 */
	public T visit(Signal signal);

	/**
	 * Delegates to the {@link Line} implementation.
	 * @param transition
	 */
	public T visit(Line line);
}
