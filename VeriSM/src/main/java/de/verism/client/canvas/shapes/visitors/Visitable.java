package de.verism.client.canvas.shapes.visitors;

/**
 * Defines the Visitable for {@link Figures}.
 * This is Design Pattern 'Visitor'.
 * @author Daniel Kotyk
 *
 */
public interface Visitable {
	/**
	 * Delegates to the visitor.
	 * @param <T>
	 * @param editor
	 */
	public <T> T accept(Visitor<T> visitor);
	
	//accept is more a "execute" method here
	public <T> void accept(CanvasVisitor<T> visitor);
}
