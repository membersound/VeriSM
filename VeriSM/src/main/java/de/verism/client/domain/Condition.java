package de.verism.client.domain;

import java.io.Serializable;

import com.google.gwt.regexp.shared.SplitResult;

import de.verism.client.domain.data.Query;
import de.verism.client.validation.rules.input.ConditionRule;


/**
 * A condition is composed of boolean expressions.
 * It evaluates in which cases the FSM goes from one state to another based on inputs,
 * or which output will be active based on states and inputs.
 * 
 * Used by {@link Transition} and {@link Signal}.
 * 
 * @author Daniel Kotyk
 *
 */
public class Condition implements Serializable {
	//holds the condition string
	private String value = "";
	

	/**
	 * Converts the id condition to a string condition.
	 * Always returns the resolved condition string without id's.
	 * @return
	 */
	public String getValue() {
		//converts the ids back to the names that is represented by the ids
		return ConditionConverter.convertWith(new Query.ById(), value);
	}
	
	/**
	 * Converts a string condition to id string.
	 * @param value
	 */
	public void setValue(String value) {
		//convert names string back to id string
		this.value = ConditionConverter.convertWith(new Query.ByName(), value);
	}
	
	/**
	 * Converter to exchange id's with names in the condition and vice versa.
	 * @author Daniel Kotyk
	 *
	 */
	private static class ConditionConverter {
		/**
		 * Converts a condition to a string containing only names or id's.
		 * 
		 * @param query the query rule used to took up split tokens
		 * @param condition the full condition string
		 * @return the converted condition string, either to names or id's.
		 */
		static String convertWith(Query.Rule query, String condition) {
			//split the condition string
			SplitResult split = ConditionRule.splitForConversation(condition);

			for (int i = 0; i <= split.length(); i++) {
				String s = split.get(i);

				//prevent null lookups
				if (s == null || s.trim().isEmpty()) {
					continue;
				}

				String result = query.find(s);
				if (result != null) {
					//as the string is split LTR, replacing the first occurrence is always sufficient
					condition = condition.replaceFirst(s, result);
				}
			}
			
			return normalized(condition);
		}

		/**
		 * Normalize the string:
		 * Replace multiple spaces (two or more, skip single one) with single whitespace.
		 * @param text
		 * @return
		 */
		private static String normalized(String text) {
			return text.replaceAll("\\s{2,}", " ");
		}
	}

	/**
	 * Converts the id string to a name string by resolving the id's to their name representations.
	 */
	public void resolveIdToName() {
		this.value = getValue();
	}
	
	/**
	 * Reverts the operation from {@link #resolveIdToName()}. So that the internal value will again consist of only id's.
	 */
	public void revertResolveIdToName() {
		setValue(value);
	}
	
	/**
	 * Returns the conditions' internal representation with object-id references.
	 * Also used for serialization as #getValue() makes use of other objects and does not return the value itself directly.
	 * @return
	 */
	public String getInternalValue() { return value; }
}
