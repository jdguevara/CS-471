package edu.boisestate.cs471.util.interfaces;

import edu.boisestate.cs471.model.SortingAlgorithm;

/**
 * Listen to changes of which algorithm is currently selected.
 */
public interface ISelectionListener {

    /**
     * The selected algorithm has changed.
     * @param oldItem The previously selected sorting algorithm.
     * @param newItem The newly selected sorting algorithm.
     */
    void onSelectionChanged(SortingAlgorithm oldItem, SortingAlgorithm newItem);
}
