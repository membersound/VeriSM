package de.verism.client.components.icons;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A menu item that can have an icon attached.
 * GWT does not provide a label + icon as menu entry, thus extending it.
 * @author Daniel Kotyk
 *
 */
public class IconMenuItem extends MenuItem {
	//the renderer for the context menu icon
	private static ImageResourceRenderer renderer = new ImageResourceRenderer();
	
	/**
	 * Creates a context menu with an icon that can be initialized using ui-binder.
	 * Usage: <IconMenuItem text="test" icon="{resource.myIcon}" />
	 * 
	 * @param text the context menu label
	 * @param icon the icon in front of the label
	 */
	@UiConstructor
	public IconMenuItem(String text, ImageResource icon) {
		super(prepareMenu(text, icon));
	}
	
	/**
	 * Prepares the menu text as safeHtml.
	 * @param text the label text
	 * @param icon the icon to render
	 * @return icon and text combined as safeHtml
	 */
	public static SafeHtml prepareMenu(String text, ImageResource icon) {
		//build the context menu entry
		SafeHtml html = renderer.render(icon);
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(html)
			   .appendEscaped(text);
		
		//set the menu entry
		return builder.toSafeHtml();
	}
}
