package de.verism.client.canvas.scrolling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.scrolling.arrow.Direction;

/**
 * Defines the scrolling edge area around the canvas.
 * MouseOver will scroll the diagram to the desired direction (8 possible).
 * 
 * @author Daniel Kotyk
 *
 */
public class ScrollingArea extends Composite {
	
	interface Binder extends UiBinder<Widget, ScrollingArea> { }

	private static final String BACKGROUND_COLOR = "#e2eaf2";
	
	@UiField
	CanvasArea canvasArea;

	// edges for scrolling
	// binding to these fields is done by @UiHandler, method name does not matter.
	@UiField
	FocusPanel 	arrowWestTop, arrowWestMiddle, arrowWestBottom,
				arrowNorthLeft, arrowNorthMiddle, arrowNorthRight,
				arrowEastTop, arrowEastMiddle, arrowEastBottom,
				arrowSouthLeft, arrowSouthMiddle, arrowSouthRight;
	
	/**
	 * Adds Mouse Listeners to the focus panels for all 8 scrolling directions.
	 * Each straight direction only has 1 FocusPanel, but the corners each have 2.
	 * Therefore the panels map has to hold an ArrayList of 1 or 2 panels.
	 * 
	 */
	public ScrollingArea() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		//temporary map for adding mouseHandlers to all scroll panels by iteration.
		//this cannot be done using @UiHandler as an ui-handler event is not aware of its source, which is required for hovering.
		final Map<Direction, List<FocusPanel>> scrollPanels = new HashMap<Direction, List<FocusPanel>>() {
			{
				put(Direction.NORTH, Arrays.asList(arrowNorthMiddle));
				put(Direction.EAST, Arrays.asList(arrowEastMiddle));
				put(Direction.SOUTH, Arrays.asList(arrowSouthMiddle));
				put(Direction.WEST, Arrays.asList(arrowWestMiddle));
				
				put(Direction.NORTH_EAST, Arrays.asList(arrowNorthRight, arrowEastTop));
				put(Direction.SOUTH_EAST, Arrays.asList(arrowEastBottom, arrowSouthRight));
				put(Direction.SOUTH_WEST, Arrays.asList(arrowWestBottom, arrowSouthLeft));
				put(Direction.NORTH_WEST, Arrays.asList(arrowWestTop, arrowNorthLeft));
			}
		};
		
		for (final Entry<Direction, List<FocusPanel>> entry : scrollPanels.entrySet()) {
			//one of 8 directions
		    final Direction direction = entry.getKey();

		    //add mouse listeners to every panel by iteration
		    for (final FocusPanel panel : entry.getValue()) {
		    	
		    	//start the scrolling on mouse down
		    	panel.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						setNextDirection(direction);
						
						//scroll at the same framerate as the canvas is refreshed
						scrollTimer.scheduleRepeating(CanvasArea.FRAMERATE * 2);
						
						//highlight the panel. For corners, 2 panels have to be highlighted
						for (FocusPanel cornerPanel : scrollPanels.get(direction)) {
							activateFocusColor(cornerPanel);
						}
					}
		    	});
		    	
		    	//cancel the scrolling on mouseup or mouseout
		    	panel.addMouseUpHandler(new MouseUpHandler() {
					@Override
					public void onMouseUp(MouseUpEvent event) {
						scrollTimer.cancel();
					}
		    	});

				//when mouse cursor leaves the scrolling area
				panel.addMouseOutHandler(new MouseOutHandler() {
					@Override
					public void onMouseOut(MouseOutEvent event) {
						scrollTimer.cancel();
						
						//un-highlight the panels
						for (FocusPanel cornerPanel : scrollPanels.get(direction)) {
							deactivateFocusColor(cornerPanel);
						}
					}
				});
		    }
		}
	}
	
	/**
	 * Change the focus panel color to indicate hovering.
	 * Mostly used for corner cases where more than one highlighting is necessary.
	 * @param panel
	 */
	private void activateFocusColor(FocusPanel panel) {
		panel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
	}
	
	/**
	 * Revert the focus panel color to the initial value.
	 * @param panel
	 */
	private void deactivateFocusColor(FocusPanel panel) {
		//inherit reverts the bg-color to the initial color
		panel.getElement().getStyle().setBackgroundColor("inherit");
	}
	
	//-------- Scrolling Timer ----------
	
	//the direction to be scrolled to
	private Direction d;
	
	//scroll amount in pixel
	private static final int SCROLL_AMOUNT = 20;
	
	private void setNextDirection(Direction d) {
		this.d = d;
	}
	
	/**
	 * Timer performing the scrolling.
	 */
	private Timer scrollTimer = new Timer() {
		@Override
		public void run() {
			Viewport.OFFSET_X += d.getPoint().getX() * SCROLL_AMOUNT / Viewport.SCALE;
			Viewport.OFFSET_Y += d.getPoint().getY() * SCROLL_AMOUNT / Viewport.SCALE;
			canvasArea.refresh();
		}
	};
}
