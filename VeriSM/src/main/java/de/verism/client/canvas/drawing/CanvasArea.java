package de.verism.client.canvas.drawing;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.verism.client.canvas.drawing.MouseContext.CanvasState;
import de.verism.client.canvas.shapes.DrawStates;
import de.verism.client.canvas.shapes.DrawStates.DrawState;
import de.verism.client.canvas.shapes.Drawable;
import de.verism.client.canvas.shapes.Line;
import de.verism.client.canvas.shapes.Rectangle;
import de.verism.client.canvas.shapes.rendering.Point;
import de.verism.client.canvas.shapes.visitors.DeleteValidationService;
import de.verism.client.canvas.shapes.visitors.Visitor;
import de.verism.client.components.contextMenu.CanvasContextMenu;
import de.verism.client.components.contextMenu.CanvasNewContextMenu;
import de.verism.client.components.dialog.edit.EditorDialog;
import de.verism.client.components.dialog.edit.IsInputProvider;
import de.verism.client.components.dialog.editState.EditStatePanel;
import de.verism.client.components.dialog.login.ResetView;
import de.verism.client.components.panels.notification.NotifierPopup;
import de.verism.client.components.panels.notification.NotifierState;
import de.verism.client.domain.State;
import de.verism.client.domain.data.Cache;
import de.verism.client.domain.data.CanvasDataProvider;
import de.verism.client.util.BooleanKeyCodes;
import de.verism.client.util.ClassNameResolver;
import de.verism.client.util.PopupHelper;
import de.verism.client.util.WindowResizeHandler;
import de.verism.client.validation.ValidationError;

/**
 * Defines the drawing area (main content). Also provides interaction handling
 * on this canvas. Encapsulates all events on the visible canvas (mouse +
 * keystroke), so that it is clean separation from the gui canvasArea. It just
 * should maintain the states (like a holder).
 * 
 * @author Daniel Kotyk
 */
public class CanvasArea extends Composite implements HasContextMenuHandlers, CanvasCallback, ResetView {
	interface Binder extends UiBinder<Widget, CanvasArea> {}

	//the canvas refresh rate in ms
	public static final int FRAMERATE = 15;

	@UiField(provided = true)
	Canvas canvas;

	// wraps the canvas and provides ability of stacking widgets onto each other.
	// used to show edit labels for the text elements in the canvas.
	@UiField
	AbsolutePanel absolutePanel;

	// the main canvas data provider
	private CanvasDataProvider canvasDataProvider = new CanvasDataProvider();

	// the currently selected object
	private Drawable selection;

	// tracks last selection for drawing connection lines between two states
	private Drawable lastSelection;

	// handler of the buffer canvas used for Drag & Drop
	private CanvasDrawHandler drawHandler;

	// init the canvas state
	private final CanvasState STATE_DRAG = new DragState();
	private final CreateState STATE_CREATE = new CreateState();
	private final ArrowState STATE_ARROW = new ArrowState();
	private final ViewportState STATE_VIEWPORT = new ViewportState();
	private MouseContext mouseCtx = new MouseContext(STATE_CREATE);

	public static DrawState DRAW_STATE = DrawStates.DRAW_DEFAULT;

	/**
	 * Create the main canvas container for drawing.
	 */
	public CanvasArea() {
		canvas = CanvasFactory.getCanvas();
		if (canvas == null) {
			RootPanel.get().add(new Label("Sorry, this application needs a HTML5 enabled browser."));
			return;
		}

		// instantiates all @UiField variables
		initWidget(GWT.<Binder> create(Binder.class).createAndBindUi(this));

		INSTANCE = this;
		drawHandler = new CanvasDrawHandler(this);
		contextMenu = new CanvasContextMenu(this);
		addContextMenuHandler(contextMenu.getHandler());
		addContextMenuHandler(new CanvasNewContextMenu(this).getHandler());
	}
	
	private CanvasContextMenu contextMenu;

