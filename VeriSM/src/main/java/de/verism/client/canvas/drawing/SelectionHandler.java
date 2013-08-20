package de.verism.client.canvas.drawing;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Window;

import de.verism.client.canvas.shapes.Drawable;

/**
 * Provides the selected object by retrieving it from the shadow canvas context.
 * Only by using shadowcanvas it is possible to select beziercurves.
 * @author Daniel Kotyk
 *
 */
public class SelectionHandler {
	//shadowCanvas is the selectionLayer
	private Canvas shadowCanvas;
	
	/**
	 * Initialize the shadow canvas.
	 */
	public SelectionHandler() {
		shadowCanvas = CanvasFactory.getCanvas();
	}

	/**
	 * Returns the current selection by using the shadowcanvas context:
	 * it draws the figures one after the other, and checks after each if the color under
	 * the mouse cursor is = {@link Drawable.SHADOW_COLOR}.
	 * 
	 * This way, the pixel under mouse
	 * is white as long as objects that are not under the mouse are drawn.
	 * As soon as the object under mouse is drawn, the pixel color changes.
	 * 
	 * @param selectables
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Drawable getSelection(List<? extends Drawable> selectables, int mouseX, int mouseY) {
		//get the shadow context
		Context2d gctx = shadowCanvas.getContext2d();
		gctx.clearRect(0, 0, Window.getClientWidth(), Window.getClientHeight());
		
		//iterate all figures of the visible canvas
		for (Drawable drawable : selectables) {
			//skip drawables outside the actual viewport, as they can of course not be selected.
			//this cuts down hitpoint testing algorithm time respectable for big projects
			if (!Viewport.isInside(drawable)) {
				continue;
			}
			
			//get the pixel color under mouse pointer
			drawable.drawShadow(gctx);
			try {
				CanvasPixelArray data = gctx.getImageData(mouseX, mouseY, 1, 1).getData();
				String hex = getHexValue(data.get(0), data.get(1), data.get(2));
	
				//if color matches, this object is the to be selected
				if (hex.equals(Drawable.SHADOW_COLOR)) {
					return drawable;
				}
			} catch (Exception e) {
				//there may be NS_ERROR_OUT_OF_MEMORY errors when making quick canvas moves and suddenly trying to select a figure.
				//as a result, just null is returned below
				break;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the hex value from rgb values.
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @return hex
	 */
	private String getHexValue(int r, int g, int b) {
		return "#"
			+ toHexValue(r)
			+ toHexValue(g)
			+ toHexValue(b);
	}
	
    /**
     * Converts a rgb number to it's hex representation.
     * As java will convert a r=0 to hex=0 instead of 00, this is also handles here.
     * Further 255 becomes FF.
     * 
     * @param number a number between 0 and 255
     * @return String the hex
     */
    private String toHexValue(int number){
        String hex = Integer.toHexString(number & 0xff).toUpperCase();

        if (hex.length() < 2) {
            return hex += "0";
        }
        return hex;
    }
}
