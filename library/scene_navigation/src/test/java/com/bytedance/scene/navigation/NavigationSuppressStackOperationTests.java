package com.bytedance.scene.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import com.bytedance.scene.Scene;
import com.bytedance.scene.SceneLifecycleManager;
import com.bytedance.scene.State;
import com.bytedance.scene.navigation.NavigationScene;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.Looper.getMainLooper;
import static com.bytedance.scene.navigation.NavigationSceneGetter.requireNavigationScene;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

/**
 * invoke navigation operation in lifecycle callbacks
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@LooperMode(PAUSED)
public class NavigationSuppressStackOperationTests {
    @Test
    public void test_Immediate_Push_Pop_Remove() {
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };
        NavigationScene navigationScene = NavigationSourceUtility.createFromSceneLifecycleManager(rootScene);
        Scene second = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };
        navigationScene.push(second);
        assertEquals(second, navigationScene.getCurrentScene());
        navigationScene.remove(second);
        assertEquals(rootScene, navigationScene.getCurrentScene());
        navigationScene.push(second);
        assertEquals(second, navigationScene.getCurrentScene());
        navigationScene.pop();
        assertEquals(rootScene, navigationScene.getCurrentScene());
    }

    @Test
    public void test_Enter_Post_Push() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);

                Scene second = new Scene() {
                    @NonNull
                    @Override
                    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                        return new View(requireSceneContext());
                    }
                };
                requireNavigationScene(this).push(second);
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                assertNotEquals(second, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        NavigationSourceUtility.createFromSceneLifecycleManager(rootScene);
        shadowOf(getMainLooper()).idle();//execute Handler posted task
        assertNotEquals(rootScene, requireNavigationScene(rootScene).getCurrentScene());
        assertTrue(called.get());
    }

    @Test
    public void test_Enter_Post_Pop() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };
        NavigationScene navigationScene = NavigationSourceUtility.createFromSceneLifecycleManager(rootScene);
        Scene scene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                requireNavigationScene(this).pop();
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        navigationScene.push(scene);
        shadowOf(getMainLooper()).idle();//execute Handler posted task
        assertEquals(State.NONE, scene.getState());
        assertTrue(called.get());
    }

    @Test
    public void test_Enter_Post_Remove() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };
        NavigationScene navigationScene = NavigationSourceUtility.createFromSceneLifecycleManager(rootScene);
        Scene scene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                requireNavigationScene(this).remove(this);
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        navigationScene.push(scene);
        shadowOf(getMainLooper()).idle();//execute Handler posted task
        assertEquals(State.NONE, scene.getState());
        assertTrue(called.get());
    }

    @Test
    public void test_Exit_Post_Push() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onPause() {
                super.onPause();
                Scene second = new Scene() {
                    @NonNull
                    @Override
                    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                        return new View(requireSceneContext());
                    }
                };
                requireNavigationScene(this).push(second);
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                assertNotEquals(second, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        Pair<SceneLifecycleManager<NavigationScene>, NavigationScene> pair = NavigationSourceUtility.createFromInitSceneLifecycleManager(rootScene);
        SceneLifecycleManager<NavigationScene> lifecycleManager = pair.first;

        lifecycleManager.onStart();
        lifecycleManager.onResume();
        lifecycleManager.onPause();
        shadowOf(getMainLooper()).idle();//execute Handler posted task
        assertNotEquals(rootScene, requireNavigationScene(rootScene).getCurrentScene());
        assertTrue(called.get());
    }

    @Test
    public void test_Exit_Post_Pop() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };

        Pair<SceneLifecycleManager<NavigationScene>, NavigationScene> pair = NavigationSourceUtility.createFromInitSceneLifecycleManager(rootScene);
        SceneLifecycleManager<NavigationScene> lifecycleManager = pair.first;
        NavigationScene navigationScene = pair.second;

        lifecycleManager.onStart();
        lifecycleManager.onResume();

        Scene scene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onPause() {
                super.onPause();
                requireNavigationScene(this).pop();
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        navigationScene.push(scene);

        lifecycleManager.onPause();
        shadowOf(getMainLooper()).idle();//execute Handler posted task\
        assertEquals(State.NONE, scene.getState());
        assertTrue(called.get());
    }

    @Test
    public void test_Exit_Post_Remove() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Scene rootScene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }
        };

        Pair<SceneLifecycleManager<NavigationScene>, NavigationScene> pair = NavigationSourceUtility.createFromInitSceneLifecycleManager(rootScene);
        SceneLifecycleManager<NavigationScene> lifecycleManager = pair.first;
        NavigationScene navigationScene = pair.second;

        lifecycleManager.onStart();
        lifecycleManager.onResume();

        Scene scene = new Scene() {
            @NonNull
            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
                return new View(requireSceneContext());
            }

            @Override
            public void onPause() {
                super.onPause();
                requireNavigationScene(this).remove(this);
                assertEquals(this, requireNavigationScene(this).getCurrentScene());
                called.set(true);
            }
        };
        navigationScene.push(scene);

        lifecycleManager.onPause();
        shadowOf(getMainLooper()).idle();//execute Handler posted task\
        assertEquals(State.NONE, scene.getState());
        assertTrue(called.get());
    }
}
