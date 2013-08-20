package de.verism.client.components.panels.menu;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.components.dialog.StartupDialog;
import de.verism.client.components.dialog.edit.EditorDialog;
import de.verism.client.components.dialog.login.LoginState;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.JsonDTO;
import de.verism.client.domain.data.Cache;
import de.verism.client.rpc.ProjectService;
import de.verism.client.rpc.ProjectServiceAsync;
import de.verism.client.util.export.JsonBuilder;

/**
 * The whole project menu defining submenus.
 * @author Daniel Kotyk
 *
 */
public class ProjectMenu extends Composite {
	interface Binder extends UiBinder<Widget, ProjectMenu> {}
	
	@UiField Hyperlink newProject, open, save;
	
	public ProjectMenu() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}
	
    private ProjectServiceAsync projectService = GWT.create(ProjectService.class);

    //the callback handling the server return values
    class ProjectCallback implements AsyncCallback<Boolean> {
		@Override
		public void onFailure(Throwable caught) {
			new NotifierPopup("Project creation failed with: " + caught.getMessage(),
					NotifierState.ERROR);
			LoadingIndicator.hide();
		}

		@Override
		public void onSuccess(Boolean created) {
			if (created) {
				new NotifierPopup("Project '" + Cache.get().getCanvasFooter().getProjectName()
					+ "' has been saved", NotifierState.SUCCESS);
				LoadingIndicator.hide();
			}
		}
    }
    
	
	@UiHandler("save")
	void onSave(ClickEvent evt) {
		if (LoginState.INSTANCE.isLoggedOut()) {
			new NotifierPopup("Projects can only be saved for logged in users. Export your project instead.",
					NotifierState.WARN);
			return;
		}
		LoadingIndicator.show(evt.getClientX(), evt.getClientY());
		
		//create the transfer object
		JsonBuilder jsonBuilder = new JsonBuilder();
		JsonDTO jsonDTO = jsonBuilder.prepareJsonDTO(Cache.get());
		
		//save to DB
		projectService.saveProject(LoginState.INSTANCE.getUsername(), jsonDTO, new ProjectCallback());
		
		//revert id changes done within the serialization
		jsonBuilder.conditionsToId(Cache.get());
	}
	
	@UiHandler("newProject")
	void onNewProject(ClickEvent evt) {
		DialogBox box = new EditorDialog();
		StartupDialog startup = new StartupDialog(box);
		
		box.add(startup);
		box.setGlassEnabled(true);
		box.setText("All unsaved work will be lost!");
		box.center();
		box.show();
	}
	
    
    /**
     * Callback fetching the whole list of projects.
     * @author Daniel Kotyk
     */
    class ListProjectCallback implements AsyncCallback<ArrayList<String>> {
		@Override
		public void onFailure(Throwable caught) {
			new NotifierPopup("Projects could not be displayed: " + caught.getMessage(),
					NotifierState.ERROR);
		}

		@Override
		public void onSuccess(ArrayList<String> projects) {
			DialogBox box = new EditorDialog();
			box.add(new ListProjectMenu(projects));
			box.setText("Open or delete one of your projects");
			box.center();
			box.show();
		}
    }
    
	@UiHandler("open")
	void onOpen(ClickEvent evt) {
		//saved projects can of course only be opened for logged in users
		if (LoginState.INSTANCE.isLoggedOut()) {
			onNewProject(evt);
			return;
		}
		
		//fetch all projects and show the project details dialog on callback
		projectService.getProjects(LoginState.INSTANCE.getUsername(), new ListProjectCallback());
	}
}
