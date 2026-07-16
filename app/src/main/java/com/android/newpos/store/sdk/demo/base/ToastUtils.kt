package com.android.newpos.store.sdk.demo.base

import android.util.Log
import android.widget.Toast
import com.android.newpos.store.sdk.demo.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastUtils {
    fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Toast.makeText(MainApplication.getContext(), message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("showToast", "Error while showing toast: ${e.message}", e)
            }
        }
    }
    fun showToast(msgId: Int) {
        showToast(MainApplication.getContext()!!.getString(msgId))
    }
}