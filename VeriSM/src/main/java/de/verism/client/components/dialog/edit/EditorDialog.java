package de.verism.client.components.dialog.edit;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;

import de.verism.client.canvas.shapes.rendering.TextRenderer;

/**
 * Edit dialog taking a widget to display inside the dialog.
 * This is the entry class for any edit popups.
 * @author Daniel Kotyk
 *
 */
public class EditorDialog extends DialogBox {
	
	//text length of the popup header
	private static final int HEADER_SIZE = 50;
	
	/**
	 * Constructs a input panel for editing objects.
	 */
	public EditorDialog() {
		//init a modal dialog that auto hides
		super(true, true);
		setAnimationEnabled(true);
	}
	
	/**
	 * Takes an input provider, used for editing the domain model objects.
	 * @param input
	 */
	public EditorDialog(IsInputProvider input) {
		this();
		
		//use the base EditPanel and pass the InputProvider interface
		setWidget(new BaseEditor(input));
	}
	
	/**
	 * Interceptor for canceling the editor dialog on ESC key.
	 * Shows again the strength of a global Editor used for every edit menu,
	 * so that this single method implements the ESC behavior globally for all menus. 
	 */
    @Override
    protected void onPreviewNativeEvent(NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (event.getTypeInt() == Event.ONKEYDOWN) {
        	if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
        		hide();
            }
        }
    }
	
	/**
	 * Limits the header title size to 40 characters, so that the PopupPanel is
	 * not oversized just because of a very long header.
	 */
	@Override
	public void setText(String text) {
		if (text.length() >= HEADER_SIZE) { 
			text = text.substring(0, HEADER_SIZE).concat(TextRenderer.ELLIPSIS);
		}
		super.setText(text);
	}
}
