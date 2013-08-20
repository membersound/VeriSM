package de.verism.client.canvas.shapes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;

import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.rendering.TextRenderer;
import de.verism.client.canvas.shapes.visitors.CanvasVisitor;
import de.verism.client.canvas.shapes.visitors.Visitor;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.dialog.editState.EditStatePanel;
import de.verism.client.domain.State;

/**
 * Represents a {@link State} visually in the canvas.
 * @author Daniel Kotyk
 *
 */
public class Rectangle extends Drawable {
	private static final String BACKGROUND_COLOR = "#e2eaf2"; //lightblue
	private static final String BACKGROUND_COLOR_ALPHA = "rgba(226, 234, 242, " + ALPHA + ")"; //semi transparent background
	private static final String GRADIENT = "#F7F7F7";
	private static final String GRADIENT_ALPHA = "rgba(247, 247, 247, " + ALPHA + ")";
	
	public static final int DEFAULT_WIDTH = 50;
	public static final int DEFAULT_HEIGHT = 50;
	public static final int MAX_WIDTH = 150;
	
	//space for inner rectangle representing initial state
	protected static final int INNER_SPACE = 4;
	
	//radius of rectangle corners
	public static final int RADIUS = 15;

	//the state represented by the rectangle
	private State state = new State();

    /**
     * Empty constructor for serialization.
     */
	private Rectangle() {};
	
	//list of all incoming and outgoing transitions from this state. used for update all of them on dragging
	private List<Line> connections = new ArrayList<Line>();
	public List<Line> getConnections() { return connections; }
	
	/**
	 * Connects this rectangle to the rectangle provided as parameter.
	 * @param to the rectangle to be connected to
	 * @param showText if condition text should be initial visible
	 * @return the connection line object
	 */
	public Line connect(Rectangle to, boolean showText) {
		return new Line(this, to, showText);
	}
	
	public Rectangle(int mouseX, int mouseY) {
		super(mouseX, mouseY, mouseX + DEFAULT_HEIGHT * Viewport.SCALE, mouseY + DEFAULT_WIDTH * Viewport.SCALE);
		center();
	}

