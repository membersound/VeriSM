package de.verism.client.components.panels.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Help menu component to wrap the whole help text.
 * The whole component will be placed inside a {@link DecoratorPopupPanel} from {@link MenuPanel}.
 * @author Daniel Kotyk
 *
 */
public class HelpPanel extends Composite {
	interface Binder extends UiBinder<Widget, HelpPanel> {}
	
	@UiField
	DecoratedTabPanel tabPanel;

	public HelpPanel() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		//initial select the first help entry
		tabPanel.selectTab(0);
	}
}

