package edu.boisestate.cs471.model;

public class InsertionSort extends SortingAlgorithm {

    private int mOuterLoopIndex;
    private int mInnerLoopIndex;
    private int mIndex;
    private static final int UNINITIALIZED = -41;

    /**
     * Instantiate a bubble sort instance, starting with a given sample size.
     * @param sampleSize The number of samples to use in the sorting data.
     */
    public InsertionSort(final int sampleSize) {
        super(sampleSize);
        onReset();
    }

    @Override
    public final String getName(final String language) {
        switch (language) {
            case "Spanish":
                return "Ordenamiento por inserción";
            case "english":
            default:
                return "Insertion Sort";
        }
    }

    @Override
    public final boolean doSortIteration() {
        boolean retValue = false;

        if (mOuterLoopIndex < mData.length) {
            if (mInnerLoopIndex == UNINITIALIZED) {
                mIndex = mData[mOuterLoopIndex];
                mColors[mOuterLoopIndex] = COLOR_COMARED;
                mInnerLoopIndex = mOuterLoopIndex;
            }

            if (mInnerLoopIndex > 0 && mData[mInnerLoopIndex - 1] > mIndex) {
                for (int i = 0; i < mOuterLoopIndex; i++) {
                    mColors[i] = COLOR_SORTED;
                }

                mData[mInnerLoopIndex] = mData[mInnerLoopIndex - 1];
                mColors[mInnerLoopIndex] = COLOR_SWAPPED;
                mInnerLoopIndex--;
            }
            else {
                mData[mInnerLoopIndex] = mIndex;
                mColors[mInnerLoopIndex] = COLOR_SORTED;
                mColors[mOuterLoopIndex] = COLOR_COMARED;

                mOuterLoopIndex++;
                mInnerLoopIndex = UNINITIALIZED;
            }
        }
        else {
            for (int i = 0; i < mData.length; i++) {
                mColors[i] = COLOR_SORTED;
            }
            retValue = true;
        }
        return retValue;
    }

    @Override
    protected final void onReset() {
        resetColors();
        mIndex = 0;
        mOuterLoopIndex = 1;
        mInnerLoopIndex = UNINITIALIZED;
    }
}
