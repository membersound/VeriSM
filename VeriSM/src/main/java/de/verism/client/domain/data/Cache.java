package de.verism.client.domain.data;

import java.util.List;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.drawing.Viewport;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.components.footer.CanvasFooter;
import de.verism.client.components.panels.io.spreadsheet.Spreadsheet;

/**
 * Provides all data that is used on the client side: inputs, outputs, states, transistions etc.
 * In general apps this would be a DB-Manager querying the DB. But as this app is client side only without server data,
 * the DataPool is hold inside this class.
 * 
 * @author Daniel Kotyk
 *
 */
public class Cache {
	//prevents external instantiation
	private Cache() {}
	private static Cache INSTANCE = new Cache();
	
	
	/**
	 * Helper for obtaining an instance of this renderer. Thread-safe singleton.
	 * @return
	 */
	public static Cache get() {
		return INSTANCE;
	}
	
	//the panels holding input and output signals
	private Spreadsheet inputPanel, outputPanel;
	
	//header containing all available canvas screens and the project name
	private CanvasFooter canvasFooter;
	
	/**
	 * Cleans the data cache on the client side before loading a new project.
	 */
	public void cleanup() {
		canvasFooter.reset();
		inputPanel.reset();
		outputPanel.reset();
		CanvasArea.get().reset();
		
		inputPanel.refresh();
		outputPanel.refresh();
		CanvasArea.get().refresh();
		Viewport.reset();
	}
	
	public List<Drawable> getDrawables() {
		return getCanvasData().getDrawables();
	}
	
	public CanvasDataProvider getCanvasData() {
		return CanvasArea.get().getCanvasDataProdiver();
	}
	
	public Spreadsheet getInputPanel() { return inputPanel; }
	public void setInputPanel(Spreadsheet inputPanel) { this.inputPanel = inputPanel; }
	public Spreadsheet getOutputPanel() { return outputPanel; }
	public void setOutputPanel(Spreadsheet outputPanel) { this.outputPanel = outputPanel; }
	public CanvasFooter getCanvasFooter() { return canvasFooter; }
	public void setCanvasFooter(CanvasFooter canvasFooter) { this.canvasFooter = canvasFooter; }
}
