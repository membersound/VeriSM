package de.verism.client.components.dialog.edit;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import de.verism.client.validation.ValidationError;


/**
 * Defines basic behavior of this {@link BaseEditor}.
 * As this panel expects a panel that implements the {@link IsInputProvider} interface,
 * this simply ensures that save + validation mechanisms are provided by the outer widget
 * passed in the {@link BaseEditor} constructor.
 * 
 * Its a kind of strategy pattern, defining how to save and validate the input fields,
 * which is always different according to what object is currently edited.
 * @author Daniel Kotyk
 *
 */
public interface IsInputProvider extends IsWidget {
    /**
     * Validates all fields using rule validation.
     * It's up to the specific implementation how to validate the fields, if at all.
     * 
     * Every value that is not desired to be saved should be validated here and 
     * give error message accordingly.
     * @return a list containing all validation errors.
     */
	List<ValidationError> validateInputFields();
	
    /**
     * Saves the values from input boxes to the object under edit.
     * Should not perform any further validation as this should be already done
     * in {@link IsInputProvider#validateInputFields()}.
     */
	void save();
	
    /**
     * Clears the error styles on the textboxes, like error icon and red border.
     */
	void clearErrorStyles();
}
