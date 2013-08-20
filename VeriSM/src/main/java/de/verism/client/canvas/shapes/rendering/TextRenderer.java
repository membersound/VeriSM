package de.verism.client.canvas.shapes.rendering;

import com.google.gwt.canvas.dom.client.Context2d;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.DrawStates;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.components.editTextBox.EditTextBox;
import de.verism.client.components.panels.io.spreadsheet.cell.render.ConditionCellStrategy;

/**
 * Embedded text for states (name) or transitions (condition).
 * @author Daniel Kotyk
 *
 */
public class TextRenderer {
	//color of the text in the same color as the menu header
	public static final String HEADER_COLOR = "#25609C";
	public static final int DEFAULT_FONT_SIZE = 13;
	
	//max line sizes
	private static final int MAX_LINE_WIDTH = 250;
	private static final int DEFAULT_LINE_HEIGHT = 16;
	
	//minimum whitespace around text
	static final int TEXT_PADDING = 20;
	
	//string to indicate that some text was omitted to display
	public static final String ELLIPSIS = "...";
	
	public static void draw(Context2d ctx, Line line, String color) {
		//text should have less shadow
		ctx.setShadowOffsetX(1);
		ctx.setShadowOffsetY(1);
		
		ConditionCellStrategy condition = new ConditionCellStrategy();
		//only show the condition without any name
		String text = condition.getValue(line.getFigure());
		
		//measure the text iteratively and calculate maximum, appended with three dots "..."
		//but only if the line is not single selected (as then the whole condition should be displayed).
		if (CanvasArea.DRAW_STATE != DrawStates.DRAW_SELECT
				&& ctx.measureText(text).getWidth() + TEXT_PADDING * Viewport.SCALE > MAX_LINE_WIDTH * Viewport.SCALE) {
			text = getAbbreviatedText(ctx, text, MAX_LINE_WIDTH * Viewport.SCALE);
		}
		
		ctx.setFillStyle(color);
		
		//center the text in the bezier line
		Point tp = getTextPoint(line.getControlPoint(), line.getPosStart(), line.getPosEnd());
		double textWidth = ctx.measureText(text).getWidth();

		updateFontSize(ctx);
		ctx.fillText(text, tp.getX() - textWidth / 2, tp.getY() + DEFAULT_FONT_SIZE * Viewport.SCALE);
	}
	
	/**
	 * Corrects the rectangles' outer size according to the new text.
	 * 
	 * When text gets bigger > expand the figure.
	 * When text gets less > shrink accordingly.
	 * @param ctx
	 * @param rectangle
	 */
	public static void correctFigureSize(Context2d ctx, Rectangle rectangle) {
		updateFontSize(ctx);
		
		double newSize;
		//define textwidth as width of new text plus an offset. textwidth is always based on the current fontsize, thus all other values have to take the SCALE into account
		double textWidth = ctx.measureText(rectangle.getFigure().getText()).getWidth() + TextRenderer.TEXT_PADDING * Viewport.SCALE;

		//size should be expanded if bigger then figure-width, else use default size.
		newSize = (Rectangle.DEFAULT_WIDTH * Viewport.SCALE <= textWidth) ? textWidth : Rectangle.DEFAULT_WIDTH * Viewport.SCALE;

		//limit rectangle width maximum
		if (newSize > Rectangle.MAX_WIDTH * Viewport.SCALE) {
			newSize = Rectangle.MAX_WIDTH * Viewport.SCALE;
		}		
		
		//get the width difference between the new size and the present size, rounded to prevent flickering due to minimal size changes
		double deltaWidth = newSize - rectangle.getW();
		//figure width is corrected by moving the endpoint horizontal using this delta
		rectangle.getPosEnd().moveBy(deltaWidth, 0);
	}
	
	/**
	 * Helper to refresh the font size, as text should also scale along with zoom levels.
	 * @param ctx
	 */
	private static void updateFontSize(Context2d ctx) {
		ctx.setFont("normal " + DEFAULT_FONT_SIZE * Viewport.SCALE + "px Verdana, Geneva, sans-serif");
	}
	
