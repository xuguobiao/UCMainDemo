package com.kido.ucmaindemo.widget.main.behavior;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kido.ucmaindemo.utils.Logger;
import com.kido.ucmaindemo.widget.main.UcNewsBarLayout;
import com.kido.ucmaindemo.widget.main.helper.HeaderScrollingViewBehavior;

import java.util.List;

/**
 * Behavior for Bar Footer.
 * <p>
 * e.g. TabLayout
 *
 * @author Kido
 */

public class BarFooterBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "UNBL_FooterBehavior";

    public BarFooterBehavior() {
    }

    public BarFooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        super.layoutChild(parent, child, layoutDirection);
        Logger.d(TAG, "layoutChild-> top=%s, height=%s", child.getTop(), child.getHeight());
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        float childOffsetRange = dependency.getTop() + getFinalTopHeight(dependency) - child.getTop();
//             float childOffsetRange = -(dependency.getMeasuredHeight() - getFinalTopHeight(dependency));
        int dependencyOffsetRange = getBarOffsetRange(dependency);
        if (!isClosed(dependency)) {
            float childTransY = dependency.getTranslationY() == 0 ? 0 :
                    dependency.getTranslationY() == dependencyOffsetRange ? childOffsetRange :
                            ((float) Math.floor(dependency.getTranslationY()) / (dependencyOffsetRange * 1.0f) * childOffsetRange);
            Logger.d(TAG, "offsetChildAsNeeded(!isClosed)-> dependency.getTranslationY()=%s, dependencyOffsetRange=%s, childOffsetRange=%s, childTransY=%s",
                    dependency.getTranslationY(), dependencyOffsetRange, childOffsetRange, childTransY);
            if (Math.abs(childTransY) > Math.abs(childOffsetRange)) {
                childTransY = childOffsetRange;
            }
            Logger.d(TAG, "offsetChildAsNeeded-> real childTransY=%s", childTransY);
            child.setTranslationY(childTransY);
        } else {
            float delta = dependency.getTranslationY() - dependencyOffsetRange;
            float childTransY = child.getTranslationY() + delta;
            Logger.d(TAG, "offsetChildAsNeeded(isClosed)-> dependency.getTranslationY()=%s, child.getTranslationY()=%s, dependencyOffsetRange=%s, childOffsetRange=%s, childTransY=%s, delta=%s",
                    dependency.getTranslationY(), child.getTranslationY(), dependencyOffsetRange, childOffsetRange, childTransY, delta);
            child.setTranslationY(childTransY);
        }

    }


    @Override
    protected View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view))
                return view;
        }
        return null;
    }

    private int getBarOffsetRange(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            return ((UcNewsBarLayout) dependency).getBarOffsetRange();
        }
        return 0;
    }

    private int getFinalTopHeight(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            return ((UcNewsBarLayout) dependency).getHeaderHeight();
        }
        return 0;
    }

    private boolean isClosed(View dependency) {
        if (dependency instanceof UcNewsBarLayout) {
            UcNewsBarLayout barLayout = ((UcNewsBarLayout) dependency);
            return barLayout.isClosed();
        }
        return false;
    }


    private boolean isDependOn(View dependency) {
        return dependency instanceof UcNewsBarLayout;
    }
}