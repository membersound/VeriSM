package de.verism.client.components.dialog.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.icons.IconTextButton;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.rpc.AccountService;
import de.verism.client.rpc.AccountServiceAsync;
import de.verism.client.util.UiHelper;

/**
 * Component displaying login and register view.
 * @author Daniel Kotyk
 *
 */
public class LoginPanel extends Composite {
	interface Binder extends UiBinder<Widget, LoginPanel> {}
	
	//contstants representing the different views
	private static final int VIEW_LOGIN = 0;
	private static final int VIEW_REGISTER = 1;
	private static final int VIEW_WELCOME = 2;
	
	@UiField DeckPanel deckPanel;
	@UiField TextBox username, usernameReg;
	@UiField PasswordTextBox password, passwordReg, passwordReg2;
	@UiField IconTextButton login, register;
	@UiField Hyperlink toLogin, toRegister;
	@UiField InlineLabel userLabel;
	@UiField ErrorField errorPanel;
	@UiField Image loadingIndicator, loadingIndicatorReg;
	
    @UiConstructor
    public LoginPanel() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		deckPanel.showWidget(VIEW_LOGIN);
    }
	
    //the rpc service to register new users
    private AccountServiceAsync accountService = GWT.create(AccountService.class);

    //the callback handling the server return values
    class AccountCallback implements AsyncCallback<String> {
		@Override
		public void onFailure(Throwable caught) {
			setError(caught.getMessage());
			loadingIndicator.setVisible(false);
			loadingIndicatorReg.setVisible(false);
		}

		@Override
		public void onSuccess(String username) {
			updateUser(username);
			hide(getParent());
			new NotifierPopup("Welcome " + LoginState.INSTANCE.getUsername() + "!", NotifierState.SUCCESS);
		}
    }
    
    /**
     * Exchanges the login input fields with the registration input fields.
     * @param evt
     */
    @UiHandler({"toRegister", "toLogin"})
    void swapView(ClickEvent evt) {
    	deckPanel.showWidget(deckPanel.getVisibleWidget() == VIEW_LOGIN ? VIEW_REGISTER : VIEW_LOGIN);
    	clearErrorStyles();
    }
    
	@UiHandler("login")
	void onLogin(ClickEvent evt) {
		loadingIndicator.setVisible(true);
		clearErrorStyles();
		if (credentialsNotEmpty(username, password)) {
			accountService.validate(username.getText(), password.getText(), new AccountCallback());
		} else {
			setError("The fields may not be empty");
			loadingIndicator.setVisible(false);
		}
	}

	/**
	 * Helper to update the user after login or registration.
	 */
	private void updateUser(String username) {
		LoginState.INSTANCE.setLoggedOut(false);
		LoginState.INSTANCE.setUsername(username);
		userLabel.setText(username);
		deckPanel.showWidget(VIEW_WELCOME);
		setHeight("10px");
	}

	@UiHandler("register")
	void onRegister(ClickEvent evt) {
		loadingIndicatorReg.setVisible(true);
		clearErrorStyles();
		if (credentialsNotEmpty(usernameReg, passwordReg, passwordReg2)) {
			//check if the user pw matches in both fields
			if (!passwordReg.getText().equals(passwordReg2.getText())) {
				setError("Your password does not match");
				loadingIndicatorReg.setVisible(false);
				return;
			}

			accountService.create(usernameReg.getText(), passwordReg.getText(), new AccountCallback());
		} else {
			setError("The fields may not be empty");
			loadingIndicatorReg.setVisible(false);
		}
	}
	
	/**
	 * Helper to hide the enclosing dialog and triggering the autoclose callback event.
	 * Walks the DOM tree up by recursion.
	 */
	private void hide(Widget parent) {
		if (parent instanceof PopupPanel) {
			((PopupPanel) parent).hide(true);
		} else {
			hide(parent.getParent());
		}
	}

	/**
	 * Submit the login or registration process if enter key is hit while cursor is inside an input element.
	 * @param evt
	 */
	@UiHandler({"username", "usernameReg", "password", "passwordReg", "passwordReg2"})
	void onEnter(KeyUpEvent evt) {
		if (evt.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			UiHelper.click(deckPanel.getVisibleWidget() == VIEW_LOGIN ? login.getElement() : register.getElement());
	    }
	}

	/**
	 * Check if the login input fields are not empty.
	 * @param textboxes
	 * @return
	 */
	private boolean credentialsNotEmpty(TextBox... textboxes) {
		boolean valid = true;
		for (TextBox box : textboxes) {
			if (box.getText().isEmpty()) {
				//update error label: <which box> may not be empty
				box.addStyleName(ErrorField.ERROR_STYLES);
				valid = false;
			}
		}
		
		return valid;
	}

	/**
	 * Helper to display the error;
	 * @param error
	 */
	private void setError(String error) {
		errorPanel.setText(error);
		errorPanel.setVisible(true);
	}
	
	/**
	 * Helper to clear the error field.
	 */
	private void clearErrorStyles() {
		errorPanel.setText("");
    	errorPanel.setVisible(false);
    	
		username.removeStyleName(ErrorField.ERROR_STYLES);
		usernameReg.removeStyleName(ErrorField.ERROR_STYLES);
		password.removeStyleName(ErrorField.ERROR_STYLES);
		passwordReg.removeStyleName(ErrorField.ERROR_STYLES);
		passwordReg2.removeStyleName(ErrorField.ERROR_STYLES);
	}
}
