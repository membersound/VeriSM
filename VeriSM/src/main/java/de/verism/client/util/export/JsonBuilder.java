package de.verism.client.util.export;

import java.util.ArrayList;
import java.util.List;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.domain.JsonDTO;
import de.verism.client.domain.Signal;
import de.verism.client.domain.Transition;
import de.verism.client.domain.data.Cache;

/**
 * Handles the creation of the json data transfer object,
 * and also takes care that application data is not changed.
 * @author Daniel Kotyk
 *
 */
public class JsonBuilder {

	/**
	 * Prepares the json data transfer object and fills it with all data contained in the app on the client side.
	 * The DTO is used to transfer the data to the server, that will create a json serialized file from it.
	 * 
	 * @param cache the clientside appdata cache to get the objects from
	 * @return the json DTO
	 */
	public JsonDTO prepareJsonDTO(Cache cache) {
		//the data transfer object for serialization send to the server
		JsonDTO jsonDTO = new JsonDTO();
		jsonDTO.setProjectName(cache.getCanvasFooter().getProjectName());
		
		//create the content to save the current canvas state
		List<Drawable> figures = Cache.get().getCanvasData().getDrawables();
		jsonDTO.setDrawables(figures);

		//as the dataProvider of the spreadsheet wraps the list in a private ListWrapper which does not 
		//implement serializable, the list has to be copied to a new list before export.
		List<Signal> inputs = cache.getInputPanel().getData();
		List<Signal> outputs = cache.getOutputPanel().getData();

		//use shallow copy of all objects for further conversation
		jsonDTO.setInputs(new ArrayList<Signal>(inputs));
		jsonDTO.setOutputs(new ArrayList<Signal>(outputs));

		//convert the condition with id's back to the UI string as id's are never exported for serialization
		//convert from id to string, eg: 12 & 13 | 14 will be: state1 & state2 | stateX
		for (Signal signal : outputs) {
			signal.getCondition().resolveIdToName();
		}
		for (Drawable drawable : figures) {
			if (drawable instanceof Line) {
				Transition transition = ((Line) drawable).getFigure();
				transition.getCondition().resolveIdToName();
			}
		}
		
		return jsonDTO;
	}


	/**
	 * Revert id changes the may have been done to the conditions during jsonDTO preparation.
	 * Also used during serialisation, where conditions look like "state0 & input1", but the
	 * application needs their id representation like "gwt-15 & gwt-17".
	 * 
	 * @param cache the clientside appdata cache to get the objects from
	 */
	public void conditionsToId(Cache cache) {
		List<Signal> outputs = cache.getOutputPanel().getData();
		List<Drawable> figures = cache.getDrawables();
		
		//revert the id conversation so that the app data is not changed.
		//it has to be done this way as GWT client side cannot make deep copy of list elements,
		//thus it's not possible to convert the conditions on deep copies of the signals.
		//instead the serialization has to work on the original object, and revert it afterwars as follows.
		for (Signal signal : outputs){
			signal.getCondition().revertResolveIdToName();
		}
		for (Drawable drawable : figures) {
			if (drawable instanceof Line) {
				Transition transition = ((Line) drawable).getFigure();
				transition.getCondition().revertResolveIdToName();
			}
		}
	}
	
	/**
	 * Generates all object id's to identify the object on the client side.
	 * @param jsonDTO
	 */
	public void genClientSideIds(JsonDTO jsonDTO) {
		//generate UUIDs on all signals for spreadsheet identification
	    //must be done on the client side, as DOM cannot be accessed serverside.
	    //order is not important.
		for (Signal signal : jsonDTO.getInputs()) {
			signal.generateId();
		}
		for (Drawable drawable : jsonDTO.getDrawables()) {
			drawable.generateId();
		}
		for (Signal signal : jsonDTO.getOutputs()) {
			signal.generateId();
		}
	}
}
