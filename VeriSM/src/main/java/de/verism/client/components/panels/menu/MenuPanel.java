package de.verism.client.components.panels.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.dialog.edit.EditorDialog;
import de.verism.client.components.dialog.login.LoginPanel;
import de.verism.client.components.dialog.login.LoginState;

/**
 * Menu on top of the screen. Provides Save + Export actions for the project.
 * @author Daniel Kotyk
 *
 */
public class MenuPanel extends Composite {
	interface Binder extends UiBinder<Widget, MenuPanel> {}
	
	@UiField Hyperlink help, project, export;
	//static to make the link updateable within  #updateLoginMenu().
	@UiField static Hyperlink login;
	
	public MenuPanel() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}

	@UiHandler("help")
	void onHelp(ClickEvent evt) {
		DecoratedPopupPanel popup = new DecoratedPopupPanel(true, true);
		popup.setAnimationEnabled(true);
		popup.setWidget(new HelpPanel());
		popup.showRelativeTo(help);
	}

	/**
	 * Provides the login and register dialog.
	 * @param evt
	 */
	@UiHandler("login")
	void onLogin(ClickEvent evt) {
		if (LoginState.INSTANCE.isLoggedOut()) {
			LoginPanel loginPanel = new LoginPanel();
			loginPanel.setWidth("260px");
			
			DialogBox dialog = new EditorDialog();
			dialog.setGlassEnabled(true);
			dialog.setText("Login or Register");
			dialog.setTitle("Login or Register");
			dialog.add(loginPanel);
			
			//add menu handling on login
			dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					//callback notification: logged in successfully; change login to logout text link
					updateLoginMenu();
				}
			});
			
			dialog.center();
			dialog.show();
		} else {
			LoginState.INSTANCE.setLoggedOut(true);
			login.setText("Login");
		}
	}
	
	@UiHandler("project")
	void onHoverProject(ClickEvent evt) {
		toggleSubMenu(new ProjectMenu(), project);
	}

	
	@UiHandler("export")
	void onHoverExport(ClickEvent evt) {
		toggleSubMenu(new ExportMenu(), export);
	}

	/**
	 * Only hide the submenu if the mouse leaves the menu entry horizontally.
	 * @param evt
	 */
	@UiHandler("project")
	void onOutProject(MouseOutEvent evt) {
		hideSubMenu(evt, project);
	}
	
	@UiHandler("export")
	void onOutExport(MouseOutEvent evt) {
		hideSubMenu(evt, export);
	}
	
	//the submenu to be shown
	private PopupPanel subMenu = new PopupPanel();
	
	/**
	 * Helper to show and hide the submenu.
	 * @param menu
	 * @param link 
	 */
	private void toggleSubMenu(Composite menu, Hyperlink link) {
		if (!subMenu.isShowing()) {
			subMenu = new SubMenuPopup(menu);
			
			//show the submenu with an offset
			subMenu.setPopupPosition(link.getAbsoluteLeft() - 9, link.getAbsoluteTop() + 27);
			subMenu.show();
		} else {
			subMenu.hide();
		}
	}
	
	/**
	 * Helper to hide the submenu on mouseout.
	 * Only hide the submenu if the mouse leaves the menu entry horizontally.
	 * @param menu
	 */
	private void hideSubMenu(MouseOutEvent evt, Hyperlink menu) {
		if (evt.getClientX() < menu.getAbsoluteLeft()
				|| evt.getClientX() > menu.getAbsoluteLeft() + menu.getOffsetWidth()) {
			subMenu.hide();
		}
	}

	/**
	 * Helper to update login dependent menu entries.
	 */
	public static void updateLoginMenu() {
		if (!LoginState.INSTANCE.isLoggedOut()) {
			login.setText("Logout '" + LoginState.INSTANCE.getUsername() + "'");
		} else {
			login.setText("Login");
		}
	}
}