	// instance for making the canvasArea a singleton
	private static CanvasArea INSTANCE;

	/**
	 * Helper for obtaining an instance of this renderer. Thread-safe singleton.
	 * 
	 * @return
	 */
	public static CanvasArea get() {
		return INSTANCE;
	}

	/**
	 * Provides ui:binder attaching resize handler to the whole window.
	 * 
	 * @param has
	 */
	public void setHasResizeHandler(boolean has) {
		ResizeHandler windowHandler = new WindowResizeHandler(this);
		Window.addResizeHandler(windowHandler);
	}

	// -------------------------- Mouse Event Handling ----------------------- //
	
	/**
	 * Capture mouse moves while dragging and update XY of the current selection
	 * considering the offset.
	 */
	@UiHandler("canvas")
	void onMouseMove(MouseMoveEvent evt) {
		mouseCtx.onMouseMove(evt);
	}

	/**
	 * Clears the selection when mouse leaves the canvas area, to prevent
	 * deletion on accident of a selected shape.
	 */
	@UiHandler("canvas")
	void onMouseOut(MouseOutEvent evt) {
		// cancel any maybe running timers when leaving the canvas
		dndTimer.cancel();
		viewportTimer.cancel();

		// if there is no veto, then clear the selection
		if (!contextMenu.getHandler().isVetoMouseOut()) {
			selection = null;
			saveAndRedraw();
		}
	}

	/**
	 * Mouse left down either selects existing elements or defines the XY of a
	 * new state. Mouse right down just selects the object clicked.
	 */
	@UiHandler("canvas")
	void onMouseDown(MouseDownEvent evt) {
		// force focus to remove maybe remaining focus in spreadsheet
		canvas.setFocus(true);

		// hit detect element under mouse
		int mouseX = evt.getRelativeX(canvas.getElement());
		int mouseY = evt.getRelativeY(canvas.getElement());
		findSelection(mouseX, mouseY);

		//only show add-state-context-menu if no object is selected
		if (selection == null) {
			switch (evt.getNativeButton()) {
				case NativeEvent.BUTTON_LEFT: mouseCtx.setState(STATE_VIEWPORT); break;
				case NativeEvent.BUTTON_RIGHT: mouseCtx.setState(STATE_CREATE); break;
			}
		}
		mouseCtx.onMouseDown(evt);
	}

	/**
	 * Mouse up either creates a new state or cancels the drag and drop timer.
	 */
	@UiHandler("canvas")
	void onMouseUp(MouseUpEvent evt) {
		mouseCtx.onMouseUp(evt);
	}
	

	/**
	 * Doubleclick makes a shape editable.
	 */
	@UiHandler("canvas")
	void onDblClick(DoubleClickEvent evt) {
		editDrawable(selection, evt.getClientX(), evt.getClientY());
	}
	
	/**
	 * Creates a new state in the canvas.
	 */
	@Override
	public void addState() {
		STATE_CREATE.createNew();
		// as new states are added by context menu, focus has to be forced to canvas again
		canvas.setFocus(true);
	}

	// ------------------------ Key Events -------------------- // 
	
	/**
	 * Handles drawing of transitions while shift key is pressed.
	 * Also delete key removes a selected shape in the canvas.
	 * @param evt
	 */
	@UiHandler("canvas")
	void onKeyDown(KeyDownEvent evt) {
		switch(evt.getNativeEvent().getKeyCode()) {
			case BooleanKeyCodes.KEY_SHIFT: mouseCtx.setState(STATE_ARROW); break;
			case KeyCodes.KEY_DELETE: remove(selection); break;
		}
	}

	/**
	 * Handles object removing on delete key press.
	 * @param evt
	 */
	@UiHandler("canvas")
	void onKeyUp(KeyUpEvent evt) {
		if (evt.getNativeKeyCode() == BooleanKeyCodes.KEY_SHIFT) {
			// when shift key is released, return to initial state
			STATE_ARROW.clearDynamicLine();
			mouseCtx.setState(STATE_CREATE);
		}
	}
	
