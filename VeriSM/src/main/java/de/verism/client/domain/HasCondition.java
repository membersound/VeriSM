package de.verism.client.domain;

import com.google.gwt.user.client.ui.HasText;

import de.verism.client.validation.rules.deletion.RectangleRule;
import de.verism.client.validation.rules.deletion.SignalRule;



/**
 * Provides condition handling for objects that from design point of view have nothing in common beside the condition.
 * Extends {@link HasText} for providing the object context reference for every condition
 * (used in {@link SignalRule} and {@link RectangleRule} during validation).
 * @author Daniel Kotyk
 *
 */
public interface HasCondition extends HasText {
	Condition getCondition();
}
