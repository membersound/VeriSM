package de.verism.client.components.suggestBox;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;

import de.verism.client.components.dialog.editSignal.EditSignalPanel;
import de.verism.client.validation.rules.input.ConditionRule;

/**
 * Creates a text box that can have multiple values, separated by boolean logic.
 * @author Daniel Kotyk
 *
 */
public class MultiValueTextBox extends TextArea {
	
	public static final String WHITESPACE = " ";

	/**
	 * List of keycodes for which keypress should not be canceled.
	 */
	private static final List<Character> SEPARATORS = getSeparators();
	
	/**
	 * Splits the alphabet by any character to generate a comma list of separators,
	 * used to determine first and last occurrences for any of these chars.
	 * 
	 * This is needed to not having to define separators in different classes redundant,
	 * so to make them only depend on the global alphabet defined in {@link EditSignalPanel}.
	 * @return the alphabet chars as a list
	 */
	private static List<Character> getSeparators() {
		List<Character> separators = new ArrayList<Character>();
		//split into single characters
		for (char c : ConditionRule.FULL_SPLIT_ALPHABET.toCharArray()) {
			separators.add(c);
		}
		
		//add chars the are not included in the first full split alphabet,
		//but after which a suggestions should be shown
		separators.add(',');
		separators.add('{');
		separators.add('}');
		return separators;
	}

	/**
	 * Gets the text of the last item after the last comma.
	 * (by default this would return the full text. Now use {@link super.getText()} if full content is needed)
	 * 
	 * It defines the text to be looked up by the {@link SuggestBox}. It has nothing to do with the visual representation.
	 * It is fetched for every change event inside the box. Even for click events.
	 */
	@Override
    public String getText() {
		String fullContent = getFullText();
		 if (fullContent != null && !fullContent.isEmpty()) {
			//gets the text from 0 up to the cursor
			String preCursor = fullContent.substring(0, getCursorPos());
			//get nearest separator before the cursor
			int preCursorSep = lastIndexOfSeparator(preCursor);
			
			if (preCursorSep < getCursorPos()) { //preSeparatorIdx > 0 && 
				//only if separator is on the left side from the cursors point of view,
				//get the string that is currently typed = to be looked up
				fullContent = fullContent.substring(preCursorSep + 1, getCursorPos());
			}
		 }

		 return fullContent;
    }
	
	/**
	 * Returns the last index of the characters defined as boolean separators.
	 * @param content
	 * @return
	 */
	public int lastIndexOfSeparator(String content) {
		int globaleIdx = -1;
		for (Character separator : SEPARATORS) {
			int tempIdx = content.lastIndexOf(separator);
			if (tempIdx > globaleIdx) {
				globaleIdx = tempIdx;
			}
		}
		
		return globaleIdx;
	}
	

	/**
	 * Returns the first index of a separator after the actual cursor position.
	 * @param content
	 * @return
	 */
	public int firstIndexOfSeparator(String content) {
		int globaleIdx = content.length();
		for (Character separator : SEPARATORS) {
			int tempIdx = content.indexOf(separator);
			if (tempIdx >= 0 && tempIdx < globaleIdx) {
				globaleIdx = tempIdx;
			}
		}
		
		return globaleIdx;
	}

	/**
	 * Writes the new selected text into the box. Thereby have to replace what's already been written by the full content string.
	 * This is only executed on selection of a suggest item.
	 */
    @Override
    public void setText(String text) {
        String fullContent = getFullText();
        if (text != null && text.isEmpty()) {
            super.setText(text);
        } else if (fullContent != null) {
    			//gets the text from 0 up to the cursor
    			String preCursorContent = getPreCursorContent();
    			
    			//get last separator before the cursor
    			int preSeparatorIdx = lastIndexOfSeparator(preCursorContent) + 1;
    			preCursorContent = fullContent.substring(0, preSeparatorIdx);
    			
    			//using trims to exactly control the whitespaces around each token
    			String finalContent = preCursorContent.trim() + WHITESPACE + text.trim() + WHITESPACE + getPostCursorContent().trim();
    			super.setText(finalContent);
    	        
    	        //place cursor at the end of the newly inserted entry
    	        //(default would be a GWT bug: at the end of the whole textarea, which is wrong for inline typing).
    	    	//+2 for placing the cusor in the middle of the two whitespaces after the entry.
    			setCursorPos(preCursorContent.length() + text.trim().length() + 1);
    			
    			//fixes focus is lost on mouseclicks, GWT issue #8051
    			setFocus(true);
            }
    }

    /**
     * Helper to get content in front of the cursor position.
     * @return
     */
	public String getPreCursorContent() {
		return getFullText().substring(0, getCursorPos());
	}
	
	/**
	 * Helper to get content after the cursor position.
	 * @return
	 */
    public String getPostCursorContent() {
		return getFullText().substring(getCursorPos());
	}

	/**
     * Removes last typed character. This is necessary if the display suggestion popup does not show anymore.
     * Because: the suggestions are calculated on the user input. Therefore the user has first to put his text in,
     * then the suggestions are calculated and shown eventually if there was a match. So if the string did not match,
     * the popup menu will not show. This indicates an invalid character at the end, which has to be removed again.
     */
	public void removeLastChar() {
		if (getCursorPos() > 0) {
			String fullContent = getFullText();
			String preCursor = fullContent.substring(0, getCursorPos() - 1);
			String postCursor = fullContent.substring(getCursorPos());
			super.setText(preCursor + postCursor);
		}
	}
	
	/**
	 * Returns the full box content as overridden {@link #getText()} now only returns a token.
	 * @return
	 */
	public String getFullText() {
		return super.getText();
	}
	
	/**
	 * Provides accessor to set the content of the box, as {@link #setText(String)} converts the input.
	 * @param text
	 */
	public void overrideText(String text) {
		super.setText(text);
	}
	
	/**
	 * This is used when {@link #setCursorPos()} is called.
	 * But as this method uses {@link #getText()} by default which now only returns the string to be looked up,
	 * it thows exception for content not available in datapool. Because the lookup.length will then always be 0,
	 * resulting the condition below to fail.
	 * 
	 * Resolution is to just use the same code, but modify all calls to {@link super#getText()}. Then the position is set
	 * according to the full and unmodified content in the box.
	 */
	@Override
	public void setSelectionRange(int pos, int length) {
	    // Setting the selection range will not work for unattached elements.
	    if (!isAttached()) {
	      return;
	    }

	    if (length < 0) {
	      throw new IndexOutOfBoundsException(
	          "Length must be a positive integer. Length: " + length);
	    }
	    if (pos < 0 || length + pos > getFullText().length()) {
	    	//never throw exceptions as a wrong selection index is not a serious problem.
	    	//thus just place the selection at the end of the box.
	    	pos = getFullText().length();
	    }
	    getImpl().setSelectionRange(getElement(), pos, length);
	}
}