	// ------------------------- Selection Handling ------------------------- //
	
	private SelectionHandler selectionHandler = new SelectionHandler();

	/**
	 * Test if current mouseclick position was inside an existing shape. If so,
	 * set the shape selected.
	 * 
	 * @param mouseY
	 * @param mouseX
	 */
	private void findSelection(int mouseX, int mouseY) {
		// if the dynamic line is visible, only rectangles should be selectable
		// (lines should not connect to other lines).
		List<? extends Drawable> selectables = (STATE_ARROW.getDynamicLine() != null) ? canvasDataProvider
				.getRectangles() : canvasDataProvider.getDrawables();

		// find the selection
		Drawable drawable = selectionHandler.getSelection(selectables, mouseX, mouseY);

		// exclude the dynamic arrow so that it cannot be selected by accident,
		// as the dynamic line will be always on top of all other shapes.
		if (drawable != null && drawable != STATE_ARROW.getDynamicLine()) {
			lastSelection = selection;
			selection = drawable;
			contextMenu.setDrawable(selection);

			CanvasArea.DRAW_STATE = DrawStates.DRAW_DRAG;
			selection.draw(canvas.getContext2d());
			return;
		}

		// clear selection if click was not inside a shape
		selection = null;
	}

	// -----------------------------State Handling ---------------------------- //
	
	public boolean isDragState() {
		return mouseCtx.getCanvasState().equals(STATE_DRAG);
	}

	/**
	 * State for moving the whole canvas viewport area while left mouse is hold down.
	 * 
	 * @author Daniel Kotyk
	 * 
	 */
	public class ViewportState implements CanvasState {
		// tracks the last known mouse position
		private int mouseX, mouseY;

		/**
		 * Track initial mouse position and start the refresh timer.
		 */
		@Override
		public void onMouseDown(MouseContext mouseCtx, MouseDownEvent evt) {
			mouseX = evt.getRelativeX(canvas.getElement());
			mouseY = evt.getRelativeY(canvas.getElement());

			//less frequent redrawing as always the whole canvas is redrawn at the scheduled interval
			viewportTimer.scheduleRepeating(FRAMERATE * 2);
		}

		/**
		 * End the viewport state.
		 */
		@Override
		public void onMouseUp(MouseContext mouseCtx, MouseUpEvent evt) {
			viewportTimer.cancel();
			mouseCtx.setState(STATE_CREATE);
		}

		/**
		 * Change the viewport offset according to mouse movements.
		 */
		@Override
		public void onMouseMove(MouseContext mouseCtx, MouseMoveEvent evt) {
			// save the new mouse position
			int newMouseX = evt.getRelativeX(canvas.getElement());
			int newMouseY = evt.getRelativeY(canvas.getElement());

			updateViewport(newMouseX, newMouseY);
		}
		
		/**
		 * Helper to update the position offset within the viewport.
		 * @param newMouseX
		 * @param newMouseY
		 */
		public void updateViewport(int newMouseX, int newMouseY) {

			// update viewport offset with the unscaled offset, as moving the canvas zoomed is a scaled move
			Viewport.OFFSET_X += (mouseX - newMouseX) / Viewport.SCALE;
			Viewport.OFFSET_Y += (mouseY - newMouseY) / Viewport.SCALE;

			// update last known position
			mouseX = newMouseX;
			mouseY = newMouseY;
		}
	}

	/**
	 * State for creating and highlighting selections.
	 * 
	 * @author Daniel Kotyk
	 * 
	 */
	class CreateState implements CanvasState {
		// tracks the last known mouse position
		private int mouseX, mouseY;
		
