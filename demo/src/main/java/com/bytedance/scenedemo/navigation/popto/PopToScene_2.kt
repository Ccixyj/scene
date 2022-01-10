package com.bytedance.scenedemo.navigation.popto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bytedance.scene.Scene
import com.bytedance.scene.ktx.requireNavigationScene
import com.bytedance.scenedemo.R
import com.bytedance.scenedemo.utility.ColorUtil

/**
 * Created by JiangQi on 8/2/18.
 */
class PopToScene_2 : Scene() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): ViewGroup {
        return inflater.inflate(R.layout.basic_layout, container, false) as ViewGroup
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view.setBackgroundColor(ColorUtil.getMaterialColor(resources, 3))
        val name = view.findViewById<TextView>(R.id.name)
        name.text = requireNavigationScene().stackHistory
        val btn = view.findViewById<Button>(R.id.btn)
        btn.text = getString(R.string.nav_pop_to_btn_back0)
        btn.setOnClickListener { requireNavigationScene().popTo(PopToScene_0::class.java) }
        val btn2 = view.findViewById<Button>(R.id.btn2)
        btn2.visibility = View.VISIBLE
        btn2.text = getString(R.string.nav_pop_to_btn_to_bottom)
        btn2.setOnClickListener { requireNavigationScene().popToRoot() }
    }
}