	/**
	 * Verschiebung eines Rechtecks mit 2 Punkten zu einem neuen Punkt. Berechnung siehe mein Heft.
	 */
	@Override
	public void moveTo(double x, double y) {
		double dx = x - posStart.getX();
		double dy = y - posStart.getY();
		posEnd.moveBy(dx, dy);
		posStart.moveBy(dx, dy);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void center() {
		//places the rectangle middle aligned under the current mouseclick. no dx = dy as initial size is a square
		double dx = DEFAULT_WIDTH / 2 * Viewport.SCALE;
		posStart.moveBy(-dx, -dx);
		posEnd.moveBy(-dx, -dx);
	}

	@Override
	public State getFigure() {
		return state;
	}

	@Override
	public boolean isInside(double px, double py) {
		return Math.min(posStart.getX(), posEnd.getX()) <= px && px <= Math.max(posStart.getX(), posEnd.getX())
			&& Math.min(posStart.getY(), posEnd.getY()) <= py && py <= Math.max(posStart.getY(), posEnd.getY());
	}

	@Override
	@JsonIgnore
	public IsInputProvider getContextMenu() {
		return new EditStatePanel(getFigure());
	}
	

	/**
	 * Draw rectangle with border.
	 */
	@Override
	public void drawDefault(Context2d ctx) {
		drawRect(ctx, BORDER_COLOR, BACKGROUND_COLOR, GRADIENT, true);
//		drawShadow(ctx); //use this to draw the ghost canvas on screen for debugging
	}
	
	/**
	 * Draw rectangle with dashed border.
	 */
	@Override
	public void drawSelected(Context2d ctx) {
		drawRect(ctx, SELECTION_COLOR, BACKGROUND_COLOR, GRADIENT, true);
	}
	
	@Override
	public void drawDragging(Context2d ctx) {
		drawRect(ctx, SELECTION_COLOR, BACKGROUND_COLOR_ALPHA, GRADIENT_ALPHA, true);
	}
	
	/**
	 * Rectangle is always selected while dragging. This method is more important for a {@link Line}.
	 */
	@Override
	public void drawDraggingNotSelected(Context2d ctx) {
		this.drawDragging(ctx);
	}
	
	@Override
	public void drawShadow(Context2d gctx) {
		prepareDrawing(gctx);
		gctx.setShadowColor("transparent");
		drawRect(gctx, SHADOW_COLOR, SHADOW_COLOR, SHADOW_COLOR, false);
	}
	
	/**
	 * Delegate to draw the rectangle with text, border and initial state.
	 * @param ctx
	 * @param borderColor
	 * @param bgColor
	 * @param gradientColor
	 */
	private void drawRect(Context2d ctx, String borderColor, String bgColor, String gradientColor, boolean showText) {
		if (!state.getText().isEmpty()) {
			//size must be corrected before rect is filled, because maybe text exceeds the figure size
			TextRenderer.correctFigureSize(ctx, this);
		}
		
		//draw rectangle
		roundRectangle(ctx, posStart.getX(), posStart.getY(), getW(), getH(), RADIUS * Viewport.SCALE);

		ctx.setFillStyle(getGradient(ctx, bgColor, gradientColor));
		ctx.fill();
		ctx.setStrokeStyle(borderColor);

		//text must be placed on top of everything
		if (showText) {
			TextRenderer.draw(ctx, this);
			//restore shadow as border should not have a shadow
			ctx.setShadowColor("transparent");
		}
		
		//draw solid border
		ctx.stroke();
		drawInitialState(ctx);
	}

	/**
	 * Creates a subtile gradient for the inner rectangle area.
	 * @param ctx
	 * @param bgColor the color to end
	 * @return
	 */
	private FillStrokeStyle getGradient(Context2d ctx, String bgColor, String gradientColor) {
		CanvasGradient gradient = ctx.createLinearGradient(posStart.getX(), posStart.getY(), posStart.getX(), posEnd.getY());
		gradient.addColorStop(0, gradientColor);
		gradient.addColorStop(1, bgColor);
		return gradient;
	}

	/**
	 * Creates a rectangle with round corners by using lines combined with quarter-circles.
	 * @param ctx
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void roundRectangle(Context2d ctx, double x, double y, double w, double h, double radius) {
		//horizontal and vertical control points
		double cpx = x + w;
		double cpy = y + h;
		ctx.moveTo(x + radius, y);
		
		//moves clockwise around the rectangle, and draws the round corners with #quadraticCurveTo()
		ctx.lineTo(cpx - radius, y);
		ctx.quadraticCurveTo(cpx, y, cpx, y + radius);
		ctx.lineTo(cpx, y + h - radius);
		ctx.quadraticCurveTo(cpx, cpy, cpx - radius, cpy);
		ctx.lineTo(x + radius, cpy);
		ctx.quadraticCurveTo(x, cpy, x, cpy - radius);
		ctx.lineTo(x, y + radius);
		ctx.quadraticCurveTo(x, y, x + radius, y);
	}

	/**
	 * Draw inner border to indicate the initial state.
	 * @param ctx
	 */
	private void drawInitialState(Context2d ctx) {
		if (state.isInitial()) {
			ctx.beginPath();
			double innerSpace = INNER_SPACE * Viewport.SCALE;
			roundRectangle(ctx, posStart.getX() + innerSpace, posStart.getY() + innerSpace, getW() - 2 * innerSpace, getH() - 2 * innerSpace, RADIUS * Viewport.SCALE);
			ctx.stroke();
		}
	}
	
	/**
	 * Comparator to compare States by name.
	 */
	public static Comparator<Rectangle> COMPARE_BY_STATE_NAME = new Comparator<Rectangle>() {
	    @Override
	    public int compare(Rectangle s1, Rectangle s2) {
	        return s1.getFigure().getText().compareTo(s2.getFigure().getText());
	    }
	};

	@Override
	public <T> void accept(CanvasVisitor<T> visitor) {
		visitor.visit(this);
	}
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}
}