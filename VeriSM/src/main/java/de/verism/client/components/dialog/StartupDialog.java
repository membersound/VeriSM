package de.verism.client.components.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widget.client.TextButton;

import de.verism.client.components.panels.menu.MenuPanel;
import de.verism.client.domain.data.Cache;
import de.verism.client.rpc.JSONCallback;
import de.verism.client.rpc.JSONService;
import de.verism.client.rpc.JSONServiceAsync;
import de.verism.client.util.FocusHelper;
import de.verism.server.file.FileUploadServlet;
import de.verism.shared.file.FileType;

/**
 * The initial popup for loading existing project files.
 * @author Daniel Kotyk
 *
 */
public class StartupDialog extends Composite {
	interface Binder extends UiBinder<Widget, StartupDialog> {}

    private DialogBox dialogBox;
    
    @UiField FormPanel uploadForm;
    @UiField TextButton submitBtn, newBtn;
    @UiField FileUpload fileUpload;
    @UiField Label errorLbl;
    @UiField Image errorImage, loadingIndicator;
    
    /**
     * Must provide a {@link DialogBox} for creating this wrapper.
     * @param dialogBox
     */
    @UiConstructor
    public StartupDialog(DialogBox dialogBox) {
    	this.dialogBox = dialogBox;
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));

		dialogBox.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				//callback notification: logged in successfully; change login to logout text link
				MenuPanel.updateLoginMenu();
			}
		});
		
		FocusHelper.focus(newBtn);
    }
    
    /**
     * Show the dialog centered.
     */
	public void show() {
        dialogBox.center();
        dialogBox.show();
	}
    
    @UiHandler("newBtn")
    void onNewBtn(ClickEvent evt) {
    	Cache.get().cleanup();
    	dialogBox.hide();
    }
    
    @UiHandler("submitBtn")
    void onSubmitBtn(ClickEvent evt) {
    	//prevent submits when button was not enabled
    	if (!submitBtn.isEnabled()) {
    		updateError("No file has been chosen.", true);
    		return;
    	}
    	
        //disable the submit button during upload and show loading icon
    	submitBtn.setEnabled(false);
    	updateError("", false);
    	loadingIndicator.setVisible(true);
    	
    	//submit the file
        uploadForm.submit();
    }
    
    /**
     * Import the file content from json format to {@Drawable} objects.
     * Executed after the {@link FileUploadServlet} has been called.
     * @param evt
     */
    @UiHandler("uploadForm")
    void onSubmitForm(SubmitCompleteEvent evt) {
    	Long key;
    	try {
    		//split the response by delimiter, as on some browsers the upload form may append some html code due to missing browser standards
    		String[] split = evt.getResults().split(FileUploadServlet.ID_DELIMITER);
    		
    		//extract the key from the upload response
    		key = Long.valueOf(split[0]).longValue();
    	} catch (Exception e) {
    		 updateError("File upload failed. Upload response has errors:\n" + evt.getResults(), true);
    		 return;
    	}

    	//use the key to get the deserialized json dto by rpc
    	jsonService.deserializeFromJson(key, new JSONCallback(dialogBox));
    }
    
    
    private JSONServiceAsync jsonService = GWT.create(JSONService.class);
	
    /**
     * Enable the submit button only if a file has been selected and it has the right extension.
     * @param evt
     */
    @UiHandler("fileUpload")
    void onChange(ChangeEvent evt) {
        boolean isFileValid = !fileUpload.getFilename().isEmpty() && validExtension(fileUpload.getFilename());
        submitBtn.setEnabled(isFileValid);
        
        //change error label
        String error = isFileValid ? "" : "File invalid.";
        updateError(error, !isFileValid);
        
        //change focused button
        TextButton focusButton = isFileValid ? submitBtn : newBtn;
		FocusHelper.focus(focusButton);
    }
    
    /**
     * Update the error field with a message.
     * @param msg the message do display
     * @param show if the error icon should be displayed
     */
    private void updateError(String msg, boolean show) {
    	loadingIndicator.setVisible(false);
    	errorLbl.setText(msg);
    	errorImage.setVisible(show);
    }
    
    /**
     * Validate the file extension.
     * @param filename
     * @return
     */
    private boolean validExtension(String filename) {
        //extract extension from filename by making substring of last point
        String extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length());
        return extension.equals(FileType.PROJECT.getFileExt());
    }
}
