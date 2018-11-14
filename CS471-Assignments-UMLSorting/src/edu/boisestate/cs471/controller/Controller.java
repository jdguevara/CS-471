package edu.boisestate.cs471.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import edu.boisestate.cs471.model.Model;
import edu.boisestate.cs471.model.SortingAlgorithm;
import edu.boisestate.cs471.util.UserCommand;
import edu.boisestate.cs471.util.interfaces.ISelectionListener;
import edu.boisestate.cs471.util.interfaces.ISortListener;
import edu.boisestate.cs471.util.interfaces.IViewUpdateListener;

/**
 * The Controller portion of a Model-View-Controller architecture.
 *
 * <p>This implements {@link ActionListener} so that the View can forward user input to the controller for processing.
 * The controller then notifies the View about any updates via a {@link IViewUpdateListener} that was registered by the
 * view using {@link #registerListener(IViewUpdateListener)}.</p>
 *
 * <p>This controller holds a reference to the Model ({@link Model}) of the Model-View-Controller. It manipulates the
 * model but also listens for changes. This controller translates model change events into the appropriate view update
 * events. It uses an {@link ISelectionListener} to listen for when the model considers a different sorting algorithm
 * instance to be the 'selected' instance, and an {@link ISortListener} to listen to changes in the selected algorithm's
 * sorting data that may need to be relayed on to the View.</p>
 */
public class Controller implements ISelectionListener, ISortListener, ActionListener {
    /** The Model of this model-view-controller. */
    private final Model mModel;
    /** A listener registered by the View to know when to redraw. */
    private IViewUpdateListener mViewUpdateListener;

    /**
     * Instantiate a controller for manipulating a given model.
     * @param model The model this controller may manipulate.
     */
    public Controller(final Model model) {
        mModel = model;
        mModel.setSelectionListener(this);
    }

    /**
     * Get a reference to the Model.
     * @return The model of the model-view-controller.
     */
    public final Model getModel() {
        return mModel;
    }
    /**
     * Register for updates that should result in updating the view presented to the user.
     * @param listener The callback listener.
     */
    public final void registerListener(final IViewUpdateListener listener) {
        mViewUpdateListener = listener;
        listener.onSortDataRangeChanged(25);
        listener.onSortDataUpdated();
        listener.onDynamicTextChanged();

    }

    /**
     * Handle the user requesting a different sample size.
     * @param newSize The new sample size.
     */
    public void updateSampleSize(final int newSize) {
        mModel.updateSampleSize(newSize);
    }
    /**
     * Update the display language.
     * @param language The new language, such as "Spanish".
     */
    public void updateLanguage(final String language) {
        mModel.updateLanguage(language);
        mViewUpdateListener.onLocalizationChanged(language);
    }


    // ==== Methods from ISelectionListener ====
    @Override
    public void onSelectionChanged(final SortingAlgorithm oldItem, final SortingAlgorithm newItem) {
        if (null != oldItem) {
            // Old item will be NULL on first call.
            oldItem.unregisterListener(this);
        }
        newItem.registerListener(this);
        if (null != mViewUpdateListener) {
            mViewUpdateListener.onButtonsChanged();
            mViewUpdateListener.onDynamicTextChanged();
            mViewUpdateListener.onSortDataUpdated();
        }

    }

    // ==== Methods from ISortListener ====
    @Override
    public void onButtonStateChange(final SortingAlgorithm algorithmInstance) {
        if (null != mViewUpdateListener) {
            mViewUpdateListener.onButtonsChanged();
        }
    }

    @Override
    public void onDataChanged(final SortingAlgorithm algorithmInstance) {
        if (null != mViewUpdateListener) {
            mViewUpdateListener.onSortDataUpdated();
        }
    }

    @Override
    public void onDataSizeChanged(final SortingAlgorithm algorithmInstance, final int newSize) {
        if (null != mViewUpdateListener) {
            mViewUpdateListener.onSortDataRangeChanged(newSize);
        }
    }

    @Override
    public void onSortStatusChanged(final SortingAlgorithm algorithmInstance, final boolean isSorted) {
        if (null != mViewUpdateListener) {
            mViewUpdateListener.onButtonsChanged();
            mViewUpdateListener.onDynamicTextChanged();
        }
    }

    // Handle user input forwarded from View

    @Override
    public void actionPerformed(final ActionEvent e) {
        String command = e.getActionCommand();
        UserCommand asEnum;
        try {
            asEnum = UserCommand.valueOf(command);
        }
        catch (IllegalArgumentException ex) {
            System.out.println("Warning: Ignoring unexpected command value " + e.getActionCommand());
            return;
        }

        switch (asEnum) {
            case PLAY:
                mModel.startAnimation();
                break;
            case PAUSE:
                mModel.stopAnimation();
                break;
            case ITERATE:
                mModel.iterateSort();
                break;
            case RANDOMIZE:
                mModel.randomize();
                break;
            case SELECT_NEXT:
                mModel.selectNext();
                break;
            case SELECT_PREVIOUS:
                mModel.selectPrevious();
                break;
            case SELECT_COMBO_BOX:
                JComboBox<?> combo;
                try {
                    combo = (JComboBox<?>) e.getSource();
                }
                catch (ClassCastException ex) {
                    System.out.println("Received a " + UserCommand.SELECT_COMBO_BOX +
                            " command from something other than a combo box. Ignoring command.");
                    break;
                }
                int newIndex = combo.getSelectedIndex();
                if (newIndex != mModel.getSelectedIndex()) {
                    mModel.selectIndex(newIndex);
                }
                break;
            default:
                System.out.println("Unhandled command type " + asEnum);
                break;
        }
    }
}
