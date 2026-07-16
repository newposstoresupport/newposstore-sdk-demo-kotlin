package com.android.newpos.store.sdk.demo.base

import android.app.Activity
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import com.android.newpos.store.sdk.demo.R

class LoadingDialogManage constructor() {

    @Volatile
    private var dialog: LoadingDialog? = null

    @Synchronized
    fun show(context: Context, message: String) {
        dismiss()
        if (context !is Activity || context.isFinishing || context.isDestroyed) return

        val d = LoadingDialog(context).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        d.findViewById<AppCompatTextView>(R.id.tv)?.text = message
        d.show()
        dialog = d
    }

    @Synchronized
    fun dismiss() {
        dialog?.let { if (it.isShowing) it.dismiss() }
        dialog = null
    }

    companion object {
        @Volatile private var _instance: LoadingDialogManage ? = null
        @JvmStatic
        fun getInstance(): LoadingDialogManage =
            _instance ?: synchronized(this) {
                _instance ?: LoadingDialogManage().also { _instance = it }
            }

    }
}
