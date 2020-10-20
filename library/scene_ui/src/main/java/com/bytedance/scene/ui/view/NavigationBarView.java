/*
 * Copyright (C) 2019 ByteDance Inc
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
package com.bytedance.scene.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by JiangQi on 8/28/18.
 */
public final class NavigationBarView extends View {
    private WindowInsetsCompat mLastInsets;
    private boolean mDrawNavigationBarBackground = true;
    private Drawable mNavigationBarBackground;

    public NavigationBarView(Context context) {
        super(context);
        init();
    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final Runnable REQUEST_LAYOUT_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    private void init() {
        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        if (getVisibility() == View.GONE) {
                            mLastInsets = null;
                            return insets;
                        }
                        WindowInsetsCompat insetsCompat = new WindowInsetsCompat(insets);
                        if (!insetsCompat.equals(mLastInsets)) {
                            mLastInsets = new WindowInsetsCompat(insets);
                            //Must post requestLayout otherwise ViewGroup's state will freeze in isLayoutRequested()==true,
                            //then following requestLayout will be ignored
                            post(REQUEST_LAYOUT_RUNNABLE);
                        }
                        return new WindowInsetsCompat(insets).replaceSystemWindowInsets(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                0);
                    }
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int[] THEME_ATTRS = {
                    android.R.attr.navigationBarColor
            };
            final TypedArray a = getContext().obtainStyledAttributes(THEME_ATTRS);
            try {
                mNavigationBarBackground = a.getDrawable(0);
            } finally {
                a.recycle();
            }
        }
    }

    public void setNavigationBarBackground(@Nullable Drawable bg) {
        mNavigationBarBackground = bg;
        invalidate();
    }

    @Nullable
    public Drawable getNavigationBarBackgroundDrawable() {
        return mNavigationBarBackground;
    }

    public void setNavigationBarBackground(@DrawableRes int resId) {
        mNavigationBarBackground = resId != 0 ? ContextCompat.getDrawable(getContext(), resId) : null;
        invalidate();
    }

    public void setNavigationBarBackgroundColor(@ColorInt int color) {
        mNavigationBarBackground = new ColorDrawable(color);
        invalidate();
    }

    private static int getDefaultSize2(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    protected void onMeasure2(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLastInsets != null) {
            onMeasure2(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mLastInsets.getSystemWindowInsetBottom(), MeasureSpec.EXACTLY));
        } else {
            onMeasure2(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawNavigationBarBackground && mNavigationBarBackground != null) {
            final int inset;
            if (Build.VERSION.SDK_INT >= 21) {
                inset = mLastInsets != null
                        ? mLastInsets.getSystemWindowInsetBottom() : 0;
            } else {
                inset = 0;
            }
            if (inset > 0) {
                mNavigationBarBackground.setBounds(0, getHeight() - inset, getWidth(), getHeight());
                mNavigationBarBackground.draw(canvas);
            }
        }
    }
}
