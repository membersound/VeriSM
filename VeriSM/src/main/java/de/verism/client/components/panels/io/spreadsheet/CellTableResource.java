package de.verism.client.components.panels.io.spreadsheet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * Define custom CellTable styles (spreadsheet/excel like).
 * @author Daniel Kotyk
 *
 */
public interface CellTableResource extends DataGrid.Resources {
	// instance to be added to the CellTable
	public static final DataGrid.Resources INSTANCE = GWT.create(CellTableResource.class);

	// defines the custom css style file
	interface CellTableStyle extends DataGrid.Style {
		String CSS = "CellTableSpreadsheet.css";
	}

	// sets the custom css styles to the celltable
	@Override
	@Source(value = {DataGrid.Style.DEFAULT_CSS, CellTableStyle.CSS})
	DataGrid.Style dataGridStyle();
}