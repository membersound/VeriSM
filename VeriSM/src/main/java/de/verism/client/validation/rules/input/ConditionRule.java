package de.verism.client.validation.rules.input;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;

import de.verism.client.domain.data.Query.SuggestionStrategy;
import de.verism.client.validation.rules.BaseValidationRule;

/**
 * Strategy to validate the condition field.
 * @author Daniel Kotyk
 *
 */
public class ConditionRule extends BaseValidationRule<String> {
	//alphabet to define the character set that is used to split the string, excluding the '=' char as this is special case
	private static final String SPLIT_ALPHABET = "|&()!~^<>";
	public static final String FULL_SPLIT_ALPHABET = SPLIT_ALPHABET + "=";
	
	//the regexpressions wrapped in brackets
	private static final String REGEX_ALPHABET = "[\\" + FULL_SPLIT_ALPHABET + "]";
	
	//plus defining a whitespace (\\s) also as split char
	private static final String FULL_REGEX_ALPHABET = "[\\" + FULL_SPLIT_ALPHABET + "\\s]";
	
	//matches anything between two brackets, eg [15:0], made optional with the "?".
	//important for vector definitions like "c[15:0]" so that only "c" is extracted.
	private static final String VECTOR_REGEX = "\\[(.*?)\\]";
	
	//match anything between two curly brackets, so that constructs like "{3{sig0,sig1}}" are possible
	private static final String REGEX_CURLY = "(?:{.*?)?{|}}?";
	
	//separator of states in curly brackets
	private final static String DELIMITER = ",";
	
	//regex used for conversation only (resolving id's to strings and vice versa). simply captures the boolean groups.
	private static final String REGEX_CONVERT = 
			"(" + VECTOR_REGEX + ")"				//match anything inside the vector definition "[...]"
			+ "|" + FULL_REGEX_ALPHABET + "+";		//or any split alphabet character groups, like "&(("
	
	//regex for validating the conditions the user typed in.
	//more complex than REGEX_CONVERT as assignments may contain numeric values ("crc == 32'hffffffff"), which must be skipped for validation.
	private static final String REGEX_SPLIT = 
			"(" + FULL_REGEX_ALPHABET + "+)?" 		//optional split chars in front of a "=" sign. the question mark "+?" changes matching from greedy to lazy, which means the chars are optionally instead of mandatory
			+ "[!=<>]+?" 							//if one or more of these characters occur (this matches assignments like x < 0, x > 0, x <= 0, x == 0 etc.)
			+ ".*?"									//match any character behind a "="...
			+ "(?:" + REGEX_ALPHABET + "|$)+" 		//...up to the last occurrence of the split alphabet, or end of line (\z in JAVA, $ in JS), w/o whitespace as assignments may contain some ("== 1'b0").
			+ "|" + REGEX_CONVERT;					//or (if no "=" char) apply the normal conversation matcher

	//the query used to look up valid expressions
	private SuggestionStrategy query;
	
	/**
	 * Must provide a query strategy for the condition rule to validate.
	 * @param query
	 */
	public ConditionRule(SuggestionStrategy query) {
		this.query = query;
	}

	/**
	 * Validate the full textarea content by splitting it at the booleanChars and verifying each split token.
	 * @param text the full text to be split
	 * @return if valid
	 */
	@Override
	public void validate(String value) {
		boolean first = true;
		SplitResult split = splitForValidation(value);

		for (int i = 0; i <= split.length(); i++) {
			String s = split.get(i);

			//allow epsilon transitions (empty)
			if (s == null || s.trim().isEmpty()) { continue; }
			
			//analyse the transition
			if (!isTokenValid(s.trim())) {
				if (first) {
					first = false;
					addMessage("Some condition values are invalid: ");
				}
				addMessage(s.trim());
			}
		}
	}
	
	/**
	 * Split for user input validation.
	 * @param value
	 * @return
	 */
	public static SplitResult splitForValidation(String value) {
		return split(REGEX_SPLIT, value);
	}
	
	/**
	 * Split for conversation between strings and id's.
	 * @param value
	 * @return
	 */
	public static SplitResult splitForConversation(String value) {
		return split(REGEX_CONVERT, value);
	}
	
	/**
	 * Helper for the splitting.
	 * Extract the elements of the condition as an array of input signals and/or states.
	 * @return
	 */
	private static SplitResult split(String regex, String value) {
		//replace everything not being a signal using the regex, and separate it with the #DELIMITER
		RegExp regExp = RegExp.compile(regex, "g");
		value = regExp.replace(value, DELIMITER);
		
		//2nd split for the curly expressions
		regExp = RegExp.compile(REGEX_CURLY, "g");
		value = regExp.replace(value, DELIMITER);
		
		//split the condition at the #DELIMITER and verify each token is an existing input signal or state
		regExp = RegExp.compile("\\" + DELIMITER);
		return regExp.split(value);
	}
	
	/**
	 * Validates a single token entry against the data pool.
	 * @param token the token to match
	 */
	private boolean isTokenValid(final String token) {
		try {
			return Iterables.find(query.getSuggestions(), new Predicate<HasText>() {
				/**
				 * As the tokens will represent variable names later, and variable names are case insensitive,
				 * compare is applied with lowerCase().
				 * @param text
				 * @return
				 */
				@Override
				public boolean apply(@Nullable HasText text) {
					return text.getText().toLowerCase().equals(token.toLowerCase());
				}
			}) != null;
		} catch (Exception e) {
			//catch eg. {@link NoSuchElementException} that come from classes of Google Guava framework
			return false;
		}
	}
}
