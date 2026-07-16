package com.android.newpos.store.sdk.demo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.newpos.store.sdk.demo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.newpos.store.android.sdk.StoreSdk
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.liulishuo.filedownloader.util.FileDownloadUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        println("MainApplication>>onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            Snackbar.make(it,
                getString(R.string.sdk_version, StoreSdk.getInstance().version),
                Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle(R.string.result_title)
//                    .setMessage(auth)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
//                    .create().show();
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment())
                .commit()

        updateHeaderVisibility(true)
        FileDownloadUtils.setDefaultSaveRootPath(getFilesDir().getAbsolutePath())
        //TODO 跳转github
    }

    fun isPhysical480x480Device(context: Context): Boolean {
        val dm = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(dm)

        val w = dm.widthPixels
        val h = dm.heightPixels

        val square = kotlin.math.abs(w - h) <= 10

        val wIn = w / dm.xdpi
        val hIn = h / dm.ydpi
        val diag = kotlin.math.hypot(wIn, hIn)

        return square && diag <= 4.5
    }

    fun updateHeaderVisibility(isMainPage: Boolean) {
        if (isPhysical480x480Device(this)) {
            setHeaderVisible(isMainPage)
        } else {
            setHeaderVisible(true)
        }
    }

    private fun setHeaderVisible(visible: Boolean) {
        val header = findViewById<View>(R.id.header)
        val container = findViewById<View>(R.id.container)
        val root = findViewById<ConstraintLayout>(R.id.root)

        header.visibility = if (visible) View.VISIBLE else View.GONE
    }

}