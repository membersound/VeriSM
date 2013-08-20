package de.verism.client.components.panels.menu;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A {@link DialogBox} presenting an image the can be saved to the harddisc.
 * @author Daniel Kotyk
 *
 */
public class ImagePopup extends DialogBox {

	/**
	 * Create the image popup.
	 * @param png the image string to show
	 */
	public ImagePopup(Image canvasImage) {
		super(true, true);
		
	    canvasImage.setWidth(Window.getClientWidth() / 3 + "px");
	    setAnimationEnabled(true);
	    setGlassEnabled(true);
	    setTitle("Save As...");
	    setText("Save this image with rightclick > Save As...");
	    setWidget(canvasImage);

	    setPopupPositionAndShow(new PopupPanel.PositionCallback() {
	    	@Override
	        public void setPosition(int offsetWidth, int offsetHeight) {
	            int left = (Window.getClientWidth() - offsetWidth) / 3;
	            int top = (Window.getClientHeight() - offsetHeight) / 3;
	            setPopupPosition(left, top);
	          }
	        });
	}

}
