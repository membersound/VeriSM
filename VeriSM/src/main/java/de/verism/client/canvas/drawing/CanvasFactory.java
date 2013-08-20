package de.verism.client.canvas.drawing;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Window;

/**
 * Factory for providing a single method when creating a canvas layer.
 * @author Daniel Kotyk
 *
 */
public class CanvasFactory {

	/**
	 * Provides a new canvas.
	 * @return the canvas sized to the browser window
	 */
	public static Canvas getCanvas() {
		final Canvas canvas = Canvas.createIfSupported();
		
		//sizing the canvas according to the canvas
		canvas.setCoordinateSpaceHeight(Window.getClientHeight());
        canvas.setCoordinateSpaceWidth(Window.getClientWidth());
        return canvas;
	}
}
