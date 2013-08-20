package de.verism.client.util.export;

import java.util.List;

import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.domain.Signal;
import de.verism.client.domain.State;
import de.verism.client.domain.data.Cache;

/**
 * Service to create verilog codes.
 *
 * regs don't need a dimension as we use 1hot encoding (limitation of this app). one hot = only one bit is high at a time, the rest are low.
 * @author Daniel Kotyk
 *
 */
public class VerilogCodeService {
	//avoid magic numbers in the code, used for onehot bit assignment
	private static final int HIGH = 1;
	private static final int LOW = 0;

	static final String SEPARATOR =",";
	private static final String WHITESPACE = " ";
	private static final String TAB = "\t";
	private static final String ASSIGNMENT = "<= 1'b";
	
	private Cache cache;
	
	public VerilogCodeService(Cache dataPool) {
		this.cache = dataPool;
	}
	
	/**
	 * Creates the verilog code.
	 * @return
	 */
	public String createCode() {
		//VerilogBuilder is threadsafe, thus would cause a little performance overhead.
		//as the app is single threaded, it is better to use VerilogBuilder here.
		VerilogBuilder result = new VerilogBuilder();
		
		//prepare verilog blocks
		String module = prepareModule();
		String params = prepareParams();
		String transitions = prepareTransitions();
		String states = prepareStates();
		String outputs = prepareOutputs();
		
		//append prepared blocks
		result.append(module)
			  .append(params)
			  .append(transitions)
			  .append(states)
			  .append(outputs)
			  .append("endmodule");
		
		return result.toString();
	}

	/**
	 * Prepares the module definitions.
	 * @return
	 */
	private String prepareModule() {
		String inputs = preparePorts(cache.getInputPanel().getData(), "input");
		String outputs = preparePorts(cache.getOutputPanel().getData(), "output reg");
		String projectName = Cache.get().getCanvasFooter().getProjectName();
		
		VerilogBuilder buffer = new VerilogBuilder();
		buffer.append("module " + projectName.replaceAll("\\s+", "_") + " (")
			  .increateTab()
			  .append("input clk" + SEPARATOR)
		      .append("input rst" + SEPARATOR)
		      .decreaseTab()
		      .appendNoTab(inputs)
		      .appendNoTab(outputs);
		
		//remove last separator as the last port may not end with a separator
		String module = buffer.cutoff(VerilogCodeService.SEPARATOR)
							  .decreaseTab()
							  .newLine()
							  .append(");")
							  .newLine()
							  .toString();

		return module;
	}
	
	/**
	 * Prepares the input and output ports.
	 * @param signals
	 * @param varSuffix
	 * @return
	 */
	private String preparePorts(List<Signal> signals, String varSuffix) {
		VerilogBuilder buffer = new VerilogBuilder();
		buffer.increateTab();
		
		for (Signal signal : signals) {
			buffer.append(varSuffix + WHITESPACE + signal.toString() + SEPARATOR);
		}
		
		return buffer.toString();
	}


	/**
	 * Prepare states and transitions as simple regs 1hot.
	 * @return
	 */
	private String prepareParams() {
		List<Rectangle> rectangles = Cache.get().getCanvasData().getRectangles();
		List<Line> lines = Cache.get().getCanvasData().getLines();
		
		VerilogBuilder buffer = new VerilogBuilder();
		
		for (Rectangle rectangle : rectangles) {
			buffer.append("reg " + rectangle.getText() + ";");
		}
		buffer.newLine();
		for (Line line : lines) {
			buffer.append("reg " + line.getText() + ";");
		}
		buffer.newLine();
		
		return buffer.toString();
	}
	
	/**
	 * Creates the condition assignments with the user-given condition.
	 * @return
	 */
	private String prepareTransitions() {
		VerilogBuilder buffer = new VerilogBuilder();
		String condition;
		
		List<Line> lines = Cache.get().getCanvasData().getLines();
		for (Line line : lines) {
			buffer.append("always @* begin")
			  .increateTab()
			  .appendNoBreak(line.getFigure().getText() + " = " + line.connectedFrom().getText());
			
			//only write the condition if it has been defined, as epsilon_transitions are allowed
			condition = line.getFigure().getCondition().getValue().trim();
			if (condition.isEmpty()) {
				buffer.newLine();
			} else {
				buffer.append("& (" + condition + ");");
			}
			
			//close the assignment
			buffer.decreaseTab()
			  .append("end")
			  .newLine();
		}
		
		return buffer.toString();
	}
	
	/**
	 * Prepares state transitions based on conditional statements.
	 * @return
	 */
	private String prepareStates() {
		List<Rectangle> rectangles = Cache.get().getCanvasData().getRectangles();
		
		VerilogBuilder buffer = new VerilogBuilder();

		for (Rectangle rectangle : rectangles) {
			State state = rectangle.getFigure();
			
			//identify the rest state that is to be = 1'b1 on reset
			int bit = state.isInitial() ? HIGH : LOW;
			
			buffer.append("always @(posedge clk or posedge rst) begin")
			  .increateTab()
			  .append("if (rst)" + TAB + TAB + TAB + state.getText() + " <= 1'b" + bit + ";");
			
			for (Line line : rectangle.getConnections()) {
				int oneHotBit = line.connectedTo().equals(rectangle) ? HIGH : LOW;
				buffer.append("else if (" + line.getFigure().getText() +  ")" + TAB + state.getText() + " " + ASSIGNMENT + oneHotBit + ";");
			}
			
			buffer.decreaseTab()
				.append("end")
				.newLine();
		}
		
		return buffer.toString();
	}
	
	/**
	 * Prepares the outputs based on conditions on inputs and states.
	 * @return
	 */
	private String prepareOutputs() {
		VerilogBuilder buffer = new VerilogBuilder();

		//combinatorial output
		buffer.append("always @* begin")
			  .increateTab();
		
		for (Signal signal : cache.getOutputPanel().getData()) {
			buffer.append(signal.getText() + " = " + signal.getCondition().getValue() + ";");
		}
		
		buffer.decreaseTab()
			  .append("end")
			  .newLine();
		
		return buffer.toString();
	}
}
