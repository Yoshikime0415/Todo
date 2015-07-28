package com.example.yoshiki.todo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by Yoshiki on 2015/07/16.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean _isChecked;
    private static final int[] CHECKED_STATE = { android.R.attr.state_checked };

    public CheckableLinearLayout(Context context) {
        super(context);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChecked(boolean checked) {
        if(_isChecked != checked) {
            _isChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return _isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!_isChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if(_isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE);
        }

        return drawableState;
    }

}
