package com.android.newpos.store.sdk.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.newpos.store.sdk.demo.app.AppFragment
import com.android.newpos.store.sdk.demo.cloud.CloudFragment
import com.android.newpos.store.sdk.demo.common.CommonFragment
import com.android.newpos.store.sdk.demo.inquirer.AppInquirerFragment
import com.android.newpos.store.sdk.demo.lbs.LbsFragment
import com.android.newpos.store.sdk.demo.ota.OTAFragment
import com.android.newpos.store.sdk.demo.param.ParamFragment
import com.android.newpos.store.sdk.demo.register.RegisterFragment
import com.android.newpos.store.sdk.demo.rki.RkiFragment

class MainFragment : Fragment() {

    companion object {
        private const val KEY_SCROLL_Y = "key_scroll_y"
    }

    private var savedScrollY: Int = 0
    private var scrollView: ScrollView? = null

    private class MenuItem(
        val iconRes: Int,
        val title: String,
        val fragmentClass: Class<out Fragment>
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_menu, container, false)

        scrollView = root.findViewById(R.id.scrollView)
        val gridLayout: GridLayout = root.findViewById(R.id.gridMenu)

        val items = ArrayList<MenuItem>()
        items.add(MenuItem(R.mipmap.common, getString(R.string.menu_common), CommonFragment::class.java))
        items.add(MenuItem(R.mipmap.app, getString(R.string.menu_app), AppFragment::class.java))
        items.add(MenuItem(R.mipmap.register, getString(R.string.menu_register), RegisterFragment::class.java))
        items.add(MenuItem(R.mipmap.inquirer, getString(R.string.menu_inquirer), AppInquirerFragment::class.java))
        items.add(MenuItem(R.mipmap.position, getString(R.string.menu_lbs), LbsFragment::class.java))
        items.add(MenuItem(R.mipmap.params, getString(R.string.menu_param), ParamFragment::class.java))
        items.add(MenuItem(R.mipmap.ota, getString(R.string.menu_ota), OTAFragment::class.java))
        items.add(MenuItem(R.mipmap.cloud, getString(R.string.menu_cloud), CloudFragment::class.java))
        items.add(MenuItem(R.mipmap.rki, getString(R.string.menu_rki), RkiFragment::class.java))

        for (item in items) {
            val itemView = inflater.inflate(R.layout.item_menu_icon_title, gridLayout, false)
            val icon: ImageView = itemView.findViewById(R.id.icon)
            val title: TextView = itemView.findViewById(R.id.title)

            icon.setImageResource(item.iconRes)
            title.text = item.title

            itemView.setOnClickListener {
                try {
                    val fragment = item.fragmentClass.newInstance()
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            gridLayout.addView(itemView)
        }

        return root
    }

    override fun onPause() {
        super.onPause()
        scrollView?.let {
            savedScrollY = it.scrollY
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            savedScrollY = savedInstanceState.getInt(KEY_SCROLL_Y, savedScrollY)
        }
        scrollView?.post {
            scrollView?.scrollTo(0, savedScrollY)
        }
    }
}
