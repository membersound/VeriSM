package de.verism.client.components.panels.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The submenu popup for the menu header.
 * @author Daniel Kotyk
 *
 */
public class SubMenuPopup extends PopupPanel {
	interface Binder extends UiBinder<Widget, SubMenuPopup> {}

	@UiField FlowPanel placeholder;
	
	public SubMenuPopup(Widget widget) {
		setWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		setStyleName("none");
		
		//auto hide on a click
		setAutoHideEnabled(true);
		addHandlers();
		
		//add the content
		placeholder.add(widget);
	}

	/**
	 * Helper to apply submenu specific handlers.
	 */
	private void addHandlers() {
		//auto hide on focus loss
		addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent evt) {
				hide();
			}
		}, MouseOutEvent.getType());
		
		//auto hide on click
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent evt) {
				hide();
			}
		}, ClickEvent.getType());
	}
}
