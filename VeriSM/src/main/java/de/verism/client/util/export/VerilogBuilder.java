package de.verism.client.util.export;



/**
 * Builder Pattern class to create chains of code for the verilog output.
 * Main target of this class is to make the code generation clearer and better to maintain.
 * This is achieved by wrapping system characters like tabs and linefeeds in methods.
 * 
 * Further the default {@link #append(String)} method will also take care of prepending tabs
 * and auto linebreaks (for pretty code that is maintainable by hand).
 * 
 * @author Daniel Kotyk
 *
 */
public class VerilogBuilder {
	//no public access to the builder
	private StringBuilder builder = new StringBuilder();
	
	//line feed + carriage return
	public static final String LF = "\r\n";
	//tabulator by whitespaces, as system tab width would be too much to produce pretty code
	private static final String TAB = "  ";
	private int tab = 0;
	
	/**
	 * Increases the prepended tab size 1.
	 * Returns the builder itself to provide chaining.
	 * @return
	 */
	VerilogBuilder increateTab() {
		tab++;
		return this;
	}
	
	/**
	 * Decreses the prepended tab size by 1.
	 * Returns the builder itself to provide chaining.
	 * @return
	 */
	VerilogBuilder decreaseTab() {
		tab--;
		return this;
	}
	
	VerilogBuilder newLine() {
		builder.append(LF);
		return this;
	}
	
	/**
	 * Appends a value without adding a linebreak. Non-standard behavior.
	 * @param value
	 */
	public VerilogBuilder appendNoBreak(String value) {
		for (int i = 0; i < tab; i++) {
			builder.append(TAB);
		}
		builder.append(value);
	    
	    return this;
	}
	
	/**
	 * Appends a value without prepended tabs. Non-standard behavior.
	 * @param value
	 * @return
	 */
	public VerilogBuilder appendNoTab(String value) {
		builder.append(value);
		newLine();
		return this;
	}
	
	/**
	 * Appends a value to the builder, prepended by the number of tabs, appended by a linebreak. Standard-behavior.
	 * @param value the value to append
	 * @return the builder itself to make it chainable
	 */
	VerilogBuilder append(String value) {
		appendNoBreak(value);
	    newLine();
	    return this;
	}
	
	/**
	 * Cuts the end of the buffer be given number.
	 * @return
	 */
	public VerilogBuilder cutoff(String stream) {
		//cutting a buffer always returns a string.
		//for maintaining chains, this has to be wrapped in a new builder object.
		String tmp = builder.substring(0, builder.lastIndexOf(stream));
		builder = new StringBuilder().append(tmp);
		return this;
	}
	 
	/**
	 * Only accessor to the internal {@link #builder}.
	 */
	@Override
	public String toString() {
		return builder.toString();
	}
}