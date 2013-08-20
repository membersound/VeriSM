package de.verism.client.domain;

import java.io.Serializable;

import com.google.gwt.user.client.ui.HasText;

/**
 * Class combining attributes that are common for any domain objects, and will be wrapped by visual model objects.
 * @author Daniel Kotyk
 *
 */
public abstract class Figure implements Serializable, HasText {
		//the auto-generated name, like "S1"
		protected String name = "";
		
		public Figure() {
			this.name = createUniqueName();
		}
		
		abstract String createUniqueName();

		@Override
		public String getText() { return name; }
		@Override
		public void setText(String text) { this.name = text; }
}
