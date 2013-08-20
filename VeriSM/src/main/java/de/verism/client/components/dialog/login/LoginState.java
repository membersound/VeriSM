package de.verism.client.components.dialog.login;


/**
 * Maintains the user login state.
 * @author Daniel Kotyk
 *
 */
public class LoginState {

		private LoginState() {};
		public static LoginState INSTANCE = new LoginState();
		
		//the username for the session
		private String username;
		private boolean loggedOut = true;
		
		public void setUsername(String username) { this.username = username; }
		public String getUsername() { return username; }
		public boolean isLoggedOut() { return loggedOut; }
		public void setLoggedOut(boolean loggedOut) { this.loggedOut = loggedOut; }
}
