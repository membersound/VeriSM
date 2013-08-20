package de.verism.client.components.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 * Ressource provider.
 * @author Daniel Kotyk
 *
 */
public interface IconResources extends ClientBundle {
  public static final IconResources INSTANCE = GWT.create(IconResources.class);

  static int WIDTH = 16;

  @Source("plus2-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource addMenuIcon();

  @Source("delete-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource deleteMenuIcon();

  @Source("file-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource newProjectIcon();
  
  @Source("filemenu-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource newProjectMenuIcon();

  @Source("upload-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource uploadProjectIcon();

  @Source("electronics-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource exportVerilogIcon();

  @Source("picture-16.png") //compact_camera-16.png
  @ImageOptions(width = WIDTH)
  ImageResource exportPictureIcon();

  @Source("download-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource exportProjectIcon();

  @Source("about-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource helpIcon();

  @Source("edit-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource editEntryIcon();

  @Source("delete2-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource cancelIcon();

  @Source("checkmark-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource saveIcon();	

  @Source("plasmid-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource initialStateIcon();

  @Source("line-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource straighten();
  
  @Source("text-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource showAll();
  
  @Source("zoom_in-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource zoomIn();
  
  @Source("zoom_out-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource zoomOut();
  
  @Source("guest-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource loginIcon();
  
  @Source("key-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource registerIcon();
  
  @Source("guest-menu-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource login();
  
  @Source("key-menu-16.png")
  @ImageOptions(width = WIDTH)
  ImageResource register();
  
  @Source("dropdown-26.png")
  @ImageOptions(width = WIDTH)
  ImageResource dropdown();
  
  @Source("folder-26.png")
  @ImageOptions(width = WIDTH)
  ImageResource folder();
  
  @Source("save-26.png")
  @ImageOptions(width = WIDTH)
  ImageResource save();
} 
