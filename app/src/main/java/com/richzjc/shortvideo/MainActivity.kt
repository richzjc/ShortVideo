package com.richzjc.shortvideo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kronos.router.Router
import com.richzjc.shortvideo.callback.UserPrivacyCallback
import com.richzjc.shortvideo.model.MainTabItemEntity
import com.richzjc.shortvideo.widget.FragmentTabHost
import com.richzjc.shortvideo.widget.RootTabItem
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {
    private var tabhost: FragmentTabHost? = null
    private var views: MutableList<RootTabItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity_main)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        UtilsContextManager.getInstance().init(application)
        val sp: SharedPreferences = getSharedPreferences("shared_preference_config", 0)
        sp.edit().putBoolean("userPrivacy", false).commit()
        Router.map("wscn://wallstreetcn.com/show/user/privacy/dialog", UserPrivacyCallback())
        Router.sharedRouter().attachApplication(application)
        OpenCVLoader.initLocal()
        views = ArrayList()
        tabhost = findViewById(R.id.tabhost)
        tabhost?.setup(this, supportFragmentManager, android.R.id.tabcontent)
        addTab()
    }

    private fun addTab() {
        val list = MainActFragmentModel.getInstance().tabItems
        (0 until list.size).forEach {
            var bundle = intent.extras
            if (bundle == null) {
                bundle = Bundle()
            }
            val entity = list[it]
            val tabSpec = tabhost?.newTabSpec(it.toString() + "11")
                ?.setIndicator(createTabItemViewAt(it, entity))
            tabhost?.addTab(tabSpec, entity.fragmentClass, bundle)
        }
    }

    private fun createTabItemViewAt(position: Int, entity: MainTabItemEntity): View {
        val view =
            LayoutInflater.from(this).inflate(R.layout.medusa_root_tab_item, null) as RootTabItem
        view?.findViewById<TextView>(R.id.tab_text)?.setTextColor(
            ContextCompat.getColorStateList(
                this,
                R.color.tab_drawable_text_color
            )
        )

        view.isSelected = 0 == position
        views!!.add(view)
        views!![position].configData(entity.title, entity.resourceId)
        return view
    }
}