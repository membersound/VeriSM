package de.verism.client.canvas.shapes;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.rendering.Arrow;
import de.verism.client.canvas.shapes.rendering.Line2D;
import de.verism.client.canvas.shapes.rendering.Point;
import de.verism.client.canvas.shapes.rendering.TextRenderer;
import de.verism.client.canvas.shapes.visitors.CanvasVisitor;
import de.verism.client.canvas.shapes.visitors.Visitor;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.dialog.editTransition.EditTransitionPanel;
import de.verism.client.domain.Transition;


/**
 * Represents a {@link Transition} object visually in the canvas.
 * @author Daniel Kotyk
 *
 */
public class Line extends Drawable {
	private static final String BORDER_COLOR_ALPHA = "rgba(102, 153, 204, " + ALPHA + ")";
	private static final String SELECTION_COLOR_ALPHA = "rgba(255, 153, 51, " + ALPHA + ")";
	
	//linewidth of shadow lines, which are much bigger so that the user does not have to select the line pixel-perfect
	private static final int SHADOW_WIDTH = 20;
	
	//the object represented by a line
	private Transition transition;
	//holds the reference of connected objects
	private Rectangle from, to;
	
	//control point through that a curved line must pass
	private Point cp;

	public Rectangle connectedFrom() { return from; }
	public Rectangle connectedTo() { return to; }
	
	private Line() {}

	Line(Rectangle from, Rectangle to, boolean showText) {
		//clone points to not modify the state points later when calling eg #center()
		super(from.getPosStart().clone(), to.getPosStart().clone());
		
		this.transition = new Transition(from.getFigure(), to.getFigure(), showText);
		this.from = from;
		this.to = to;
		
		//add back-references
		from.getConnections().add(this);
		to.getConnections().add(this);
		
		center();
	}
	
	/**
	 * Centers the controlpoint initial to the middle of the straight line, after creation.
	 */
	@Override
	public void center() {
		//based on the center points of the connected rectangles
		Point s = from.getCenter();
		Point e = to.getCenter();
		
		double x = (s.getX() + e.getX()) / 2;
		double y = (s.getY() + e.getY()) / 2;
		
		cp = new Point(x, y);
	}

	@Override
	public Transition getFigure() {
		return transition;
	}

	@Override
	public IsInputProvider getContextMenu() {
		return new EditTransitionPanel(getFigure());
	}
	
	//offset for which distance the line should still be selected, as thin lines would otherwise be hard to select
	private static final double LINE_DISTANCE = 10;

	@Override
	public boolean isInside(double px, double py) {
		//check if distance of the click point to the line segment is short enough to select the line
		double distance = Line2D.ptSegDist(posStart, posEnd, px, py);
		return distance <= LINE_DISTANCE * Viewport.SCALE;
	}
	
	
	@Override
	public void drawDefault(Context2d ctx) {
		drawLine(ctx, BORDER_COLOR, TextRenderer.HEADER_COLOR, transition.isShowText());
//		drawShadow(ctx);
	}

	@Override
	public void drawSelected(Context2d ctx) {
		//always show the condition context on selection, no matter if it otherwise is shown
		drawLine(ctx, SELECTION_COLOR, TextRenderer.HEADER_COLOR, true);
	}

	@Override
	public void drawDragging(Context2d ctx) {
		drawLine(ctx, SELECTION_COLOR_ALPHA, TextRenderer.HEADER_COLOR, true);
	}
	
	/**
	 * Used while a transition is not selected, but dragged implicit while dragging a selected state.
	 * @param ctx
	 */
	@Override
	public void drawDraggingNotSelected(Context2d ctx) {
		drawLine(ctx, BORDER_COLOR_ALPHA, BORDER_COLOR, transition.isShowText());
	}
	
	@Override
	public void drawShadow(Context2d gctx) {
		prepareDrawing(gctx);
		gctx.setShadowColor("transparent");
		gctx.setLineWidth(SHADOW_WIDTH * Viewport.SCALE);
		drawLine(gctx, SHADOW_COLOR, SHADOW_COLOR, false);
	}
	
	private void drawLine(Context2d ctx, String strokeColor, String textColor, boolean showText) {
		ctx.setStrokeStyle(strokeColor);
		ctx.setFillStyle(strokeColor);
		
		//draw full arrow
		drawArrow(ctx);

		//text must be placed on top of everything
		if (showText) {
			TextRenderer.draw(ctx, this, textColor);
		}
		ctx.setShadowColor("transparent");
	}
	
