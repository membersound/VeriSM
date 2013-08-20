package de.verism.client.rpc;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.domain.JsonDTO;
import de.verism.server.json.JSONServiceImpl;

/**
 * Client-side RPC service for (de)-serialization of canvas shapes.
 * Provides ability to call the server-slide RPC service implementation {@link JSONServiceImpl}.
 * @author Daniel Kotyk
 *
 */
@RemoteServiceRelativePath("json")
public interface JSONService extends RemoteService {
	/**
	 * Extract all {@link Drawable} out of the project file
	 * @param key the file identifier for the uploaded content stored on the server
	 * @return
	 * @throws IOException
	 */
	JsonDTO deserializeFromJson(Long key) throws IOException;
	
	/**
	 * Generate the project file representing all {@link Drawable} as JSON strings.
	 * @param jsonDTO
	 * @return the key to identify the file, to be placed into servlet query.
	 * @throws IOException
	 */
	Long serializeToJson(JsonDTO jsonDTO) throws IOException;
	
	/**
	 * Export the canvas content to picture.
	 * @param content the image stream
	 * @return the key to identify the file, to be placed into servlet query.
	 */
	Long exportToPicture(String content);
	
	/**
	 * Generate verilog code out of the application content.
	 * @return
	 */
	Long exportToVerilog(String verilog);
}
