package edu.boisestate.cs471.model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;

import edu.boisestate.cs471.util.interfaces.ISortListener;

/**
 * A representation of a sorting algorithm, capable of actually sorting data.
 */
public abstract class SortingAlgorithm {
    /** The default color to use when displaying the sorting data. */
    private static final Color COLOR_DEFAULT = Color.RED;
    protected static final Color COLOR_SORTED = Color.BLUE;
    protected static final Color COLOR_COMARED = Color.YELLOW;
    protected static final Color COLOR_SWAPPED = Color.GREEN;
    /**
     * An array of numbers from 1 to N, where N is the length of the data (sample size). These values can and should
     * be directly modified by an implementation of this class.
     */
    protected int[] mData;
    /**
     * An array of color values to specify the color to render each sample from {@link #mData}. These values can and
     * should be directly modified by an implementation of this class.
     */
    protected Color[] mColors;
    /** The number of iterations performed so far. */
    private int mIterationCounter = 0;
    /** Flag indicating if the sample data is currently sorted. */
    private boolean mIsSorted = false;
    /** The time the animation started. A value of 0 means the value of {@link #mStopTime} is irrelevant. */
    private long mStartTime = 0;
    /** The time the animation stopped. */
    private long mStopTime = 0;
    /** Periodic timer for iterating through the sort if {@link #startAnimation()} is called. */
    private Timer mAnimation;
    /** Multi-thread lock to prevent race conditions with {@link #startAnimation()} and {@link #stopAnimation()}. */
    private final Object mAnimationLock = new Object();
    /** A collection of listeners that should be notified when this algorithm changes. */
    private List<ISortListener> mListeners = new ArrayList<>();

    /**
     * Instantiate a sorting algorithm with a given sample count.
     * @param sampleSize The number of items to create for the {@link #getData()} and {@link #getColors()} samples.
     */
    public SortingAlgorithm(final int sampleSize) {
        randomize(sampleSize);
    }

    /**
     * Get the display name for this sorting algorithm.
     * @param language The display language, e.g. English
     * @return The human-readable name of this algorithm.
     */
    public abstract String getName(String language);

    /**
     * A hook provided to children to reset any variables if the sample data has been modified without the child
     * knowing. For instance, if {@link #randomize()} was called.
     */
    protected abstract void onReset();

    /**
     * Perform a single iteration of the sorting algorithm on the sample data, {@link #mData}, optionally updating the
     * color values {@link #mColors}. If an implementation does modify the colors, it is encouraged to call
     * {@link #resetColors()} at the beginning of the method.
     * @return True if the data is now sorted.
     */
    protected abstract boolean doSortIteration();

