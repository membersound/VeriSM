package de.verism.client.components.panels.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;

/**
 * Define custom CellTable styles (spreadsheet/excel like).
 * @author Daniel Kotyk
 *
 */
public interface CellListResource extends CellList.Resources {
	// instance to be added to the CellTable
	public static final CellList.Resources INSTANCE = GWT.create(CellListResource.class);

	// defines the custom css style file
	interface CellListStyle extends CellList.Style {
		String CSS = "CellList.css";
	}

	// sets the custom css styles to the celltable
	@Override
	@Source(value = {CellList.Style.DEFAULT_CSS, CellListStyle.CSS})
	CellList.Style cellListStyle();
}