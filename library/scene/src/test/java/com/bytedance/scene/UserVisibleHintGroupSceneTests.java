package com.bytedance.scene;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.bytedance.scene.group.UserVisibleHintGroupScene;
import com.bytedance.scene.navigation.NavigationScene;
import com.bytedance.scene.utility.TestUtility;
import com.bytedance.scene.utlity.ViewIdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UserVisibleHintGroupSceneTests {

    @Test
    public void test() {
        TestScene testScene = new TestScene();
        Pair<SceneLifecycleManager<NavigationScene>, NavigationScene> pair = NavigationSourceUtility.createFromInitSceneLifecycleManager(testScene);

        SceneLifecycleManager sceneLifecycleManager = pair.first;
        NavigationScene navigationScene = pair.second;

        GroupSceneLifecycleTests.TestChildScene childScene = new GroupSceneLifecycleTests.TestChildScene();
        assertEquals(childScene.getState(), State.NONE);

        testScene.add(testScene.mId, childScene, "childScene");
        assertEquals(childScene.getState(), State.ACTIVITY_CREATED);

        sceneLifecycleManager.onStart();
        assertEquals(childScene.getState(), State.STARTED);
        assertTrue(testScene.isVisible());

        testScene.setUserVisibleHint(false);
        assertEquals(childScene.getState(), State.STARTED);
        assertFalse(testScene.isVisible());
        assertFalse(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.STARTED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.CREATED);

        testScene.setUserVisibleHint(true);
        assertEquals(childScene.getState(), State.STARTED);
        assertTrue(testScene.isVisible());
        assertTrue(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.STARTED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.STARTED);

        sceneLifecycleManager.onResume();
        assertEquals(childScene.getState(), State.RESUMED);
        assertTrue(testScene.isVisible());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.RESUMED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.RESUMED);

        testScene.setUserVisibleHint(false);
        assertEquals(childScene.getState(), State.RESUMED);
        assertFalse(testScene.isVisible());
        assertFalse(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.RESUMED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.CREATED);

        testScene.setUserVisibleHint(true);
        assertEquals(childScene.getState(), State.RESUMED);
        assertTrue(testScene.isVisible());
        assertTrue(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.RESUMED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.RESUMED);

        sceneLifecycleManager.onPause();
        assertEquals(childScene.getState(), State.STARTED);
        assertTrue(testScene.isVisible());

        testScene.setUserVisibleHint(false);
        assertEquals(childScene.getState(), State.STARTED);
        assertFalse(testScene.isVisible());
        assertFalse(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.STARTED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.CREATED);

        testScene.setUserVisibleHint(true);
        assertEquals(childScene.getState(), State.STARTED);
        assertTrue(testScene.isVisible());
        assertTrue(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.STARTED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.STARTED);

        sceneLifecycleManager.onStop();
        assertEquals(childScene.getState(), State.ACTIVITY_CREATED);

        testScene.setUserVisibleHint(false);
        assertEquals(childScene.getState(), State.ACTIVITY_CREATED);
        assertFalse(testScene.isVisible());
        assertFalse(testScene.getUserVisibleHint());
        assertEquals(testScene.getLifecycle().getCurrentState(), Lifecycle.State.CREATED);
        assertEquals(testScene.getUserVisibleHintLifecycle().getCurrentState(), Lifecycle.State.CREATED);

        testScene.setUserVisibleHint(true);
        assertEquals(childScene.getState(), State.ACTIVITY_CREATED);
        assertFalse(testScene.isVisible());
        assertTrue(testScene.getUserVisibleHint());

        sceneLifecycleManager.onDestroyView();
        assertEquals(childScene.getState(), State.NONE);

        testScene.setUserVisibleHint(false);
        assertEquals(childScene.getState(), State.NONE);
        assertFalse(testScene.isVisible());
        assertFalse(testScene.getUserVisibleHint());

        testScene.setUserVisibleHint(true);
        assertEquals(childScene.getState(), State.NONE);
        assertFalse(testScene.isVisible());
        assertTrue(testScene.getUserVisibleHint());
    }

    /**
     * androidx.lifecycle:lifecycle-runtime:2.1.0 exception
     * <p>
     * LifecycleOwner of this LifecycleRegistry is alreadygarbage collected. It is too late to change lifecycle state.
     * java.lang.IllegalStateException: LifecycleOwner of this LifecycleRegistry is alreadygarbage collected. It is too late to change lifecycle state.
     * at androidx.lifecycle.LifecycleRegistry.sync(LifecycleRegistry.java:327)
     * at androidx.lifecycle.LifecycleRegistry.moveToState(LifecycleRegistry.java:145)
     * at androidx.lifecycle.LifecycleRegistry.handleLifecycleEvent(LifecycleRegistry.java:131)
     * at com.bytedance.scene.group.UserVisibleHintGroupScene.onActivityCreated(UserVisibleHintGroupScene.java:95)
     */
    @Test
    public void testUserVisibleHintLifecycleGC() {
        TestScene testScene = new TestScene();
        Pair<SceneLifecycleManager<NavigationScene>, NavigationScene> pair = NavigationSourceUtility.createFromInitSceneLifecycleManager(testScene);
        SceneLifecycleManager sceneLifecycleManager = pair.first;

        sceneLifecycleManager.onStart();
        sceneLifecycleManager.onResume();
        TestUtility.forceGc();
        testScene.setUserVisibleHint(false);
    }

    public static class TestScene extends UserVisibleHintGroupScene {
        public final int mId;

        public TestScene() {
            mId = ViewIdGenerator.generateViewId();
        }

        @NonNull
        @Override
        public ViewGroup onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
            return new FrameLayout(requireSceneContext());
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.setId(mId);
        }
    }
}
