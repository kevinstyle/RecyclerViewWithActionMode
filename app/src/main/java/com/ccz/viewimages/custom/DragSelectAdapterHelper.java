/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ccz.viewimages.custom;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;

import com.ccz.viewimages.dragselect.DragSelectRecyclerViewAdapter;


public class DragSelectAdapterHelper extends DragSelectAdapterHelperBase {

    private AppCompatActivity mActivity;
    private ActionMode        mActionMode;

    public DragSelectAdapterHelper(DragSelectRecyclerViewAdapter owner) {
        super(owner);
    }

    @Override
    protected void startActionMode() {

        Log.e("actionmode", "startActionMode");

        if (!(owner instanceof ActionMode.Callback)) {
            throw new IllegalStateException("Adapter must implement ActionMode.Callback");
        }

        mActivity = (AppCompatActivity) owner.getContext();
        setActionMode(mActivity.startSupportActionMode((ActionMode.Callback) owner));
    }

    // Back button will trigger this function
    @Override
    public void finishActionMode() {
        if (mActionMode != null) {
            Log.e("actionmode", "finishActionMode");
            mActionMode.finish();
            clearActionMode();
        }
    }

    // Left arrow will trigger this function
    @Override
    protected void clearActionMode() {
        Log.e("actionmode", "clearActionMode");
        mActionMode = null;
        owner.clearSelected();
    }

    @Override
    protected void setActionModeTitle(String title) {
        mActionMode.setTitle(title);
    }

    @Override
    protected boolean isActionModeStarted() {
        return mActionMode != null;
    }

    public void onStartActionMode() {
        startActionMode();
        owner.notifyDataSetChanged();
    }

    public void onDestroyActionMode() {
        finishActionMode();
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setActionMode(ActionMode mode) {
        mActionMode = mode;
    }

    public boolean isInActionMode() {
        return mActionMode != null;
    }
}
