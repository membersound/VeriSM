package de.verism.client.util;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;

import de.verism.client.canvas.drawing.CanvasArea;

/**
 * Add resize behavior to the whole browser window.
 * @author Daniel Kotyk
 */
public class WindowResizeHandler implements ResizeHandler{

	private CanvasArea canvasArea;
	
	public WindowResizeHandler(CanvasArea canvasArea) {
		this.canvasArea = canvasArea;
	}

	/**
	 * Resize the canvas.
	 * @param height 
	 * @param width 
	 */
	private void resize(int width, int height) {
		Canvas canvas = canvasArea.getCanvas();
		
	     //modify canvas size if different from before resize
	     if (canvas.getCoordinateSpaceWidth() != width) {
	    	 canvas.setCoordinateSpaceWidth(width);
	     }
	     if (canvas.getCoordinateSpaceHeight() != height) {
	    	 canvas.setCoordinateSpaceHeight(height);
	     }
	        
	     canvas.setPixelSize(width, height);
	}
	

	@Override
	public void onResize(ResizeEvent event) {
		resizeTimer.cancel();
		resizeTimer.schedule(250);
	}
	

	/**
	 * Timer prevents the resize to take place every ms.
	 * resizing should only take place when the user finished browser window resize,
	 * and not in between while(!) resizing.
	 */
	Timer resizeTimer = new Timer() {
		@Override
		public void run() {
			//retrieve the new size of the canvas
		    int width = canvasArea.getElement().getParentElement().getClientWidth();
		    int height = canvasArea.getElement().getParentElement().getClientHeight();
		    
			resize(width, height);
		}
	};
}
