package de.verism.client.domain;

import java.io.Serializable;
import java.util.List;

import de.verism.client.canvas.shapes.Drawable;

/**
 * Data Transfer Object for all drawable elements in the canvas.
 * This object is to be serialized by the server and send back as downloadable file to the client.
 * 
 * Important: this file should be in /shared folder by convention, but due to a GWT bug it's only working in client folder atm.
 * @author Daniel Kotyk
 *
 */
public class JsonDTO implements Serializable { 
	//list of drawing elements to be serialized
    private List<Drawable> drawables;
    
    //input and output vectors
    private List<Signal> inputs, outputs;
	
    private String projectName;
    
    /**
     * Emptry constructor for serialization.
     */
    public JsonDTO() {}
    
    public List<Drawable> getDrawables() { return drawables; }
    public void setDrawables(List<Drawable> drawables) { this.drawables = drawables; }
	public List<Signal> getInputs() { return inputs; }
	public void setInputs(List<Signal> inputs) { this.inputs = inputs; }
	public List<Signal> getOutputs() { return outputs; }
	public void setOutputs(List<Signal> outputs) { this.outputs = outputs; }
	public String getProjectName() { return projectName; }
	public void setProjectName(String projectName) { this.projectName = projectName; }
}
