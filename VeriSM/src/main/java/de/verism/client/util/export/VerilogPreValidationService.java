package de.verism.client.util.export;

import java.util.ArrayList;
import java.util.List;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.HasCondition;
import de.verism.client.domain.Signal;
import de.verism.client.domain.Transition;
import de.verism.client.domain.data.Cache;
import de.verism.client.validation.ValidationError;
import de.verism.client.validation.Validator;
import de.verism.client.validation.rules.verilog.HasConditionRule;
import de.verism.client.validation.rules.verilog.HasSignalRule;
import de.verism.client.validation.rules.verilog.HasTransitionRule;
import de.verism.client.validation.rules.verilog.InitialStateRule;
import de.verism.client.validation.rules.verilog.IsInputUsedRule;

/**
 * This service validates the application data before verilog code is generated.
 * Every error is logged as separate {@link ValidationError}, that then can be presented to the user.
 * @author Daniel Kotyk
 *
 */
public class VerilogPreValidationService {
	private List<ValidationError> errors = new ArrayList<ValidationError>();
	public  List<ValidationError> getValidationError() { return errors; }

	/**
	 * Validates the data before verilog code generation:
	 * - every object name must be unique and valid
	 * - at least one input and one output signal must be defined
	 * - every transition must have 2 states connected
	 * - every states must have at least one transition (either in or out)
	 * - all outputs must have a condition defined
	 * - there must be an initial states defined
	 * - all inputs must be used either in a transition or an output condition
	 * @return
	 */
	public boolean hasCodeErrors(CanvasArea canvasArea, Cache cache) {
		//prepare project contained data
		List<Rectangle> rectangles = canvasArea.getCanvasDataProdiver().getRectangles();
		List<Transition> transitions = canvasArea.getCanvasDataProdiver().getTransitions();
		List<Signal> inputs = cache.getInputPanel().getData();
		List<Signal> outputs = cache.getOutputPanel().getData();
		
		//validate that there is a initial state defined
		Validator<List<Rectangle>> stateValidator = new Validator<List<Rectangle>>();
		stateValidator.validate(rectangles, new InitialStateRule(), null);
		
		//validate presence of signals
		Validator<List<Signal>> signalValidator = new Validator<List<Signal>>();
		signalValidator.validate(inputs, new HasSignalRule("input"), null);
		signalValidator.validate(outputs, new HasSignalRule("output"), null);
		
		//validate that each input signal is linked to either a transition or an output signal
		List<HasCondition> conditions = new ArrayList<HasCondition>();
		conditions.addAll(transitions);
		conditions.addAll(outputs);
		Validator<Signal> inputsValidator = new Validator<Signal>();
		inputsValidator.add(new IsInputUsedRule(conditions));
		for (Signal signal : inputs) {
			inputsValidator.validate(signal);
		}
		
		//validate that each state has at least one transitions
		Validator<Rectangle> transitionValidator = new Validator<Rectangle>();
		transitionValidator.add(new HasTransitionRule());
		for (Rectangle rectangle : rectangles) {
			transitionValidator.validate(rectangle);
		}
		
		//validate that all conditions have been defined for output signals
		Validator<HasCondition> conditionValidator = new Validator<HasCondition>();
		conditionValidator.add(new HasConditionRule());
		for (Signal signal : outputs) {
			conditionValidator.validate(signal);
		}
		
		//collect the errors from all used validators
		errors.clear();
		errors.addAll(stateValidator.getErrors());
		errors.addAll(signalValidator.getErrors());
		errors.addAll(inputsValidator.getErrors());
		errors.addAll(transitionValidator.getErrors());
		errors.addAll(conditionValidator.getErrors());
		
		return errors.size() > 0;
	}
}
