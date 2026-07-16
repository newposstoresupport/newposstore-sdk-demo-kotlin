package com.android.newpos.store.sdk.demo.register

import android.app.Application
import com.android.newpos.store.sdk.demo.MainApplication
import com.android.newpos.store.sdk.demo.base.InitCallback
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * @ClassName : RegisterViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:17
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class RegisterViewModel(application: Application): BaseViewModel(title = "Registration",application) {
    //you should use coroutines to dev async business
    fun register() {
        showLoading(LoadingOption("Initializing..."))
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    MainApplication.getInstance()
                        .initStoreSdk(AppUtils.getClientId(), object : InitCallback {
                            override fun onFinished() {
                                dismissLoading()
                            }
                        })
                }
            } catch (e: Exception) {
                showError(e)
            }
        }
    }
}