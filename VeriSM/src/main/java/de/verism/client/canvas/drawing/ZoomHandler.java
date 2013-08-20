package de.verism.client.canvas.drawing;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;

import de.verism.client.com.google.gwt.incubator.SliderBar;

/**
 * Handler for the simultaneous zooming of both the canvas and the slider.
 * @author Daniel Kotyk
 *
 */
public class ZoomHandler {
	//zone of the zoom values
	public static final double MIN = 0.1, MAX = 3, DEFAULT = 1;
	
	//tracks always the last value of the slider
	private double oldValue = 1;

	private SliderBar slider;
	
	/**
	 * Create using the slider itself.
	 * @param slider
	 */
	public ZoomHandler(SliderBar slider) {
		this.slider = slider;
	}

	/**
	 * Updates the current value both for the slider and the canvas.
	 */
	public void updateValue() {
		//determine the difference of the slider move
		double delta = oldValue - slider.getCurrentValue();
		oldValue = slider.getCurrentValue();
		
		Panel canvasPanel = CanvasArea.get().getWrapperPanel();
		Viewport.zoom(delta / Viewport.ZOOM_LEVEL, canvasPanel.getOffsetWidth() / 2, canvasPanel.getOffsetHeight() / 2);
		scheduleZoomTimer();
	}
	
	//tracks is the scheduled zoom is to be executed
	private boolean toBeZoomed = false;
	
	/**
	 * Provides public access for the slider to also trigger the zoom timer.
	 */
	public void scheduleZoomTimer() {
		//as every mouse wheel event executes this method, it may be triggered at a higher framerate then the refresh.
		//the toBeZoomed variable is reset within the zoom timer, and only then a new zoom can be scheduled here
		if(!toBeZoomed) {
			toBeZoomed = true;
			zoomTimer.schedule(CanvasArea.FRAMERATE * 2);
		}
	}
	
	/**
	 * Timer for zooming.
	 */
	private Timer zoomTimer = new Timer() {
		@Override
		public void run() {
			CanvasArea.get().refresh();
			toBeZoomed = false;
		}
	};
	
	/**
	 * Helper to register the canvas mouse wheel event to the slider.
	 * This must be called from the {@link CanvasArea} class after instantiation.
	 * @param canvas
	 */
	public void registerCanvasWheel(final Canvas canvas) {
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent evt) {
				//make a threadsave integer, if exists. then for every wheel incr or decrement according to delta.
				//in the zooming, start a timer that decr this threadsafe counter on a specific framerate.
				//if cntr is = 0, stop the timer.
				double newScale = Viewport.zoom(evt.getDeltaY(), evt.getRelativeX(canvas.getElement()), evt.getRelativeY(canvas.getElement()));
				scheduleZoomTimer();
				
				//update value without firing the change event,
				//as this would otherwise cause the method above to execute and result in infinite loop
				slider.setCurrentValue(newScale, false);
				oldValue = newScale;
			}
		});
	}
}
