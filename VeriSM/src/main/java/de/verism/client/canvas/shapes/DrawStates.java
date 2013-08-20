package de.verism.client.canvas.shapes;

import com.google.gwt.canvas.dom.client.Context2d;


/**
 * Wraps all available states that can be used to alter the drawing behavior of the {@link Drawable}.
 * 
 * Advantage of this StatePattern variant is that from code always only the Drawable.draw(ctx) method
 * has to be executed. And if it should be drawn eg selected, one does not have to care calling a specific
 * method for this. Just change the DrawState accordingly before draw() is invoked.
 * @author Daniel Kotyk
 *
 */
public class DrawStates {
	//predefined states that can be used for drawing objects on the canvas
    public static final DrawState DRAW_DEFAULT = new DrawDefault();
    public static final DrawState DRAW_SELECT = new DrawSelected();
    public static final DrawState DRAW_DRAG = new DrawDragging();
    public static final DrawState DRAW_DRAG_NOT_SEL = new DrawDraggingNotSelected();
    
    /**
     * Template for a new drawing state.
     * @author Daniel Kotyk
     *
     */
    public interface DrawState {
        void draw(Context2d ctx, Drawable drawable);
    }
    
    /**
     * Default drawing behavior.
     * @author Daniel Kotyk
     *
     */
    private static class DrawDefault implements DrawState {
        @Override
        public void draw(Context2d ctx, Drawable drawable) {
            drawable.drawDefault(ctx);
        }
    }
    
    /**
     * Draw an element selected.
     * @author Daniel Kotyk
     *
     */
    private static class DrawSelected implements DrawState {
        @Override
        public void draw(Context2d ctx, Drawable drawable) {
            drawable.drawSelected(ctx);
        }
    }
    
    /**
     * Draw an element while dragging.
     * @author Daniel Kotyk
     *
     */
    private static class DrawDragging implements DrawState {
        @Override
        public void draw(Context2d ctx, Drawable drawable) {
            drawable.drawDragging(ctx);
        }
    }
    
    /**
     * Draw an element while dragging, but not selected (like a line on a selected rectangle).
     * @author Daniel Kotyk
     *
     */
    private static class DrawDraggingNotSelected implements DrawState {
        @Override
        public void draw(Context2d ctx, Drawable drawable) {
            drawable.drawDraggingNotSelected(ctx);
        }
    }
}