    /**
     * Register a listener to be notified about changes to this algorithm.
     * @param listener The listener to register
     * @see #unregisterListener(ISortListener)
     * @see ISortListener
     */
    public void registerListener(final ISortListener listener) {
        mListeners.add(listener);
    }
    /**
     * Unregister a previously registered listener.
     * @param listener The listener to unregister
     * @see #registerListener(ISortListener)
     */
    public void unregisterListener(final ISortListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Get the integers being sorted by this algorithm.
     * @return An array of numbers from 1 to N, where N is the length of the array.
     */
    public final int[] getData() {
        return mData;
    }

    /**
     * Get an array of colors to use when rendering the data from {@link #getData()}.
     * @return An array of colors of the same length as {@link #getData()}.
     */
    public final Color[] getColors() {
        return mColors;
    }

    /**
     * Check if the data is currently sorted.
     * @return True if the sample data is sorted.
     */
    public final boolean isSorted() {
        return mIsSorted;
    }

    /**
     * Check if the algorithm is currently running.
     * @return True if the animation is running.
     */
    public final boolean isAnimating() {
        return (null != mAnimation);
    }

    /**
     * Get a human readable message indicating if the data is sorted, and if the sorting was only done by the animation,
     * how long it took to do so.
     * @param language The display language, such as English.
     * @return A human readable string.
     */
    public final String getSortedMessage(final String language) {
        if (!mIsSorted) {
            // Not sorted, return empty string
            return "";
        }
        // Sorted, and we know the duration it took to complete the sort
        if (0 != mStopTime && 0 != mStartTime) {
            switch (language) {
                case "Spanish":
                    return "Ordenados en " + getSortDurationMillis() + "ms";
                case "english":
                default:
                    return "Sorted in " + getSortDurationMillis() + "ms";
            }
        }
        // Sorted, but the time value is invalid (animation was paused, or manually iterated)
        switch (language) {
            case "Spanish":
                return "¡Ordenado!";
            case "english":
            default:
                return "Sorted!";
        }
    }

    /**
     * Start sorting the data.
     */
    public final void startAnimation() {
        synchronized (mAnimationLock) {
            if (null != mAnimation) {
                return;
            }
            mAnimation = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    iterateSort();
                    if (isSorted()) {
                        mStopTime = System.currentTimeMillis();
                        System.out.println("Sorting complete, stopping animation");
                        mAnimation.stop();
                        mAnimation = null;
                        signalButtonStateChanged();
                    }
                }
            });
            if (mIterationCounter == 0) {
                // Sort duration timer is only valid if it has not been manually iterated over.
                mStartTime = System.currentTimeMillis();
                mStopTime = 0;
            }
            mAnimation.start();
            signalButtonStateChanged();
        }
    }

    /**
     * Stop sorting the data.
     */
    public void stopAnimation() {
        synchronized (mAnimationLock) {
            if (null != mAnimation) {
                mAnimation.stop();
                // Invalidate sort duration timer
                mStartTime = 0;
                mStartTime = 0;
                mAnimation = null;
                signalButtonStateChanged();
            }
        }
    }

    /**
     * Determine how many milliseconds it took to sort the data. This is only valid if the sort was performed
     * exclusively by the animation.
     *
     * @return The sort duration in milliseconds if the sort has completed, the running time if it is still animated, or
     * 0 if the animation was not used.
     */
    public final long getSortDurationMillis() {
        if (mStartTime <= 0) {
            // Duration is not valid.
            return 0;
        }
        if (mStopTime <= 0) {
            // Still running
            return System.currentTimeMillis() - mStartTime;
        }
        return mStopTime - mStartTime;
    }

    /**
     * Perform a single iteration of the sort.
     */
    public final void iterateSort() {
        if (mIsSorted) {
            System.out.println("already sorted");
            return;
        }
        mIterationCounter++;
        setIsSorted(doSortIteration());
        signalDataChanged();
    }

    /**
     * Randomize the data being sorted by this algorithm.
     */
    public final void randomize() {
        randomize(mData.length);
    }

    /**
     * Randomize the data being sorted by this algorithm, using a possibly different number of data samples.
     *
     * @param sampleCount The number of samples to use in the new randomized data set.
     */
    public final void randomize(final int sampleCount) {
        // Stop the animation, if it is running
        stopAnimation();

        boolean lengthChanged = false;
        if ((null == mData) || (mData.length != sampleCount)) {
            // Create a set of colors to match the new sample size
            mColors = new Color[sampleCount];
            lengthChanged = true;
        }

        // Reset state
        setIsSorted(false);
        resetColors();
        mIterationCounter = 0;

        // Create a new random data set of sorting values
        final List<Integer> listData = new ArrayList<>();
        for (int i = 1; i <= sampleCount; i++) {
            listData.add(i);
        }
        Collections.shuffle(listData);
        mData = new int[sampleCount];
        for (int i = 0; i < listData.size(); i++) {
            mData[i] = listData.get(i);
        }

        // Reset any state in the child implementation
        onReset();

        // Signal listeners
        if (lengthChanged) {
            signalDataCountChanged();
        }
        signalDataChanged();
        signalButtonStateChanged();
    }

    /**
     * Get the number of iterations that have been performed, including manual iterations ({@link #iterateSort()}) and
     * animated iterations ({@link #startAnimation()}).
     * @return The number of iterations performed.
     */
    public int getIterationCount() {
        return mIterationCounter;
    }

    /**
     * Set all color values in {@link #mColors} to the default color, {@link #COLOR_DEFAULT}.
     */
    protected final void resetColors() {
        for (int i = 0; i < mColors.length; i++) {
            mColors[i] = COLOR_DEFAULT;
        }
    }

    /**
     * Update the isSorted flag and call any listeners if the value changed.
     * @param isSorted The new value, which may or may not be identical to the old value.
     */
    private void setIsSorted(final boolean isSorted) {
        if (isSorted != mIsSorted) {
            mStopTime = System.currentTimeMillis();
            mIsSorted = isSorted;
            mListeners.forEach(listener -> listener.onSortStatusChanged(this, mIsSorted));
        }
    }

    /**
     * Notify any listeners that the enable/disable state of the buttons should be updated.
     */
    private void signalButtonStateChanged() {
        mListeners.forEach(listener -> listener.onButtonStateChange(this));
    }

    /**
     * Notify any listeners that data in {@link #mData} and/or {@link #mColors} has changed.
     */
    private void signalDataChanged() {
        mListeners.forEach(listener -> listener.onDataChanged(this));
    }

    /**
     * Notify any listeners that the number of elements in {@link #mData} and {@link #mColors} has changed.
     */
    private void signalDataCountChanged() {
        mListeners.forEach(listener -> listener.onDataSizeChanged(this, mData.length));
    }
}
