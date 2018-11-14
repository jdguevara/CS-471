package edu.boisestate.cs471.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import edu.boisestate.cs471.util.interfaces.ISelectionListener;

/**
 * The model portion of the Model-View-Controller.
 */
public final class Model {
    /** All available sorting algorithms. */
    private final List<SortingAlgorithm> mAlgorithms;
    /** The list index of the currently selected algorithm. */
    private int mSelectedIndex = 0;
    /** A listener to notify if the selected algorithm changes. */
    private ISelectionListener mSelectionListener = null;
    /** The number of data samples to use for each sorting algorithm. */
    private int mSampleSize = 25;
    /** The current translation language. */
    private String mLanguage = "english";

    /**
     * Default constructor.
     */
    public Model() {
        // Instantiate all sorting algorithms.
        mAlgorithms = new ArrayList<>();
        mAlgorithms.add(new BubbleSort(mSampleSize));
        mAlgorithms.add(new SelectionSort(mSampleSize));
        mAlgorithms.add(new InsertionSort(mSampleSize));
    }

    /**
     * Helper function to get the currently selected sorting algorithm instance.
     * @return The instance from the {@link Model#mAlgorithms} list.
     */
    private SortingAlgorithm getSelectedAlgorithm() {
        return mAlgorithms.get(mSelectedIndex);
    }

    /**
     * Set the listener that will be notified if the algorithm selection changes. If a listener was previously set,
     * calling this method will replace the previous listener.
     *
     * @param listener A listener to be notified when a different algorithm becomes selected. The listener is
     * immediately notified of a change from null to the currently selected algorithm.
     */
    public void setSelectionListener(final ISelectionListener listener) {
        mSelectionListener = listener;
        listener.onSelectionChanged(null, getSelectedAlgorithm());
    }

    /**
     * Change the selected algorithm.
     *
     * @param index The index to select from the algorithm list.
     */
    public void selectIndex(final int index) {
        if (index == mSelectedIndex) {
            return;
        }
        if (index < 0 || index >= mAlgorithms.size()) {
            throw new IndexOutOfBoundsException();
        }
        selectIndexUnchecked(index);
    }

    /**
     * Select the next sorting algorithm. If the last algorithm is selected, this will wrap around to select the first
     * one.
     */
    public void selectNext() {
        if (mSelectedIndex + 1 >= mAlgorithms.size()) {
            // Wrap around
            selectIndexUnchecked(0);
        }
        else {
            selectIndexUnchecked(mSelectedIndex + 1);
        }
    }

    /**
     * Select the previous sorting algorithm. If the first algorithm is selected, this will wrap around to select the
     * last one.
     */
    public void selectPrevious() {
        if (0 == mSelectedIndex) {
            // Wrap around
            selectIndexUnchecked(mAlgorithms.size() - 1);
        }
        else {
            selectIndexUnchecked(mSelectedIndex - 1);
        }
    }

    /**
     * Update the selected index without performing any index-out-of-bounds or wrapping checks. This will notify the
     * {@link ISelectionListener} that was registered by {@link #setSelectionListener(ISelectionListener)}, if there
     * is one.
     *
     * @param index The new index to select.
     */
    private void selectIndexUnchecked(final int index) {
        SortingAlgorithm oldAlg = getSelectedAlgorithm();
        oldAlg.stopAnimation();
        mSelectedIndex = index;
        if (null != mSelectionListener) {
            mSelectionListener.onSelectionChanged(oldAlg, getSelectedAlgorithm());
        }
    }

    /**
     * Randomize the sample data for the currently selected algorithm.
     */
    public void randomize() {
        getSelectedAlgorithm().randomize();
    }

    /**
     * Perform a single iteration of the currently selected sorting algorithm.
     */
    public void iterateSort() {
        getSelectedAlgorithm().iterateSort();
    }

    /**
     * Start the sorting loop, continuing until the sample data is sorted or {@link #stopAnimation()},
     * {@link #randomize()}, or {@link #updateSampleSize(int)} is called.
     */
    public void startAnimation() {
        getSelectedAlgorithm().startAnimation();
    }

    /**
     * Stop the sorting loop. If the animation was not currently running, this is silently ignored.
     */
    public void stopAnimation() {
        getSelectedAlgorithm().stopAnimation();
    }

    /**
     * Change the number of samples being sorted by each algorithm. This has the side effect of randomizing all of the
     * datasets.
     * @param newSize The number of samples to use for each sorting algorithm.
     */
    public void updateSampleSize(final int newSize) {
        mSampleSize = newSize;
        for (SortingAlgorithm alg : mAlgorithms) {
            alg.randomize(mSampleSize);
        }
    }

    /**
     * Set the language used for returning text strings, such as {@link #getSortedMessage()}.
     * @param language The display language.
     */
    public void updateLanguage(final String language) {
        mLanguage = language;
    }

    // ==== Methods used by the View to render the model. ====

    /**
     * Get an index of which algorithm is currently supported, which is needed for the dropdown combo box.
     * @return The index of the currently selected algorithm.
     */
    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    /**
     * Get the display name for all sorting algorithm options.
     * @return An array of algorithm display names.
     */
    public String[] getAllAlgorithmNames() {
        final Stream<String> map = mAlgorithms.stream().map(alg -> alg.getName(mLanguage));
        return map.toArray(String[]::new);
    }

    /**
     * Get the data being sorted by the current sorting algorithm.
     * @return An array of numbers ranging from 1 to N, where N is the length of the array.
     */
    public int[] getDataValues() {
        return getSelectedAlgorithm().getData();
    }
    /**
     * Get the color to display each data value from {@link #getDataValues()}.
     * @return An array of colors of the same length as {@link #getDataValues()}.
     */
    public Color[] getDataColors() {
        return getSelectedAlgorithm().getColors();
    }
    /**
     * Get a human readable message indicating if the data is sorted, and if the sorting was only done by the animation,
     * how long it took to do so.
     * @return A human readable string.
     */
    public String getSortedMessage() {
        return getSelectedAlgorithm().getSortedMessage(mLanguage);
    }
    /**
     * Get the number of iterations that have been performed.
     * @return The number of iterations performed.
     */
    public int getIterationCount() {
        return getSelectedAlgorithm().getIterationCount();
    }

    /**
     * Get the human readable language currently in use, such as English or Spanish.
     * @return The name of the current language.
     */
    public String getCurrentLanguage() {
        return mLanguage;
    }

    /**
     * Check if the play button should be enabled.
     * @return True if the play button should be enabled.
     */
    public boolean isPlayEnabled() {
        SortingAlgorithm selected = getSelectedAlgorithm();
        return (!selected.isAnimating() && !selected.isSorted());
    }

    /**
     * Check if the pause button should be enabled.
     * @return True if the pause button should be enabled.
     */
    public boolean isPauseEnabled() {
        SortingAlgorithm selected = getSelectedAlgorithm();
        return selected.isAnimating();
    }

    /**
     * Check if the iterate button should be enabled.
     * @return True if the iterate button should be enabled.
     */
    public boolean isIterateEnabled() {
        SortingAlgorithm selected = getSelectedAlgorithm();
        return (!selected.isAnimating() && !selected.isSorted());
    }
}
