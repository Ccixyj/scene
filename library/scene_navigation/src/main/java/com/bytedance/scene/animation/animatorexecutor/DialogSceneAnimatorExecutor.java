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
package com.bytedance.scene.animation.animatorexecutor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.bytedance.scene.Scene;
import com.bytedance.scene.animation.AnimationInfo;
import com.bytedance.scene.animation.NavigationAnimatorExecutor;

/**
 * Created by JiangQi on 8/9/18.
 */
public class DialogSceneAnimatorExecutor extends NavigationAnimatorExecutor {
    private static final long ANIMATION_DURATION = 150;

    @Override
    public boolean isSupport(@NonNull Class<? extends Scene> from, @NonNull Class<? extends Scene> to) {
        return true;
    }

    @Override
    protected boolean disableConfigAnimationDuration() {
        return true;
    }

    @NonNull
    @Override
    protected Animator onPushAnimator(AnimationInfo from, final AnimationInfo to) {
        final View toView = to.mSceneView;
        float toViewAlpha = toView.getAlpha();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, toViewAlpha);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toView.setAlpha((float) animation.getAnimatedValue());
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(ANIMATION_DURATION);
        return valueAnimator;
    }

    @NonNull
    @Override
    protected Animator onPopAnimator(final AnimationInfo fromInfo, final AnimationInfo toInfo) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromInfo.mSceneView.getAlpha(), 0.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fromInfo.mSceneView.setAlpha((float) animation.getAnimatedValue());
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(ANIMATION_DURATION);
        return valueAnimator;
    }
}
