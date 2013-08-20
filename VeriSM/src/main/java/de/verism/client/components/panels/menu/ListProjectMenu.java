package de.verism.client.components.panels.menu;

import java.util.ArrayList;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.widget.client.TextButton;

import de.verism.client.components.dialog.login.LoginState;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.data.Cache;
import de.verism.client.rpc.JSONCallback;
import de.verism.client.rpc.ProjectService;
import de.verism.client.rpc.ProjectServiceAsync;

/**
 * Shows a list of all projects a user has saved in DB.
 * @author Daniel Kotyk
 *
 */
public class ListProjectMenu extends Composite {
	interface Binder extends UiBinder<Widget, ListProjectMenu> {}
	
	@UiField(provided = true) CellList<String> cellList;
	@UiField TextButton load, delete;
	@UiField Image loadingIndicator;
	
	private SingleSelectionModel<String> selectionModel;
	private ListDataProvider<String> provider;
	
	public ListProjectMenu(ArrayList<String> projects) {
		selectionModel = new SingleSelectionModel<String>();
		
		cellList = new CellList<String>(new TextCell(), CellListResource.INSTANCE);
		cellList.setSelectionModel(selectionModel);
		//make selection follow key events
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		
		provider = new ListDataProvider<String>(projects);
		provider.addDataDisplay(cellList);

		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}
	
    private ProjectServiceAsync projectService = GWT.create(ProjectService.class);

    /**
     * Callback to delete a project from the list.
     * @author Daniel Kotyk
     */
    class DeleteProjectCallback implements AsyncCallback<Boolean> {
		@Override
		public void onFailure(Throwable caught) {
			new NotifierPopup("Project '" + Cache.get().getCanvasFooter().getProjectName()
						+ "' could not be deleted: " + caught.getMessage(), NotifierState.ERROR);
			loadingIndicator.setVisible(false);
			delete.setEnabled(true);
		}

		@Override
		public void onSuccess(Boolean deleted) {
			//delete the project also in current visible list on the client side if deletion in the webserver db was successful
			provider.getList().remove(selectionModel.getSelectedObject());
			
			//Workaround for GWT issue #8106: the cellList is currently not updated if only a single row element remains
			if (provider.getList().size() == 1) {
				try {
					cellList.redrawRow(0);
					cellList.redrawRow(1);
				} catch (Exception e) {
					//failing redraws are not serious in this case 
				}
			}
			
			//force update on the visual list as a GWT framework issue prevents auto-refresh (comments.gmane.org/gmane.org.google.gwt/52306)
			cellList.setVisibleRangeAndClearData(cellList.getVisibleRange(), true);
			loadingIndicator.setVisible(false);
			delete.setEnabled(true);
		}
    }
    
    @UiHandler("load")
    void onLoad(ClickEvent evt) {
    	if (hasSelection()) {
    		load.setEnabled(false);
    		loadingIndicator.setVisible(true);
		    //call JSONCallback to reuse the upload logic
		    projectService.getProject(LoginState.INSTANCE.getUsername(), selectionModel.getSelectedObject(),
		    		new JSONCallback((DialogBox) getParent().getParent()));
    	}
    }

	@UiHandler("delete")
    void onDelete(ClickEvent evt) {
    	if (hasSelection()) {
    		delete.setEnabled(false);
    		loadingIndicator.setVisible(true);
		    projectService.deleteProject(LoginState.INSTANCE.getUsername(), selectionModel.getSelectedObject(),
		    		new DeleteProjectCallback());
    	}
    }
	
    /**
     * Helper to prevent actions on the list if no selection has been made.
     * @return
     */
    private boolean hasSelection() {
    	return selectionModel.getSelectedObject() != null;
	}
}
