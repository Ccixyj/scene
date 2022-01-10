package com.bytedance.scenedemo.navigation.push_pop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.bytedance.scene.Scene
import com.bytedance.scene.ktx.requireNavigationScene
import com.bytedance.scenedemo.R
import com.bytedance.scenedemo.utility.*

/**
 * Created by JiangQi on 7/30/18.
 */
class PushPopBasicUsageDemoScene : Scene() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val scrollView = ScrollView(requireSceneContext())
        scrollView.fitsSystemWindows = true

        val layout = LinearLayout(requireSceneContext())
        layout.orientation = LinearLayout.VERTICAL

        scrollView.addView(layout)

        val argument = arguments
        val value = argument?.getInt("1", 0) ?: 0
        scrollView.setBackgroundColor(ColorUtil.getMaterialColor(activity!!.resources, value))

        addClassPathTitle(layout)
        addSpace(layout, 12)
        addTitle(layout, getString(R.string.main_title_basic))

        addButton(layout, value.toString(), View.OnClickListener {
            val bundle = Bundle()
            bundle.putInt("1", value + 1)
            requireNavigationScene().push(PushPopBasicUsageDemoScene::class.java, bundle)
        })
        addSpace(layout, 100)

        return scrollView
    }
}
