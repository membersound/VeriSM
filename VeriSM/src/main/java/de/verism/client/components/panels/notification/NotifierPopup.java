package de.verism.client.components.panels.notification;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A popup showing notification messages and auto hides after some seconds.
 * @author Daniel Kotyk
 *
 */
public class NotifierPopup extends PopupPanel {
	//timeout for the notification bubble
    private static final int TIMEOUT = 3000;
    
    //height of the header
	protected static final int DOCK_HEIGHT = 45;
    
	/**
	 * Creates a colored notification popup displaying the given text.
	 * @param text
	 */
	public NotifierPopup(String text, NotifierState state) {
		//autohide, but not modal
        super(true, false);
        setAnimationEnabled(true);
        setWidget(new NotificationLabel(text, state.getIcon()));
        setStyleName(NotificationResources.INSTANCE.css().bubbleStyle());
        
        show(state);
    }

	/**
	 * Shows the popup.
	 * @param timeout the time after that the popup should autohide
	 */
    public void show(NotifierState state) {
    	addStyleName(state.getStyle());
    	
    	//display centered on top of the canvas
    	//the callback is entered after construction of the popup, but before made visible.
    	//this provides possibility to intercept and change the popups' position, as popup width
    	//differs according to shown text and thus cannot set the XY position directly.
    	setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				setPopupPosition((int) ((Window.getClientWidth() - offsetWidth) / 2), DOCK_HEIGHT);
			}
    	});

        //auto hide after x seconds
        Timer t = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };
        t.schedule(TIMEOUT);
    }
}