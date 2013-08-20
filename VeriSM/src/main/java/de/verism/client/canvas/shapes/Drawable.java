package de.verism.client.canvas.shapes;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.LineCap;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasText;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.rendering.Point;
import de.verism.client.canvas.shapes.visitors.Visitable;
import de.verism.client.components.contextMenu.HasContextMenu;
import de.verism.client.domain.Figure;

/**
 * Base class providing common used functionality that is shared by all drawable objects.
 * Implements {@link HasText} to provide a general getText() method for drawing the text onto the canvas using this central method.
 * 
 * @author Daniel Kotyk
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id") //resolves circular references by introducing a "@id" field property
public abstract class Drawable implements Serializable, IsDrawable, IsDragable, HasTextId, Visitable, HasContextMenu {
	//global constants for all drawables
	public static final String BORDER_COLOR = "#6699cc"; //darkblue 5892ce
	//transparency
	static final double ALPHA = 0.5;
	static final String SELECTION_COLOR = "#ff9933"; //orange f0a02a
	public static final int DEFAULT_BORDER_SIZE = 2;
	//degree of shadow blur
	static final double SHADOW_BLUR = 4;
	
	//the color for all objects on the shadow canvas
	public static final String SHADOW_COLOR = "#FF0000";
	
	//the auto-generated id for client side object tracking
	private transient String id = "";
	
	//track object place. both rectangles and lines can be characterized by two points in a coordinate space.
	//further values like width, height, distance etc can all be computed using these two points.
	Point posStart, posEnd;
	
    /**
     * Empty constructor for serialization.
     */
	Drawable() {};
	
	public Drawable(Point start, Point end) {
		this.posStart = start;
		this.posEnd = end;
		generateId();
	}

	public Drawable(double fromX, double fromY, double toX, double toY) {
		this(new Point(fromX, fromY), new Point(toX, toY));
	}
	
	
	@Override
	public void generateId() {
		id = DOM.createUniqueId();
	}
	
    @Override
    public void draw(Context2d ctx) {
        prepareDrawing(ctx);
        CanvasArea.DRAW_STATE.draw(ctx, this);
    }

	/**
	 * Default drawing action for an object. No selection or special drawings applied.
	 * All custom drawing states are package protected, as they should only be invoced
	 * using a {@link DrawStates.DrawState}
	 * @param ctx
	 */
	abstract void drawDefault(Context2d ctx);
	
	/**
	 * Select an object by providing custom rendering (eg. red border around the shape).
	 */
	abstract void drawSelected(Context2d ctx);
	
	/**
	 * Draws the object semi-transparent to indicate dragging over other shapes.
	 * @param ctx
	 */
	abstract void drawDragging(Context2d ctx);
	
	/**
	 * Draws an objects that is to be moved along with the selected object, but not selected itself.
	 * @param ctx
	 */
	abstract void drawDraggingNotSelected(Context2d ctx);

	/**
	 * Draws the shadow canvas objects for hitpoint detection.
	 * @param ctx
	 */
	public abstract void drawShadow(Context2d ctx);
	
	/**
	 * Prepares the context for properties that are shared by all Drawables,
	 * likes border size, shadow etc.
	 * @param ctx
	 */
	void prepareDrawing(Context2d ctx) {
		ctx.beginPath();
		initObjectShadow(ctx);
		ctx.setLineCap(LineCap.ROUND);
		ctx.setLineWidth(DEFAULT_BORDER_SIZE * Viewport.SCALE);
	}
	
	/**
	 * Defines global shadow blur and color.
	 * @param ctx
	 */
	private void initObjectShadow(Context2d ctx) {
		ctx.setShadowBlur(SHADOW_BLUR * Viewport.SCALE);
		ctx.setShadowOffsetX(DEFAULT_BORDER_SIZE * Viewport.SCALE);
		ctx.setShadowOffsetY(DEFAULT_BORDER_SIZE * Viewport.SCALE);
		ctx.setShadowColor("rgba(0, 0, 0," + ALPHA / 2 + ")");
	}


	@Override
	public String getText() {
		return getFigure().getText();
	}

	@Override
	public void setText(String text) {
		getFigure().setText(text);
	}


	/**
	 * Returns the width of the rectangle.
	 * @return
	 */
	public double getW() {
		return Math.abs(posEnd.getX() - posStart.getX());
	}
	
	/**
	 * Returns the height of the rectangle.
	 * @return
	 */
	public double getH() {
		return Math.abs(posEnd.getY() - posStart.getY());
	}
	
	/**
	 * Returns the center point of eg a rectangle.
	 * Used for computing the points where a line has start/end.
	 * @return
	 */
	public Point getCenter() {
		return new Point(posStart.getX() + getW() / 2, posStart.getY() + getH() / 2);
	}

	/**
	 * Checks if the new object complies with the minimal size requirements. Adjust the size if it is too small.
	 * Every {@link IsDrawable} must therefore define itself the condition for which it is defined as 'too small'.
	 */
	public abstract void center();
	
	//wäre figure hier die strategie, die jede figure selbst zur verfügung stellen muss?
	public abstract Figure getFigure();
	
	@Override
	public String getId() { return id; }
	@Override
	public Point getPosStart() { return posStart; }
	@Override
	public Point getPosEnd() { return posEnd; }
	public void setPosStart(Point posStart) { this.posStart = posStart; }
	public void setPosEnd(Point posEnd) { this.posEnd = posEnd; }
}
