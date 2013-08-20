package de.verism.client.components.panels.io.spreadsheet.toggle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasArea;


/**
 * A component serving a toggle button for showing + hiding the dock provided in the constructor.
 * @author Daniel Kotyk
 *
 */
public class ToggleSpreadsheet extends Composite {
	interface Binder extends UiBinder<Widget, ToggleSpreadsheet> {}
	
	//time for showing and hiding the {@link #dock}
    private static final int ANIMATION_TIME = 250;
	private DockLayoutPanel dock;
	
	//icons
	private static final String LEFT_POINTING = "\u25C4";
	private static final String RIGHT_POINTING = "\u25BA";
	
	//holds the size the spreadsheet has been created with. Used to restore the size on show.
	private int defaultSize = 13;

    @UiField
	ToggleButton toggleButton;
    
    interface ToggleSpreadsheetResources extends ClientBundle {
    	public static final ToggleSpreadsheetResources INSTANCE = GWT.create(ToggleSpreadsheetResources.class);
    	
    	@Source("open.png")
        ImageResource rightPointing();
    	
    	@Source("close.png")
        ImageResource leftPointing();
      }
    
    /**
     * @param dock the dock to be toggled.
     */
	@UiConstructor
	public ToggleSpreadsheet(DockLayoutPanel dock) {
		this.dock = dock;
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}
	
	/**
	 * Shows and hides the spreadsheet docks.
	 * @param evt
	 */
	@UiHandler("toggleButton")
	void toggle(ClickEvent evt) {
		//prepare change values according to the button state
	   	boolean isDown = toggleButton.getValue();
	   	//restore initial size of the dock
		int dockSize = isDown ? 0 : defaultSize;
		//the wrapper around the dock, which serves for showing and hiding the spreadsheet
	   	DockLayoutPanel dockWrapper = (DockLayoutPanel) dock.getParent();
	   	
	   	//prepare button text, which must point in opposite direction for left and right buttons
	   	String left = rtl ? LEFT_POINTING : RIGHT_POINTING;
	   	String right = rtl ? RIGHT_POINTING : LEFT_POINTING;
	   	String icon = isDown ? left : right ;
	   	String text = isDown ? "show" : "hide";
	   	String buttonText = rtl ? icon + " " + text : text + " " + icon;

	   	//change dock and button
		dockWrapper.setWidgetHidden(dock, isDown);
	  	dockWrapper.setWidgetSize(dock, dockSize);
	  	dockWrapper.animate(ANIMATION_TIME);
	  	
	  	//modify button text
	  	setText(buttonText);
	  	CanvasArea.get().refresh();
	}
	
	/**
	 * Sets a new text for the button.
	 * Must also set the Face text as normal setText() will only affect the hovering text.
	 * @param text
	 */
	private void setText(String text) {
	  	toggleButton.setText(text);
	  	toggleButton.getUpFace().setText(text);
	  	toggleButton.getDownFace().setText(text);
	}
	
	
	//track if button is left or right edge aligned
	private boolean rtl;
	
	/**
	 * Flips the button text to the right edge.
	 * @param isRtl
	 */
	public void setRtl(boolean rtl) {
		this.rtl = rtl;
		defaultSize = 20;
	}
}
