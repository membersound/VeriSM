package de.verism.client.domain;

import de.verism.client.util.UniqueName;

/**
 * The transition domain object.
 * @author Daniel Kotyk
 *
 */
public class Transition extends Figure implements HasCondition {
	private Condition condition = new Condition();
	
	private Transition() {}
	
	/**
	 * States are only required for unique name creation depending on these states.
	 * @param from
	 * @param to
	 */
	public Transition(State from, State to, boolean showText) {
		this.name = createUniqueName(from.getText(), to.getText());
		this.showText = showText;
	}

	@Override
	public Condition getCondition() { return condition; }
	public void setCondition(Condition condition) { this.condition = condition; }


	@Override
	String createUniqueName() {
		return createUniqueName("", "");
	}

	/**
	 * Overloaded unique name creation. Transition name should give a hint to transitions that it connects.
	 * As statenames are always unique, this ensures that transitions names are also always unique.
	 * @param from
	 * @param to
	 * @return
	 */
	private String createUniqueName(String from, String to) {
		return UniqueName.forTransition(from, to);
	}
	
	//text should initially always be invisible for transitions
	private boolean showText = false;
	
	public boolean isShowText() { return showText; }
	public void setShowText(boolean value) { this.showText = value; }
}
