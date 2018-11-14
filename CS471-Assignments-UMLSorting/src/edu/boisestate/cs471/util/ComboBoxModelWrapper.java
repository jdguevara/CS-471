package edu.boisestate.cs471.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ComboBoxModelWrapper<T> implements ComboBoxModel<T> {
    private int mSlectedIndex = 0;
    private T[] mItems = null;
    private final List<ListDataListener> mListeners = new ArrayList<>();

    /**
     * Wrap an array of combo box elements.
     * @param data The elements to wrap.
     */
    public ComboBoxModelWrapper(final T[] data) {
        mItems = data;
    }

    /**
     * Update the wrapped elements, triggering any listeners.
     * @param data The new data.
     */
    public final void setData(final T[] data) {
        if (mItems.length != data.length) {
            throw new IllegalArgumentException("Cannot add/remove");
        }
        mItems = data;
        final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, mItems.length - 1);
        for (final ListDataListener l : mListeners) {
            l.contentsChanged(event);
        }
    }

    @Override
    public final void addListDataListener(final ListDataListener l) {
        mListeners.add(l);
    }

    @Override
    public final T getElementAt(final int index) {
        return mItems[index];
    }

    @Override
    public final int getSize() {
        return mItems.length;
    }

    @Override
    public final void removeListDataListener(final ListDataListener l) {
        mListeners.remove(l);
    }

    @Override
    public final Object getSelectedItem() {
        return mItems[mSlectedIndex];
    }

    @Override
    public final void setSelectedItem(final Object item) {
        for (int i = 0; i < mItems.length; i++) {
            if (mItems[i].equals(item)) {
                mSlectedIndex = i;
                return;
            }
        }
        throw new IllegalArgumentException("Invalid item: " + item);
    }
}