		/**
		 * Marks a shape as to be selected, and update the mouse offset inside
		 * the shape for dragging.
		 */
		@Override
		public void onMouseDown(MouseContext mouseCtx, MouseDownEvent evt) {
			if (selection == null) {
				// save the mouse position
				mouseX = evt.getRelativeX(canvas.getElement());
				mouseY = evt.getRelativeY(canvas.getElement());
			} else {
				// highlight the found selection and end the iteration
				saveState();

				// offset for drag moves
				int mouseX = evt.getRelativeX(canvas.getElement());
				int mouseY = evt.getRelativeY(canvas.getElement());
				mouseCtx.updateOffset(mouseX, mouseY, selection);

				// scheduling implicit cancels any executing timer
				dndTimer.scheduleRepeating(FRAMERATE);

				// while mousedown, the figure may be dragged
				mouseCtx.setState(STATE_DRAG);
			}
		}
		
		public void createNew() {
			Drawable drawable = new Rectangle(mouseX, mouseY);
			canvasDataProvider.add(drawable);

			// always redraw as either new states have been created or shapes
			// have been moved
			saveAndRedraw();
		}

		@Override
		public void onMouseMove(MouseContext manager, MouseMoveEvent evt) {}
		
		@Override
		public void onMouseUp(MouseContext manager, MouseUpEvent evt) {}
	}

	/**
	 * State for dragging any shapes.
	 * 
	 * @author Daniel Kotyk
	 * 
	 */
	class DragState implements CanvasState {
		@Override
		public void onMouseDown(MouseContext mouseCtx, MouseDownEvent evt) {
		}

		/**
		 * On finish dragging, return to initial state.
		 */
		@Override
		public void onMouseUp(MouseContext mouseCtx, MouseUpEvent evt) {
			// correct cp if over a connected rectangle
			if (selection instanceof Rectangle) {
				for (Line line : ((Rectangle) selection).getConnections()) {
					line.updateControlPoint();
				}
			}

			// stop the dragndrop
			dndTimer.cancel();
			mouseCtx.setState(STATE_CREATE);
			CanvasArea.DRAW_STATE = DrawStates.DRAW_SELECT;

			// always redraw as either new states have been created or shapes
			// have been moved
			saveAndRedraw();
		}

		/**
		 * Updates the dragged shape to simulated object moving.
		 */
		@Override
		public void onMouseMove(MouseContext mouseCtx, MouseMoveEvent evt) {
			if (selection == null) {
				return;
			}
			
			int mouseX = evt.getRelativeX(canvas.getElement());
			int mouseY = evt.getRelativeY(canvas.getElement());

			if (selection instanceof Rectangle) {
				// absolute distance that the rectangle is moved
				double dx = selection.getPosStart().getX()
						- (mouseX - mouseCtx.getOffsetX());
				double dy = selection.getPosStart().getY()
						- (mouseY - mouseCtx.getOffsetY());

				// move the rectangle
				selection.moveTo(mouseX - mouseCtx.getOffsetX(), mouseY
						- mouseCtx.getOffsetY());

				// update the controlpoint for the bezier lines
				for (Line line : ((Rectangle) selection).getConnections()) {
					Point cp = line.getControlPoint();
					// both divided by 2 as the cp should not be moved TO the
					// new coordinates, but only in this direction
					cp.moveBy(-dx / 2, -dy / 2);
				}
			} else {
				// move the line
				selection.moveTo(mouseX, mouseY);
			}
		}
	}

	/**
	 * State for drawing the arrow lines.
	 * 
	 * @author Daniel Kotyk
	 * 
	 */
	class ArrowState implements CanvasState {
		// a dynamic line following the cursor arrow tip while moving the mouse
		// and holding SHIFT down
		private Line dynamicLine;

