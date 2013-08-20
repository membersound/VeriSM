package de.verism.client.components.footer;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.ZoomHandler;
import de.verism.client.com.google.gwt.incubator.SliderBar;
import de.verism.client.components.contextMenu.ProjectNameContextMenu;
import de.verism.client.components.dialog.login.ResetView;
import de.verism.client.components.editTextBox.EditTextBox;
import de.verism.client.components.panels.io.spreadsheet.cell.LabelEditTextCell;
import de.verism.client.components.panels.io.spreadsheet.cell.render.NameCellStrategy;

/**
 * Holds the UI footer elements for the project name and the slider.
 * @author Daniel Kotyk
 *
 */
public class CanvasFooter extends Composite implements ResetView {
	interface Binder extends UiBinder<Widget, CanvasFooter> {}

	@UiField(provided = true)
	EditTextBox projectName;
	
	@UiField
	SliderBar slider;
	
	@UiField
	Image zoomIn, zoomOut;
	
	/**
	 * Initialize the first canvas.
	 */
	public CanvasFooter() {
		EditTextCell editTextCell = new LabelEditTextCell(new NameCellStrategy());
		projectName = new EditTextBox(editTextCell);
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
		
		//attach custom context menu
		ProjectNameContextMenu menu = new ProjectNameContextMenu(projectName);
		projectName.addContextMenuHandler(menu.getHandler());
		
		//init the zoom handler
		zoomHandler = new ZoomHandler(slider);
	}
	
	private ZoomHandler zoomHandler;
	public ZoomHandler getZoomHandler() { return zoomHandler; }
	
	@UiHandler("slider")
	void onValueChange(ValueChangeEvent<Double> evt) {
		zoomHandler.updateValue();
	}
	
	@UiHandler("zoomIn")
	void onZoomIn(ClickEvent evt) {
		 // sets the slider on click of the zoom icons,
		 // and implicit fires an update event that causes the #onValueChange() to execute the zooming in the canvas.
		slider.setCurrentValue(ZoomHandler.MAX, true);
	}
	
	@UiHandler("zoomOut")
	void onZoomOut(ClickEvent evt) {
		slider.setCurrentValue(ZoomHandler.MIN, true);
	}

	//this is used for the jsonDTO.
	//as EditTextBox implements HasText, this pays off now and so providing an easy interface for getting and setting the project name text.
	public String getProjectName() { return projectName.getText(); }
	public void setProjectName(String text) { projectName.setText(text); }

	@Override
	public void reset() {
		projectName.setText(NameCellStrategy.EMPTY);
		slider.setCurrentValue(ZoomHandler.DEFAULT, true);
	}
}
