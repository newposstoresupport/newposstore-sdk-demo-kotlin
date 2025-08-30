package com.android.newpos.store.sdk.demo.inquirer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.newpos.store.android.sdk.base.BaseApi
import com.newpos.store.android.sdk.base.BaseLog
import com.newpos.store.android.sdk.base.SPreference


/**
 * @ClassName : AppInquirerViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:21
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class AppInquirerViewModel(application: Application): BaseViewModel(title = "App Inquirer", application) {

    companion object {
        const val key = "key_ready"
    }

    private val status = MutableLiveData<Boolean>()
    private val appStatus = MutableLiveData<Boolean>()

    fun getStatus(): MutableLiveData<Boolean> = status

    fun getAppStatus(): MutableLiveData<Boolean> = appStatus

    fun queryStatus() {
        status.postValue(BaseApi.getInstance().isRegisterInquirer())
    }

    fun isReadyToUpdate(): Boolean {
        //TODO Here you can customize your own logic, when the application is idle and busy
        return SPreference.I().getBoolean(key)
    }

    fun updateAppStatus(ready: Boolean) {
        BaseLog.d("updateAppStatus:$ready")
        SPreference.I().putBoolean(key, ready)
        appStatus.postValue(ready)
    }
}