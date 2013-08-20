package de.verism.client.validation.rules.deletion;

import java.util.List;

import com.google.gwt.regexp.shared.SplitResult;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.domain.HasCondition;
import de.verism.client.util.ClassNameResolver;
import de.verism.client.validation.rules.BaseValidationRule;
import de.verism.client.validation.rules.input.ConditionRule;

/**
 * Base class doing all validation before deleting an object.
 * By using generics in superclass, it can take any object implementing HasTextId.
 * Eg both {@link State} or {Signal}, although they have nothing in common from the class point of view.
 * 
 * @author Daniel Kotyk
 *
 */
public abstract class BaseDeletionRule extends BaseValidationRule<HasTextId> {
	/**
	 * Checks if a name is contained in a condition.
	 * @param value the object to check for delete permission
	 * @param conditions the list for lookup
	 * @return
	 */
	void contains(HasTextId value, List<HasCondition> conditions) {
		boolean first = true;
		
		for (HasCondition hasCondition : conditions) {
			String condition = hasCondition.getCondition().getInternalValue();
			
			SplitResult split = ConditionRule.splitForValidation(condition);
			for (int i = 0; i < split.length(); i++) {
				String result = split.get(i);
				
				//prevent null lookups
				if (result == null || result.trim().isEmpty()) {
					continue;
				}
				
				if (result.equals(value.getId())) {
					
					//first error should add a complete error message, all following errors just be appended as comma separated list
					if (first) {
						first = false;
						addMessage("Could not delete " + ClassNameResolver.getSimpleName(value) + " '" + value.getText()
								+ "' due to dependency with " + ClassNameResolver.getSimpleName(hasCondition) + " '" + hasCondition.getText() + "'");
						continue;
					}
					
					addMessage(ClassNameResolver.getSimpleName(hasCondition) + " '" + hasCondition.getText() + "'");
				}
			}
		}
	}
}
