package de.verism.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.components.dialog.StartupDialog;
import de.verism.client.components.footer.CanvasFooter;
import de.verism.client.components.panels.error.ErrorTextBoxResources;
import de.verism.client.components.panels.io.spreadsheet.Spreadsheet;
import de.verism.client.components.panels.notification.NotificationResources;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.data.Cache;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Daniel Kotyk
 */
public class Verism implements EntryPoint {
    interface Binder extends UiBinder<Widget, Verism> {}

    TextBox nameField;
    
	@UiField CanvasFooter canvasHeader;
    @UiField DialogBox dialogBox;
    @UiField StartupDialog startupDialog;
    @UiField Spreadsheet inputPanel, outputPanel;
    @UiField DockLayoutPanel dockLeft, dockRight;
    
	/**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
    	initLogging();
    		     
        //rootLayoutPanel provides resize of browser window so that all elements resize accordingly
        RootLayoutPanel.get().add(GWT.<Binder> create(Binder.class).createAndBindUi(this));

        //show the startup dialog on pageload
        startupDialog.show();
        //the slider must be initialized after all objects have been created as it depends both on the canvas header and the canvas itself
        canvasHeader.getZoomHandler().registerCanvasWheel(CanvasArea.get().getCanvas());
        
        initDataResources();
        initCssStyles();
	}
    
    /**
     * Defines how to handle all uncaught exceptions: send them via RPC to the server and save them in the serverlog.
     */
    private void initLogging() {
    	GWT.setUncaughtExceptionHandler(new   
        	GWT.UncaughtExceptionHandler() {  
        	public void onUncaughtException(Throwable e) {  
        		Logger logger = Logger.getLogger("rootLogger");
        	    logger.log(Level.SEVERE, "client side exception:", e);
        	    
        	    //also notify the user of the failure
        	   	new NotifierPopup("An internal exception occured, and logged on the server side. Please restart the application.", NotifierState.ERROR);
        	}
        });
    }
    
    /**
     * For custom css styles, #ensureInjected() must be called on every resource class.
     * Otherwise the css classes cannot be used from program code (eg change style programmatically).
     */
    private void initCssStyles() {
    	ErrorTextBoxResources.INSTANCE.css().ensureInjected();
    	NotificationResources.INSTANCE.css().ensureInjected();
	}

	/**
     * Sets up the data provider which serves all data across the application.
     */
	private void initDataResources() {
		Cache.get().setInputPanel(inputPanel);
		Cache.get().setOutputPanel(outputPanel);
		Cache.get().setCanvasFooter(canvasHeader);
	}
}
