package de.verism.client.components.icons;

import com.google.gwt.cell.client.ButtonCellBase;
import com.google.gwt.cell.client.TextButtonCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.widget.client.TextButton;

/**
 * Creates a button that can have an icon attached.
 * Usage: <my:IconTextButton icon='{res.theIcon}' />
 * 
 * Special about this extension is not only the ui-binder usage,
 * but also the ability to use it just as a normal @UiField TextButton in code.
 * This separates the icon clearly from the logic, as it only occurs in the ui.xml.
 * @author Daniel Kotyk
 *
 */
public class IconTextButton extends TextButton {
	/**
	 * Creates a new button using the appearance pattern.
	 */
	public IconTextButton() {
		super(new TextButtonCell.DefaultAppearance(MyResources.INSTANCE), "");
	}
	
	/**
	 * Pass the icon for the button via ui:binder.
	 * @param icon
	 */
    public void setIcon(ImageResource icon) {
    	((TextButtonCell) getCell()).setIcon(icon); 
    }
    
    /**
     * Defines the resource used for the textbutton creation.
     * This is the only way to provide a custom css class for a {@link TextButton}.
     * @author Daniel Kotyk
     *
     */
    interface MyResources extends ButtonCellBase.DefaultAppearance.Resources {
    	  public static final MyResources INSTANCE = GWT.create(MyResources.class);
    	
    	  @Override
    	  @Source(value = {ButtonCellBase.DefaultAppearance.Style.DEFAULT_CSS, "TextButton.css"})
    	  ButtonCellBase.DefaultAppearance.Style buttonCellBaseStyle();
    }
}