		/**
		 * Creates a static connection between two rectangles.
		 */
		@Override
		public void onMouseDown(MouseContext mouseCtx, MouseDownEvent evt) {
			//save the actual state
			refresh();
			
			//show line moving along with the mouse pointer
			if (dynamicLine == null && selection != null && selection instanceof Rectangle) {
				int mouseX = evt.getRelativeX(canvas.getElement());
				int mouseY = evt.getRelativeY(canvas.getElement());
				
				// create temporary rectangle so that a dynamic line can be
				// drawn between this and the current selection
				Rectangle transparent = new Rectangle(mouseX, mouseY);
				// make the rectangle of size 0, as it should not be shown and
				// only serves as connection endpoint for the line
				transparent.getPosEnd().moveTo(
						transparent.getPosStart().getX(),
						transparent.getPosStart().getY());

				// add the line to the drawing list; dynamic line should never show text
				dynamicLine = ((Rectangle) selection).connect(transparent, false);
				canvasDataProvider.add(dynamicLine);
				
				// arrowed lines should only be drawn if a rectangle is selected
				dndTimer.scheduleRepeating(FRAMERATE);
			}
		}

		@Override
		public void onMouseUp(MouseContext mouseCtx, MouseUpEvent evt) {
			int mouseX = evt.getRelativeX(canvas.getElement());
			int mouseY = evt.getRelativeY(canvas.getElement());
			findSelection(mouseX, mouseY);
			
			// draw connection line between two states
			if (lastSelection != null && selection != null
					&& lastSelection instanceof Rectangle
					&& selection instanceof Rectangle) {
				createConnection((Rectangle) lastSelection, (Rectangle) selection);
			}

			//always clear a the dynamic line, so that new connections can be initiated on the next click
			clearDynamicLine();
		}

		/**
		 * Shows a dynamic arrow line
		 */
		@Override
		public void onMouseMove(MouseContext mouseCtx, MouseMoveEvent evt) {
			if (dynamicLine != null) {
				int mouseX = evt.getRelativeX(canvas.getElement());
				int mouseY = evt.getRelativeY(canvas.getElement());
	
				// if line already exists (ie is shown at the cursor position),
				// update the position of the line accordingly to let it move with the cursor.
				dynamicLine.connectedTo().moveTo(mouseX, mouseY);
				dynamicLine.center();
			}
		}

		/**
		 * Clears the line after Key.SHIFT is released or static line is created
		 * by click.
		 */
		public void clearDynamicLine() {
			if (dynamicLine != null) {
				canvasDataProvider.remove(dynamicLine);
			}
			this.dynamicLine = null;

			dndTimer.cancel();
			saveAndRedraw();
		}

		public Drawable getDynamicLine() {
			return dynamicLine;
		}
	}
	
	
	/**
	 * Drag'n'drop timer for refreshing the whole canvas while dragging a
	 * figure.
	 */
	private Timer dndTimer = new Timer() {
		@Override
		public void run() {
			repaintSelected();
		}
	};

	/**
	 * Timer for moving the canvas viewport while left-click-dragging.
	 */
	private Timer viewportTimer = new Timer() {
		@Override
		public void run() {
			refresh();
		}
	};
	
