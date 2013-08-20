package de.verism.client.canvas.shapes.visitors;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.Signal;

public class TypeVisitor implements Visitor<Drawable> {

	@Override
	public Rectangle visit(Rectangle rectangle) {
		return rectangle;
	}

	@Override
	public Drawable visit(Signal signal) {
		return null;
	}

	@Override
	public Line visit(Line line) {
		return line;
	}

}
