package edu.boisestate.cs471.util.interfaces;

import edu.boisestate.cs471.model.Model;

public interface IViewUpdateListener {

    /**
     * Callback indicating the commonly changing text has changed. These values are:
     * <ul><li>The isSorted message, {@link Model#getSortedMessage()}</li>
     * <li>The name of the currently selected sorting algorithm</li>
     * <li>The label for the number of iterations performed.</li>
     * </ul>
     */
    void onDynamicTextChanged();

    /**
     * Callback indicating the localized language was updated, meaning all text needs to be updated.
     * @param newLanguage The new localized language.
     */
    void onLocalizationChanged(String newLanguage);

    /**
     * Callback indicating the enable/disable state of the animation buttons has changed.
     */
    void onButtonsChanged();

    /**
     * Callback indicating that the number of data samples being sorted has changed.
     * @param newSize The new number of data samples for each sorting algorithm.
     */
    void onSortDataRangeChanged(int newSize);

    /**
     * Callback indicating that the sorting data for the currently selected sorting algorithm has been updated.
     * @see Model#getDataValues()
     * @see Model#getDataColors()
     */
    void onSortDataUpdated();
}