	@Override
	void prepareDrawing(Context2d ctx) {
		super.prepareDrawing(ctx);
		ctx.setShadowOffsetX(DEFAULT_BORDER_SIZE / 2 * Viewport.SCALE);
		ctx.setShadowOffsetY(DEFAULT_BORDER_SIZE / 2 * Viewport.SCALE);
	}

	/**
	 * Draws a line with an arrow tip at the end.
	 */
	private void drawArrow(Context2d ctx) {
		//start and end point of the line are the center points inside the rectangle
		Point cs = from.getCenter();
		Point ce = to.getCenter();
		
		//the rectangle intersection points, first from startrectangle to controlpoint, then from cp to endrectangle
		Point s = Line2D.getIntersection(cs, cp, from, true);
		Point e = Line2D.getIntersection(cp, ce, to, false);

        //angle of the line	= angle of the line from controlpoint to endpoint (see: http://en.wikipedia.org/wiki/B%C3%A9zier_curve)
		double angle = Math.atan2(e.getY() - cp.getY(), e.getX() - cp.getX());
		
		//small offset to the end point as the arrow tip is otherwise a bit hidden behind the rectangle
		int offset = 3;
	    Point startArrow = new Point(
	    		(int) Math.round(e.getX() - offset * Math.cos(angle)),
			    (int) Math.round(e.getY() - offset * Math.sin(angle)));
	    
	    //move the endpoint by offset
	    e.moveTo(startArrow.getX(), startArrow.getY());
	    
	    //update the transition point coordinates accordingly (for proper isInside() testing on selection)
	    posStart.moveTo(s);
	    posEnd.moveTo(e);

		//draw the bezier line without the arrow head
	    ctx.moveTo(s.getX(), s.getY());
		ctx.quadraticCurveTo(cp.getX(), cp.getY(), e.getX(), e.getY());
		//draw solid border
		ctx.stroke();
		
		//draw the arrow
		//as the arrow head should be filled, and the line should not, a new path must begin here
		ctx.beginPath();
		Arrow.draw(ctx, s, e, angle);
		ctx.stroke();
		ctx.fill();
	}

	
	/**
	 * Moves the controlpoint to a new place so that resulting bezier goes through the 3 points: start, end, and actual mouse position.
	 * This is necessary, because the controlPoint is NOT part of the curve, and thus must be calculated.
	 */
	@Override
	public void moveTo(double x, double y) {
		cp.moveTo(getControlPoint(new Point(x,y), posStart, posEnd));
	}
	
	/**
	 * Returns the control point for the bezier so that it crosses a specific point.
	 * Because the cp is not a point on the line, but the user can only select a point on the line to drag it.
	 * @param crossPoint the point that should be crossed by the bezier
	 * @param s the start point of the line
	 * @param e the end point of the line
	 * @return the control point for achiving a bezier curve through these 3 points
	 */
	private Point getControlPoint(Point crossPoint, Point s, Point e) {
		double cx = 2 * crossPoint.getX() - s.getX() / 2 - e.getX() / 2;
		double cy = 2 * crossPoint.getY() - s.getY() / 2 - e.getY() / 2;
		
		return new Point(cx, cy);
	}
	
	/**
	 * Tests if the controlpoint is inside one of the rectangles.
	 * If so, the bezier makes no sence anymore, and would additionally be disaligned.
	 * Thus, just center the line in this case and straighten it.
	 */
	public void updateControlPoint() {
		if (from.isInside(cp.getX(), cp.getY())
		   || to.isInside(cp.getX(), cp.getY())) {
			center();
		}
	}
	
	/**
	 * Draws the control point of the line as little dot.
	 * Suitable for debugging the line behavior.
	 * @param ctx
	 */
	private void drawCP(Context2d ctx) {
		ctx.beginPath();
		ctx.arc(cp.getX(), cp.getY(), 4, 0, Math.PI * 2);
		ctx.fill();
	}
	
	
	
	@Override
	public <T> void accept(CanvasVisitor<T> visitor) {
		visitor.visit(this);
	}
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visit(this);
	}
	
	
	public Point getControlPoint() { return cp; }
	public void setControlPoint(Point cp) { this.cp = cp; }
}
