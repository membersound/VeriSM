package de.verism.client.components.suggestBox;

import java.util.List;

import com.google.gwt.user.client.ui.HasText;

import de.verism.client.components.suggestBox.patch.PatchedMultiWordSuggestOracle;

/**
 * The suggestbox for defining conditions.
 * @author Daniel Kotyk
 *
 */
public class ConditionMultiWordSuggestOracle extends PatchedMultiWordSuggestOracle {
	
	/**
	 * Provides a method to add a list of objects that all implement the HasText interface.
	 * Thereby they all provide a specific name that can be queried for lookup in suggestBox.
	 * @param list
	 */
	public void addAll(List<? extends HasText> list) {
		for (HasText hasText : list) {
			super.add(hasText.getText());
		}
	}
}
