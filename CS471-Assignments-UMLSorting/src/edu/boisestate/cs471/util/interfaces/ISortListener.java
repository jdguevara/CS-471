package edu.boisestate.cs471.util.interfaces;

import edu.boisestate.cs471.model.SortingAlgorithm;

/**
 * Listen to changes in the state of a specific sorting algorithm.
 */
public interface ISortListener {
    /**
     * The buttons to control a sort should be updated, for instance if the animation has completed.
     *
     * @param algorithmInstance The instance of the algorithm that has changed state.
     */
    void onButtonStateChange(SortingAlgorithm algorithmInstance);

    /**
     * The values being sorted by the algorithm have been updated.
     *
     * @param algorithmInstance The instance of the algorithm that has modified the sample data.
     */
    void onDataChanged(SortingAlgorithm algorithmInstance);

    /**
     * The number of samples being sorted has changed.
     *
     * @param algorithmInstance The instance of the algorithm that has changed state.
     * @param newSize The new sample size being sorted by the algorithm.
     */
    void onDataSizeChanged(SortingAlgorithm algorithmInstance, int newSize);

    /**
     * The data being sorted by this algorithm has become sorted or become unsorted.
     *
     * @param algorithmInstance The instance of the algorithm that has changed state.
     * @param isSorted True if the data is now sorted, false if the previously sorted data is now unsorted.
     */
    void onSortStatusChanged(SortingAlgorithm algorithmInstance, boolean isSorted);
}