	/**
	 * Gets the "crossPoint" using umgestellte equation from {@link #getControlPoint()},
	 * to get the center point of the curve, which is most suitable point to display the text at.
	 * @return
	 */
	private static Point getTextPoint(Point cp, Point s, Point e) {
		double cpx = (cp.getX() + (s.getX() + e.getX()) / 2) / 2;
		double cpy = (cp.getY() + (s.getY() + e.getY()) / 2) / 2;
		
		return new Point(cpx, cpy);
	}
	
	/**
	 * Draw text onto a figure.
	 * @param ctx the drawing context
	 * @param figure the figure to draw the text on
	 */
	public static void draw(Context2d ctx, Rectangle figure) {
		//text should have less shadow
		ctx.setShadowOffsetX(1 * Viewport.SCALE);
		ctx.setShadowOffsetY(1 * Viewport.SCALE);
		
		String text = figure.getFigure().getText();
		
		//measure the text iteratively and calculate max appended with three dots "..."
		if (ctx.measureText(text).getWidth() + TEXT_PADDING * Viewport.SCALE >= Rectangle.MAX_WIDTH * Viewport.SCALE) {
			text = getAbbreviatedText(ctx, text, Rectangle.MAX_WIDTH * Viewport.SCALE);
		}
		
		//draw text
		ctx.setFillStyle(HEADER_COLOR);
		
		//center the text in the rectangle, as P(x, y) is the left upper starting point of the rectangle.
		//normalize to scale as text will be measured for default unscaled font
		double midX = correctX(ctx, figure.getPosStart().getX(), figure.getW(), text);
		double midY = correctY(figure.getPosStart().getY(), figure.getH());
		
		//correct the size as text may have been abbreviated and thus changed size
		ctx.fillText(text, midX, midY);
	}
	
	/**
	 * Get the abbreviated text and appends three dots '...' to indicate that not the whole string is painted.
	 * This is just a temporary string used for drawing the text into the canvas.
	 * @param ctx
	 * @param text
	 * @param maxWidth 
	 * @return
	 */
	private static String getAbbreviatedText(Context2d ctx, String text, double maxWidth) {
		double textWidth = ctx.measureText(text).getWidth();
		
		for (int i = 0; i < text.length(); i++) {
			double itrSize = ctx.measureText(text.substring(0, i).concat(ELLIPSIS + TEXT_PADDING)).getWidth();
			if (itrSize > maxWidth || itrSize > textWidth) {
				return text.substring(0, i).concat(ELLIPSIS);
			}
		}
		
		return text;
	}
	
	/**
	 * Width of rectangle - width of text = free space in the rectangle.
	 * Divide the free space by 2 to have the same space on both ends of the text.
	 */
	private static double correctX(Context2d ctx, double x, double w, String text) {
		double textWidth = ctx.measureText(text).getWidth();
		return x + (w - textWidth) / 2;
	}
	
	/**
	 * Move the text down half the size of the rectangle height to display is middle aligned.
	 * As the text has also a pixel font size, also add half of this.
	 * Also the line width of the border has to be taken into account.
	 */
	private static double correctY(double y, double h) {
		return  y + h / 2 + DEFAULT_FONT_SIZE * Viewport.SCALE / 2 - Drawable.DEFAULT_BORDER_SIZE * Viewport.SCALE + 1;
	}
	
	
	
	/**
	 * x + width/2 gives the middle in x horizontal direction.
	 * As the edit box should not start there, but should also have its middle there,
	 * half of the box size has to be substracted.
	 * @param drawable
	 * @return the X center coordinate of the drawable
	 */
	public static double centerX(Rectangle drawable) {
		return drawable.getPosStart().getX() + drawable.getW() / 2 - EditTextBox.BOX_SIZE / 2;
	}
	
	
	/**
	 * y + height/2 gives the middle in y vertical direction.
	 * As the box should not start there, but should have its content middle aligned,
	 * half of the text-line-height and the border of the input has to be substracted.
	 * @param drawable
	 * @param textBox 
	 * @return the Y center coordinate of the drawable
	 */
	public static double centerY(Rectangle drawable, EditTextBox textBox) {
		return drawable.getPosStart().getY() + drawable.getH() / 2 - DEFAULT_LINE_HEIGHT / 2 - Drawable.DEFAULT_BORDER_SIZE;
	}
}