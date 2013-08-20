package de.verism.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.JsonDTO;
import de.verism.client.domain.data.Cache;
import de.verism.client.domain.data.CanvasDataProvider;
import de.verism.client.util.export.JsonBuilder;

/**
 * Callback for RPC servlet deserialization.
 * Receives the {@link JsonDTO} wrapper based on deserialization of on a project file.
 * @author Daniel Kotyk
 *
 */
public class JSONCallback implements AsyncCallback<JsonDTO> {
	   private DialogBox dialog;
	
	   public JSONCallback(DialogBox dialog) {
			this.dialog = dialog;
	   }

	   @Override
	   public void onFailure(Throwable caught) {
		   new NotifierPopup("Project loading failed with: " + caught.getMessage(), NotifierState.ERROR);
	   }

	   @Override
	   public void onSuccess(JsonDTO jsonDTO) {
		    if (jsonDTO == null) {
			    return;
		    }
		   
		    Cache cache = Cache.get();
		    cache.cleanup();

			//obtain main data controller
    		CanvasDataProvider canvas = Cache.get().getCanvasData();
    		
    		//populate the file content into the canvas
    		canvas.setDrawables(jsonDTO.getDrawables());
    		
    		//populate inputs + outputs
    		cache.getInputPanel().setData(jsonDTO.getInputs());
    		cache.getOutputPanel().setData(jsonDTO.getOutputs());
    		cache.getCanvasFooter().setProjectName(jsonDTO.getProjectName());
    		
		   	JsonBuilder jsonBuilder = new JsonBuilder();
		   	jsonBuilder.genClientSideIds(jsonDTO);
    		jsonBuilder.conditionsToId(cache);

    		//move the initial state to the center of the canvas
    		boolean centered = false;

			//the center of the canvas
			int height = CanvasArea.get().getCanvas().getOffsetHeight() / 2;
			int width = CanvasArea.get().getCanvas().getOffsetHeight() / 2;
    		
    		for (Rectangle rect : cache.getCanvasData().getRectangles()) {
    			if (rect.getFigure().isInitial()) {
    				//the initial state point
    				int x = - (int) rect.getPosStart().getX();
    				int y = - (int) rect.getPosStart().getY();
    				
    				//move 
    				CanvasArea.get().getStateViewport().updateViewport(x + width, y + height);
    				centered = true;
    				break;
    			}
    		}
    		//if no initial state has been defined by the user, center to the first state
    		if (!centered && !cache.getCanvasData().getRectangles().isEmpty()) {
    			Rectangle rect = cache.getCanvasData().getRectangles().get(0);
				int x = - (int) rect.getPosStart().getX();
				int y = - (int) rect.getPosStart().getY();
    			CanvasArea.get().getStateViewport().updateViewport(x + height, y + width);
    		}
    		
			//draw content when all data is deserialized
    		CanvasArea.get().refresh();
	        dialog.hide();
	        new NotifierPopup("Project import successfull", NotifierState.SUCCESS);
	   }	   
}
