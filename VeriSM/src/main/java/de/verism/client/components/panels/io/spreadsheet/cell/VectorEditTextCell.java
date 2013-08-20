package de.verism.client.components.panels.io.spreadsheet.cell;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.components.panels.io.spreadsheet.cell.render.CellStrategy;

/**
  * Custom cell for modifying the {@link EditTextCell} styles.
  * @author Daniel Kotyk
  *
  */
public class VectorEditTextCell extends EditTextCell {
	public static final int DEFAULT_LINE_HEIGHT = 16;
	private String textAlign = "left";
	
	//style of an editable input vector field in the spreadsheet
	private static final String DASHED_BORDER = "border: " + Drawable.DEFAULT_BORDER_SIZE + "px dashed "+ 
			Drawable.BORDER_COLOR + " !important;" +
			"border-radius: 3px;" + "-moz-border-radius: 3px;";

	private CellStrategy cellStrategy;

	public VectorEditTextCell(CellStrategy cellStrategy) {
		this.cellStrategy = cellStrategy;
	}
	
	/**
     * Style the input field element for indicating active input: text changes to 'italic'.
     */
	@Override
    protected void edit(Context context, Element parent, String value) {
         super.edit(context, parent, value);
         InputElement input = parent.getFirstChild().<InputElement> cast();
         input.setAttribute("style", //"display: inline-block;" +
        		 "text-align: " + textAlign + ";" +
        		 "width: 100% !important; " + //important for not typing hidden text out of the input
        		 "border: 0px !important;" + //prevent inline border of input field
        		 "margin: 0px !important; padding: 0px !important;" + 
        		 "background: inherit;" +
        		 "font-family: Verdana, Geneva, sans-serif !important; " + 
        		 "height: " + DEFAULT_LINE_HEIGHT + "px !important;" + //fixes cell resize on edit and text cutoff
        		 "line-height: " + DEFAULT_LINE_HEIGHT + "px !important;" + 
        		 "font-style: italic !important;"); //kursiv

         cellStrategy.setTooltip(input);
     }
	
     /**
      * Style the table cell wrapper around the input field: border changes to 'dashed'.
      */
     @Override
     public boolean isEditing(Context context, Element parent, String value) {
         boolean isEditing = super.isEditing(context, parent, value);
         
          if (isEditing) {
        	  //dashed border with round corners
              parent.getParentElement().setAttribute("style", DASHED_BORDER);
          } else {
              parent.getParentElement().removeAttribute("style");
          }
          
          return isEditing;
      }

     /**
      * Restrict key characters to numbers for the vector dimension (bits) input field.
      */
    @Override
    public void onBrowserEvent(Context context,
    		Element parent, String value, NativeEvent event,
    		ValueUpdater<String> valueUpdater) {
    	super.onBrowserEvent(context, parent, value, event, valueUpdater);

    	//prevent all keys but numbers for the bits input field
    	//isEditing() ensures these checks to only be applied in cell edit mode
    	if (isEditing(context, parent, value)) {
    		int key = event.getKeyCode();
    		
    		if (cellStrategy.isPreventKey(key)) {
    			event.preventDefault();
    			return;
    		}
    	}
    }

	public CellStrategy getCellStrategy() { return cellStrategy; }

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}
}