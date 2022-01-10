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
package com.bytedance.scene.animation.interaction.ghostview;

import android.graphics.Matrix;
import android.os.Build;
import androidx.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @hide
 */
@RestrictTo(LIBRARY_GROUP)
public class GhostViewUtils {
    private static final GhostViewImpl.Creator CREATOR;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            CREATOR = new GhostViewApi21.Creator();
        } else {
            CREATOR = new GhostViewApi14.Creator();
        }
    }

    public static GhostViewImpl addGhost(View view, ViewGroup viewGroup, Matrix matrix) {
        return CREATOR.addGhost(view, viewGroup, matrix);
    }

    public static void removeGhost(View view) {
        CREATOR.removeGhost(view);
    }
}
