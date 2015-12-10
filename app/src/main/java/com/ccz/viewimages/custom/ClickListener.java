package com.ccz.viewimages.custom;

import android.widget.Checkable;

/**
 * Created by Kevin on 2015/12/9.
 */
public interface ClickListener {
    void onClick(int index);
    void onLongClick(int index);
    void onCheckChange(int index, Checkable v, boolean isChecked);
}
