package de.verism.client.components.contextMenu;

import de.verism.client.components.dialog.edit.IsInputProvider;

/**
 * Classes implementing {@link HasContextMenu} must provide
 * a context menu edit panel for editing the objects properties.
 * @author Daniel Kotyk
 *
 */
public interface HasContextMenu {

	/**
	 * Returns the context menu for editing the objects' properties.
	 * @return
	 */
	IsInputProvider getContextMenu();
}
