package com.ccz.viewimages.custom;


import com.ccz.viewimages.dragselect.DragSelectRecyclerViewAdapter;

public abstract class DragSelectAdapterHelperBase {

    protected DragSelectRecyclerViewAdapter owner;

    protected DragSelectAdapterHelperBase(DragSelectRecyclerViewAdapter owner) {
        this.owner = owner;
    }

    public void onItemSelectedStateChanged() {

        if (!isActionModeStarted()) {
            startActionMode();
        }

        int count = owner.getSelectedCount();
        if (count == 0) {
            finishActionMode();
            return;
        }

        setActionModeTitle(String.valueOf(owner.getSelectedCount()));
    }

    protected abstract void startActionMode();

    protected abstract void setActionModeTitle(String title);

    protected abstract boolean isActionModeStarted();

    protected abstract void finishActionMode();

    protected abstract void clearActionMode();
}
