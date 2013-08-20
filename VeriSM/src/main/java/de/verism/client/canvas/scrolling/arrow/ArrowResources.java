package de.verism.client.canvas.scrolling.arrow;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

import de.verism.client.components.icons.IconResources;

/**
 * Provides image resources for the cardinal scrolling arrows.
 * Makes reference to arrow images of all 8 cardinal directions.
 * 
 * @author Daniel Kotyk
 *
 */
public interface ArrowResources extends ClientBundle {
	//up + down
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowNorthMiddle();
	
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowSouthMiddle();
	
	//left + right
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowEastMiddle();
	
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowWestMiddle();
	
	//corners
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowWestTop();
	
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowWestBottom();
	
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowEastTop();
	
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource arrowEastBottom();
	
	//for making empty focuspanels visible
	@ImageOptions(width = IconResources.WIDTH)
	ImageResource spacer();
}