	// ------------------------------ Object Editing ------------------------- //

	
	/**
	 * Shows the edit popup for the current selection.
	 */
	@Override
	public void editDrawable(Drawable drawable, int left, int top) {
		if (drawable == null) {
			return;
		}
		
		IsInputProvider inputs = drawable.getContextMenu();
		if (drawable.getFigure() instanceof State) {
			((EditStatePanel) inputs).setCanvasCallback(this);
		}
		DialogBox dialog = new EditorDialog(inputs);
		dialog.setText("Edit " + ClassNameResolver.getSimpleName(drawable.getFigure()) + ": " + drawable.getFigure().getText());
		
		// update the drawable on close
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				saveAndRedraw();
				updateSpreadsheet();
			}
		});
		
		PopupHelper.show(dialog, left, top);
	}

	/**
	 * Combines a refresh by saving whole canvas to the buffer and repaint
	 * afterwards.
	 */
	private void saveAndRedraw() {
		saveState();
		repaintSelected();
	}

	/**
	 * As removing is not possible without prior selecting an element, and on
	 * selection the image w/o the selection is saved, removing just means
	 * displaying the image without the selection. Thus it is enough to just
	 * remove the selected object from the figures, and repaint the image.
	 * 
	 * @param selection the figure to be removed
	 */
	public void remove(Drawable drawable) {
		if (drawable != null) {

			Visitor<ValidationError> visitor = new DeleteValidationService();
			ValidationError error = drawable.accept(visitor);
			// stop deletion on error
			if (error.hasErrors()) {
				new NotifierPopup(error.getMessage(), NotifierState.ERROR);
				return;
			}

			canvasDataProvider.remove(drawable);
			selection = null;
			repaintSelected();
		}
	}

	/**
	 * Save the current front canvas to the back buffer, without the selected
	 * element so that only the selection is always drawn dynamically, and the
	 * rest remains a static image. This makes performance much better.
	 */
	private void saveState() {
		// refreshes the active context by drawing all unselected figures.
		bufferUnselected();
		drawHandler.moveToFront(selection, canvasDataProvider.getDrawables());
	}


	// ------------------------ Canvas Handling -------------------- // 

	/**
	 * Save the front canvas to canvas buffer. Used by various classes.
	 */
	public void bufferUnselected() {
		drawHandler.bufferUnselected(selection, canvasDataProvider.getDrawables());
	}

	/**
	 * Delegates to refresh the canvas drawings.
	 */
	public void repaintSelected() {
		drawHandler.repaint(canvas.getContext2d(), selection);
	}

	/**
	 * Delegate to refresh the whole drawing.
	 */
	@Override
	public void refresh() {
		saveAndRedraw();
	}

	/**
	 * As Spreadsheet update should not occure on every redraw, only on special
	 * edits, this needs an own function.
	 */
	private void updateSpreadsheet() {
		// only refresh outputs as inputs to not depend on state names
		Cache.get().getOutputPanel().refresh();
	}

	/**
	 * Changes the initial state by deleting all present and setting the new
	 * state.
	 */
	@Override
	public void changeInitialState(State state, boolean initial) {
		// if a state should be set initial, then all other states have to be
		// cleared
		if (initial) {
			for (Rectangle rectangle : canvasDataProvider.getRectangles()) {
				rectangle.getFigure().setInitial(false);
			}
		}

		state.setInitial(initial);
		saveAndRedraw();
	}

	//tracks if the canvas should show or hide all condition text of transitions
	private boolean showConditionText = false;
	
	@Override
	public void showLineConditions(boolean showAllConditions) {
		this.showConditionText = showAllConditions;
		
		for (Line line : canvasDataProvider.getLines()) {
			line.getFigure().setShowText(showAllConditions);
		}
		saveAndRedraw();
	}
	
	/**
	 * Creates a connection line between both figures
	 * 
	 * @param from
	 * @param to
	 */
	private void createConnection(Rectangle from, Rectangle to) {
		//prevent self transitions
		if (from.equals(to)) {
			return;
		}
		
		// prevent double transitions from one state to another. each transition
		// must be unique regarding connecting states.
		for (Line transition : from.getConnections()) {
			if (transition.connectedTo().equals(to)) {
				return;
			}
		}

		Line transition = from.connect(to, showConditionText);
		canvasDataProvider.add(transition);
	}

	@Override
	public void reset() {
		canvasDataProvider = new CanvasDataProvider();
	}
	
	@Override
	public HandlerRegistration addContextMenuHandler(ContextMenuHandler handler) {
		return addDomHandler(handler, ContextMenuEvent.getType());
	}

	
	// getter + setter
	public Canvas getCanvas() { return canvas; }
	public void setCanvas(Canvas canvas) { this.canvas = canvas; }
	public Drawable getSelection() { return selection; }
	public void setSelection(Drawable selection) { this.selection = selection; }
	public AbsolutePanel getWrapperPanel() { return absolutePanel; }
	public CanvasDataProvider getCanvasDataProdiver() { return canvasDataProvider; }
	public ViewportState getStateViewport() { return STATE_VIEWPORT; }
}