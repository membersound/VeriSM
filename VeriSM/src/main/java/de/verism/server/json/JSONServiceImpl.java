package de.verism.server.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.verism.client.domain.JsonDTO;
import de.verism.client.rpc.JSONService;


/**
 * Server-side RPC service for (de)-serialization of canvas shapes.
 * @author Daniel Kotyk
 *
 */
public class JSONServiceImpl extends RemoteServiceServlet implements JSONService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonDTO deserializeFromJson(Long key) throws IOException {
		String input = FileContentProvider.getFileContent(key);
		
		//convert html chars like "&amp;" back to UTF-8 chars "&"
		return getMapper().readValue(input, JsonDTO.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long serializeToJson(JsonDTO jsonDTO) throws IOException {
		//serialize the dto to json string
		String content = getMapper().writeValueAsString(jsonDTO);
		return FileContentProvider.addFileContent(content);
	}

	/**
	 * Provides the preconfigured mapper for (de)serialization.
	 * 
	 * Jackson by default takes all public field, public getters+setters.
	 * As some class may contain public getters used in the application only, which do not refer to a variable,
	 * all get/set methods can be excluded by default.
	 * Further, visibility of the all variable fields has to be increased to ANY, as otherwise only public fields will be found.
	 * @return the configure json mapper
	 */
	public static ObjectMapper getMapper() {
		return new ObjectMapper()
			.setVisibility(PropertyAccessor.ALL, Visibility.NONE) //disable all getter/setter
			.setVisibility(PropertyAccessor.FIELD, Visibility.ANY) //enable all variable fields to be serialized
			.setDefaultTyping(new DefaultTypeResolverBuilder(DefaultTyping.NON_CONCRETE_AND_ARRAYS) //defaultTyping handles abstract classes and arrays (eg List<Drawable>)
				.init(JsonTypeInfo.Id.MINIMAL_CLASS, null) //take only the simple class name as reference, not the fully qualified package name
	        	.inclusion(JsonTypeInfo.As.PROPERTY)); //include the additional class information as a property "@c" in each json object
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long exportToPicture(String content) {
		return FileContentProvider.addFileContent(content);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long exportToVerilog(String verilog) {
		return FileContentProvider.addFileContent(verilog);
	}
}
