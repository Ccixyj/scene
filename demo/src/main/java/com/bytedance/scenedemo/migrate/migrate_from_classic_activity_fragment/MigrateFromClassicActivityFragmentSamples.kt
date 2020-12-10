package com.bytedance.scenedemo.migrate.migrate_from_classic_activity_fragment

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bytedance.scene.NavigationSceneUtility
import com.bytedance.scene.Scene
import com.bytedance.scene.SceneDelegate
import com.bytedance.scene.interfaces.ChildSceneLifecycleAdapterCallbacks
import com.bytedance.scene.navigation.NavigationScene
import com.bytedance.scene.ui.SceneNavigationContainer
import com.bytedance.scenedemo.R

/**
 * This sample show how to use Scene with classic Android app develop technology (Activity+Fragment) together.
 * Once all fragments are converted to 'SceneProxyFragment', you can remove all Fragments, then use Scene directly.
 */
//TODO in developing
class MigrateFromClassicAndroidActivitySamplesActivity : AppCompatActivity(), SceneNavigationContainer {
    override fun getThemeId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isVisible(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNavigationScene(): NavigationScene? = this.mSceneActivityDelegate?.navigationScene


    private lateinit var mRootFragment: Fragment
    private lateinit var mSceneActivityDelegate: SceneDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.bytedance.scenedemo.R.layout.layout_migrate_from_classic_activity_fragment)

        mRootFragment = ClassicFragment()
        supportFragmentManager.beginTransaction().add(R.id.classic_fragment_container, mRootFragment).commitNowAllowingStateLoss()


        findViewById<View>(R.id.btn)?.setOnClickListener {
            Toast.makeText(this, "open detail", Toast.LENGTH_SHORT).show()
            mSceneActivityDelegate.navigationScene?.push(DetailScene::class.java)
        }

        val sceneContainer = findViewById<View>(R.id.scene_container)

        mSceneActivityDelegate = NavigationSceneUtility.setupWithActivity(this, EmptyHolderScene::class.java)
                .toView(com.bytedance.scenedemo.R.id.scene_container).drawWindowBackground(false)
                .fixSceneWindowBackgroundEnabled(true)
                .sceneBackground(android.R.color.white)
                .supportRestore(false)
                .build()

        mSceneActivityDelegate.navigationScene?.registerChildSceneLifecycleCallbacks(object : ChildSceneLifecycleAdapterCallbacks() {
            var count = 0
            override fun onSceneActivityCreated(scene: Scene, savedInstanceState: Bundle?) {
                super.onSceneActivityCreated(scene, savedInstanceState)
                count++
                if (count > 1 && mRootFragment.userVisibleHint) {
                    mRootFragment.userVisibleHint = false
                    //forbidden previous button to be clicked second time
                    sceneContainer.isClickable = true
                }
            }

            override fun onSceneViewDestroyed(scene: Scene) {
                super.onSceneViewDestroyed(scene)
                count--
                if (count == 1 && !mRootFragment.userVisibleHint) {
                    mRootFragment.userVisibleHint = true
                    sceneContainer.isClickable = false
                }
            }
        }, false)
    }

    override fun onBackPressed() {
        val sceneDelegate = this.mSceneActivityDelegate
        if (sceneDelegate.onBackPressed()) {
            //empty
        } else {
            super.onBackPressed()
        }
    }

    class EmptyHolderScene : Scene() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
            return View(requireSceneContext())
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            getView().setBackgroundColor(Color.TRANSPARENT)
        }
    }

    //This fragment has business logic
    class ClassicFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val textView = TextView(requireActivity())
            textView.text = "ClassicFragment"
            return textView
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
        }

        private fun gotoDetail() {
            (requireActivity() as MigrateFromClassicAndroidActivitySamplesActivity).navigationScene?.push(DetailScene::class.java)
        }
    }

    //This fragment don't have any business logic, its business logic is refactored to another Scene
    class SceneProxyFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
        }
    }

    class DetailScene : Scene() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
            val textView = TextView(requireSceneContext())
            textView.text = "DetailScene"
            return textView
        }
    }
}