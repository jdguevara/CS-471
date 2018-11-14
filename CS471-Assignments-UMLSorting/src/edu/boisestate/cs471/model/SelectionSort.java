package edu.boisestate.cs471.model;

public class SelectionSort extends SortingAlgorithm {

    private int mOuterLoopIndex;
    private int mInnerLoopIndex;
    private int mMinimumUnsortedIndex;

    /**
     * Instantiate a selection sort instance, starting with a given sample size.
     * @param sampleSize The number of samples to use in the sorting data.
     */
    public SelectionSort(final int sampleSize) {
        super(sampleSize);
        onReset();
    }

    @Override
    public final String getName(final String language) {
        switch (language) {
            case "Spanish":
                return "Ordenamiento por selección";
            case "english":
            default:
                return "Selection Sort";
        }
    }

    @Override
    protected boolean doSortIteration() {
        resetColors();
        for (int i = 0; i < mOuterLoopIndex; i++) {
            mColors[i] = COLOR_SORTED;
        }
        if (mOuterLoopIndex < mData.length - 1) {
            if (mInnerLoopIndex == 0) {
                mMinimumUnsortedIndex = mOuterLoopIndex;
                mInnerLoopIndex = mOuterLoopIndex + 1;
            }

            if (mInnerLoopIndex < mData.length) {
                mColors[mInnerLoopIndex] = COLOR_COMARED;
                mColors[mMinimumUnsortedIndex] = COLOR_COMARED;
                if (mData[mInnerLoopIndex] < mData[mMinimumUnsortedIndex]) {
                    mMinimumUnsortedIndex = mInnerLoopIndex;
                }
                mInnerLoopIndex++;
            }
            else {
                final int temp = mData[mMinimumUnsortedIndex];
                mData[mMinimumUnsortedIndex] = mData[mOuterLoopIndex];
                mData[mOuterLoopIndex] = temp;
                mColors[mMinimumUnsortedIndex] = COLOR_SWAPPED;
                mColors[mOuterLoopIndex] = COLOR_SWAPPED;

                mInnerLoopIndex = 0;
                mOuterLoopIndex++;
            }
        }
        else {
            mColors[mOuterLoopIndex] = COLOR_SORTED;
            return true;
        }
        return false;
    }

    @Override
    protected final void onReset() {
        mOuterLoopIndex = 0;
        mInnerLoopIndex = 0;
        mMinimumUnsortedIndex = 0;
    }

}
