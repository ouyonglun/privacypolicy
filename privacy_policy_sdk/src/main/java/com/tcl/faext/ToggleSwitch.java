/* MODIFIED-BEGIN by hui.zhu, 2017-09-05, BUG-5257378*/
/* Copyright (C) 2017 Tcl Corporation Limited */
package com.tcl.faext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Switch;

/**
 * Created by shaohua.li on 12/7/15.
 */
public class ToggleSwitch extends Switch {

    private OnBeforeCheckedChangeListener mOnBeforeListener;

    public static interface OnBeforeCheckedChangeListener {
        public boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean checked);
    }

    public ToggleSwitch(Context context) {
        super(context);
    }

    public ToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToggleSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener listener) {
        mOnBeforeListener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mOnBeforeListener != null
                && mOnBeforeListener.onBeforeCheckedChanged(this, checked)) {
            return;
        }
        super.setChecked(checked);
    }

    public void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
    }
}
/* MODIFIED-END by hui.zhu,BUG-5257378*/
