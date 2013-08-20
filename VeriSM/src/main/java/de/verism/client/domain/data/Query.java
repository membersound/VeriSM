package de.verism.client.domain.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.HasText;

import de.verism.client.canvas.shapes.HasTextId;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.components.suggestBox.AutoSuggestBox;
import de.verism.client.domain.HasCondition;
import de.verism.client.domain.State;
import de.verism.client.domain.Transition;

/**
 * Simulates queries that would be normally executed against a db.
 * Here it makes use of the {@link Cache} where all data is stored on the client side.
 * @author Daniel Kotyk
 *
 */
public class Query {

	/**
	 * Gets a key-value mapped list where keys are the names that are later converted to variable names on verilog export.
	 * Thus this can be used to validate duplicate name entries.
	 * @return
	 */
	public static ImmutableMap<String, ? extends HasText> getAllNames() {
		return mapTextToKey(getAllData());
	}
	
	/**
	 * Query strategy how the split string should be looked up.
	 * @author Daniel Kotyk
	 *
	 */
	public interface Rule {
		String find(String query);
	}
	
	/**
	 * Finds an object by id.
	 * Used to replace the condition string with id's and vice versa.
	 * @author Daniel Kotyk
	 *
	 */
	public static class ById implements Rule {

		@Override
		public String find(String id) {
			HasTextId result = mapIdToKey(getPartialSuggestions()).get(id);
			return result != null ? result.getText() : null;
		}
	}
	
	/**
	 * Finds an object by name.
	 * @author Daniel Kotyk
	 *
	 */
	public static class ByName implements Rule {
		@Override
		public String find(String name) {
			//lowerCase() as variable names are case insensitive
			HasText result = mapTextToKey(getPartialSuggestions()).get(name.toLowerCase());
			return result != null ? ((HasTextId) result).getId() : null;
		}
	}
	
	/**
	 * Finds an object name only within the states.
	 * Used for verilog code generation.
	 * @author Daniel Kotyk
	 *
	 */
	public static class ByState implements Rule {

		@Override
		public String find(String name) {
			List<State> states = Cache.get().getCanvasData().getStates();
			HasText result = mapTextToKey(states).get(name.toLowerCase());
			//returns the same value is the input was, and if matched a state prepended with the state assertion
			return result != null ? "(state == " + result.getText() + ")" : name;
		}
		
	}
	
	/**
	 * Helper to get a list mapped to string keys consisting of what is returned by #getText() of the contained objects.
	 * @param list
	 * @return
	 */
	private static ImmutableMap<String, ? extends HasText> mapTextToKey(List<? extends HasText> list) {
		return Maps.uniqueIndex(list, new Function<HasText, String>() {
			public String apply(HasText object) {
				//always base variable naming on case insensitive values
				return object.getText().toLowerCase();
			}
		});
	}
	
	/**
	 * Maps all id's as keys to a list.
	 * @param list
	 * @return
	 */
	private static ImmutableMap<String, ? extends HasTextId> mapIdToKey(List<? extends HasTextId> list) {
		return Maps.uniqueIndex(list, new Function<HasTextId, String>() {
			public String apply(HasTextId object) {
				return object.getId();
			}
		});
	}

	/**
	 * Returns all user data input (signals, states, transitions etc).
	 * Used for checking if key name is unique.
	 * @return
	 */
	private static List<HasTextId> getAllData() {
		List<HasTextId> data = new ArrayList<HasTextId>();
		
		try {
			data.addAll(getPartialSuggestions());
			data.addAll(Cache.get().getOutputPanel().getData());
			data.addAll(Cache.get().getCanvasData().getLines());
		} catch (NullPointerException e) {
			//for an empty SM, no initializations have been done within the canvas, which will throw NPE then.
			//this try-catch prevents the exception and lets the export job continue, as exporting/saving an 
			//empty project is a totally viable usecase.
		}
		return data;
	}
	
	/**
	 * Returns the suggestions consisting only of inputs and states.
	 * Used for conversation between names and id's for the condition strings.
	 * @return
	 */
	public static List<HasTextId> getPartialSuggestions() throws NullPointerException {
		List<HasTextId> suggestions = new ArrayList<HasTextId>();
		suggestions.addAll(Cache.get().getInputPanel().getData());
		suggestions.addAll(Cache.get().getCanvasData().getRectangles());
		return suggestions; 
	}
	
	/**
	 * A strategy to provide the different suggestions for {@link AutoSuggestBox}.
	 * @author Daniel Kotyk
	 *
	 */
	public interface SuggestionStrategy {
		List<HasTextId> getSuggestions();
	}
	
	//static accessors for the private classes providing singletons
	public static SuggestionStrategy TRANSITION = new TransitionSuggest();
	public static SuggestionStrategy SIGNAL = new OutputSignalSuggest();
	
	/**
	 * Transitions should only depend in input signals.
	 * @author Daniel Kotyk
	 *
	 */
	private static class TransitionSuggest implements SuggestionStrategy {
		@Override
		public List<HasTextId> getSuggestions() {
			List<HasTextId> suggestions = new ArrayList<HasTextId>();
			suggestions.addAll(Cache.get().getInputPanel().getData());
			return suggestions; 
		}
	}
	
	/**
	 * Output signals may depend on both input signals and states.
	 * @author Daniel Kotyk
	 *
	 */
	private static class OutputSignalSuggest implements SuggestionStrategy {
		@Override
		public List<HasTextId> getSuggestions() {
			return getPartialSuggestions();
		}
	}
	
	//-------- conditions ----------- //

	/**
	 * Returns all objects that have a condition: transitions + output signals.
	 * @return
	 */
	public static List<HasCondition> getAllConditions() {
		List<HasCondition> conditions = new ArrayList<HasCondition>();
		conditions.addAll(Cache.get().getOutputPanel().getData());
		//get all transitions from all lines
		conditions.addAll(getLines());
		return conditions;
	}
	
	/**
	 * Returns only the output signal conditions.
	 * @return
	 */
	public static List<HasCondition> getPartialConditions() {
		List<HasCondition> conditions = new ArrayList<HasCondition>();
		conditions.addAll(Cache.get().getOutputPanel().getData());
		return conditions;
	}
	
	/**
	 * Returns a list of all transitions in the application.
	 * @return
	 */
	private static List<Transition> getLines() {
		List<Transition> transitions = new ArrayList<Transition>();
		for (Line line : Cache.get().getCanvasData().getLines()) {
			transitions.add(line.getFigure());
		}
		
		return transitions;
	}

}
