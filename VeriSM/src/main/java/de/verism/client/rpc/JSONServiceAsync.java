/**
 * 
 */
package de.verism.client.rpc;

import java.io.IOException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.domain.JsonDTO;
import de.verism.server.json.JSONServiceImpl;

/**
 * Client-side RPC service for (de)-serialization of canvas shapes.
 * Provides ability to call the server-slide RPC service implementation {@link JSONServiceImpl}.
 * @author Daniel Kotyk
 * @generated generated asynchronous callback interface to be used on the client side
 *
 */
public interface JSONServiceAsync {

	/**
	 * Extract all {@link Drawable} out of the project file
	 * @param key the file identifier for the uploaded content stored on the server
	 * @param  callback the callback that will be called to receive the return value
	 * @return
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void deserializeFromJson(Long key, AsyncCallback<JsonDTO> callback);

	/**
	 * Generate the project file representing all {@link Drawable} as JSON strings.
	 * @param jsonDTO
	 * @param  callback the callback that will be called to receive the return value (see <code>@gwt.callbackReturn</code> tag)
	 * @gwt.callbackReturn the key to identify the file, to be placed into servlet query.
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void serializeToJson(JsonDTO jsonDTO, AsyncCallback<Long> callback);

	/**
	 * Export the canvas content to picture.
	 * @param content the image stream
	 * @param  callback the callback that will be called to receive the return value (see <code>@gwt.callbackReturn</code> tag)
	 * @gwt.callbackReturn the key to identify the file, to be placed into servlet query.
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void exportToPicture(String content, AsyncCallback<Long> callback);

	/**
	 * Generate verilog code out of the application content.
	 * @return
	 * @param  callback the callback that will be called to receive the return value
	 * @generated generated method with asynchronous callback parameter to be used on the client side
	 */
	void exportToVerilog(String verilog, AsyncCallback<Long> callback);

}
