package de.verism.client.components.panels.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.CanvasArea;
import de.verism.client.components.panels.error.ErrorField;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.JsonDTO;
import de.verism.client.domain.data.Cache;
import de.verism.client.rpc.JSONService;
import de.verism.client.rpc.JSONServiceAsync;
import de.verism.client.util.export.JsonBuilder;
import de.verism.client.util.export.VerilogCodeService;
import de.verism.client.util.export.VerilogPreValidationService;
import de.verism.client.validation.ValidationError;
import de.verism.server.file.FileDownloadServlet;
import de.verism.shared.file.FileType;
/**
 * The menu entry for exporting a project.
 * @author Daniel Kotyk
 *
 */
public class ExportMenu extends Composite {
	interface Binder extends UiBinder<Widget, ExportMenu> {}
	
	public ExportMenu() {
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));
	}
	
	@UiField Hyperlink verilog, png, save;

	// target servlet link, prepared to append the project canvas content
	private String targetLink = GWT.getModuleBaseURL()
			+ FileDownloadServlet.SERVLET_NAME
			+ "?" + FileDownloadServlet.CONTENT_PROP + "=";
	
	// the RPC service
	private JSONServiceAsync jsonService = GWT.create(JSONService.class);
	
	/**
	 * Create the downloadable project file by calling the {@link FileDownloadServlet}.
	 * @param evt
	 */
	@UiHandler("save")
	void onSave(ClickEvent evt) {
		if (vetoMenu) {
			new NotifierPopup("Please wait: the file generation is already in progress.", NotifierState.WARN);
			return;
		}
		LoadingIndicator.show(evt.getClientX(), evt.getClientY());
	    vetoMenu = true;
		
		//create the object
		JsonBuilder jsonBuilder = new JsonBuilder();
		JsonDTO jsonDTO = jsonBuilder.prepareJsonDTO(Cache.get());
		
		//calls the server RPC service
		jsonService.serializeToJson(jsonDTO, new JSONCallback(FileType.PROJECT));
		
		//revert id changes done within the serialization
		jsonBuilder.conditionsToId(Cache.get());
	}

	/**
	 * The callback receiving the generated id for calling the downloadServlet.
	 * @author Daniel Kotyk
	 */
	class JSONCallback implements AsyncCallback<Long> {
		private FileType fileType;
		
		public JSONCallback(FileType fileType) {
			this.fileType = fileType;
		}

		@Override
		public void onFailure(Throwable caught) {
			new NotifierPopup("Unable to obtain server response: " + caught.getMessage(), NotifierState.ERROR);
		    caught.printStackTrace();
		    
		    //also failing exports should reset the veto state, as otherwise the user cannot retry the export
		    LoadingIndicator.hide();
		    vetoMenu = false;
		}

		@Override
		public void onSuccess(Long id) {
			//create the servlet query
			//save.setHref(targetLink + jsonDTO);
			   
			//chain of parameters possible by builder pattern
			QueryBuilder query = new QueryBuilder();
			query.addParam(FileDownloadServlet.TYPE_PROP, fileType.toString())
			     .addParam(FileDownloadServlet.FILE_NAME, Cache.get().getCanvasFooter().getProjectName());
				
			//calls the downloadServlet. '_self' prevents opening a new tab before the download dialog is shown.
			Window.open(targetLink + id + query.toString(), "_self", "");
				
			new NotifierPopup("File download generation successfull", NotifierState.SUCCESS);
		    LoadingIndicator.hide();
		    vetoMenu = false;
		}
		   
		/**
		 * Builder Pattern class to create chains of params for the servlet request.
		 * @author Daniel Kotyk
		 *
		 */
		private class QueryBuilder {
			private StringBuilder builder = new StringBuilder();
			   
			/**
			 * Adds a new query param.
			 * @param key the key identifying the parameter
			 * @param value the value of the parameter
			 * @return the builder instance itself to provide chaining
			 */
			private QueryBuilder addParam(String key, String value) {
			   builder.append("&" + key + "=" + value);
			   return this;
			}
			   
			@Override
			public String toString() {
				return builder.toString();
			}
		}
	}
	
	@UiHandler("verilog")
	void onVerilog(ClickEvent evt) {
		if (vetoMenu) {
			new NotifierPopup("Please wait: the file generation is already in progress.", NotifierState.WARN);
			return;
		}
		LoadingIndicator.show(evt.getClientX(), evt.getClientY());
	    vetoMenu = true;
		
		//validate application data before verilog code generation
		VerilogPreValidationService verilogVal = new VerilogPreValidationService();
		if (verilogVal.hasCodeErrors(CanvasArea.get(), Cache.get())) {
			new NotifierPopup("Please correct the following errors", NotifierState.ERROR);
			
			String errorMsg = "";
			for (ValidationError error : verilogVal.getValidationError()) {
				errorMsg += "- " + error.getMessage() + "\n";
			}
			
			//create the error popup
			ErrorField errorField = new ErrorField();
			errorField.setText(errorMsg);
			errorField.setVisible(true);
			
			PopupPanel popup = new PopupPanel(true, false);
			popup.setWidget(errorField);
			popup.setWidth("500px");
			popup.setTitle("Validation errors");
			popup.center();
			popup.show();
			
		    LoadingIndicator.hide();
		    vetoMenu = false;
			return;
		}
		
		//Code generation is delegated to a single service
		VerilogCodeService verilogService = new VerilogCodeService(Cache.get());
		String verilog = verilogService.createCode();
		jsonService.exportToVerilog(verilog, new JSONCallback(FileType.VERILOG));
	}

	//during execution of a rpc call, the menu action may not be executed twice to not generate multiple files accidentially
	private boolean vetoMenu;
	
	@UiHandler("png")
	void onPictureExport(ClickEvent evt) {
		if (vetoMenu) {
			new NotifierPopup("Please wait: the file generation is already in progress.", NotifierState.WARN);
			return;
		}
		LoadingIndicator.show(evt.getClientX(), evt.getClientY());
	    vetoMenu = true;
		
		//clear selection
		CanvasArea.get().setSelection(null);
		CanvasArea.get().bufferUnselected();
		CanvasArea.get().repaintSelected();
		
		//save the whole content (also what's invisible to the viewport) on a single exportCanvas.
		new NotifierPopup("Picture generation successfull", NotifierState.SUCCESS);
		
		//save content to base64 string
		String png = CanvasArea.get().getCanvas().toDataUrl(FileType.PICTURE.getContentType());
	    new ImagePopup(new Image(png));
	    LoadingIndicator.hide();
	    vetoMenu = false;
	}
}
