package edu.boisestate.cs471.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

/**
 * A visualization of the data being sorted by a sorting algorithm.
 */
public class Visualizer extends JPanel {
    private static final long serialVersionUID = 1332933685097802005L;
    private static final int BAR_GAP = 1;
    private int[] mData;
    private Color[] mColors;
    private final Object mDataLock = new Object();

    private double mBarWidth = 0;
    private int mVisualizerHeight = 0;
    private int mVisualizerWidth = 0;
    private int mMaxValue = 0;
    private double mDataMultiplier;

    /**
     * Default constructor.
     */
    public Visualizer() {
        setDoubleBuffered(true);
        processSize();
        setDataRange(0);
    }

    /**
     * Update the number of data samples being sorted.
     * @param sampleCount How many samples are being sorted.
     */
    public final void setDataRange(final int sampleCount) {
        synchronized (mDataLock) {
            mData = new int[sampleCount];
            mColors = new Color[sampleCount];
            computeBarWidth();
            mMaxValue = sampleCount;
            if (mMaxValue > 0) {
                mDataMultiplier = ((double) mVisualizerHeight) / mMaxValue;
            }
            else {
                mDataMultiplier = 0;
            }
        }
    }

    /**
     * Update the values that are being sorted, and their colors.
     * @param sampleValues An array of data to plot.
     * @param colorValues The color to render each data point.
     */
    public final void updateData(final int[] sampleValues, final Color[] colorValues) {
        synchronized (mDataLock) {
            if (sampleValues.length != mData.length) {
                throw new IllegalArgumentException(
                        "Invalid length of data samples. Expected " + mData.length + " but got " + sampleValues.length);
            }
            mData = sampleValues;
            mColors = colorValues;
        }
    }

    @Override
    protected final void paintComponent(final Graphics g) {
        super.paintComponent(g);

        synchronized (mDataLock) {
            processSize();

            double x = (BAR_GAP / 2);
            int y;
            int height = mVisualizerHeight;
            if (mData.length > 0) {
                height = mVisualizerHeight / mData.length;
            }
            for (int i = 0; i < mData.length; i++) {
                y = mVisualizerHeight - (int) (mData[i] * mDataMultiplier);
                g.setColor(mColors[i]);
                g.fillRect((int) x, y, (int) mBarWidth, height);
                x += mBarWidth + BAR_GAP;
            }
        }
    }

    /**
     * Update rendering variables based on a new size of this widget.
     */
    private void processSize() {
        final Rectangle bounds = getBounds();
        if (mVisualizerHeight != (int) bounds.getHeight()) {
            mVisualizerHeight = (int) bounds.getHeight();
            mDataMultiplier = ((double) mVisualizerHeight) / mMaxValue;
        }

        if (mVisualizerWidth != (int) bounds.getWidth()) {
            mVisualizerWidth = (int) bounds.getWidth();
            computeBarWidth();
        }

    }

    /**
     * Determine the width of each bar on the graph.
     */
    private void computeBarWidth() {
        if (null == mData || 0 == mData.length) {
            mBarWidth = mVisualizerWidth;
        }
        else {
            mBarWidth = (mVisualizerWidth * 1.0 / mData.length) - BAR_GAP;
        }
    }
}
