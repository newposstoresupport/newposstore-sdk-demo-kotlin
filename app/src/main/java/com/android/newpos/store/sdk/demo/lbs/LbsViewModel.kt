package com.android.newpos.store.sdk.demo.lbs

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.BaseUtils
import com.newpos.store.android.sdk.dto.LbsLocationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//import io.reactivex.Observable
//import io.reactivex.schedulers.Schedulers

/**
 * @ClassName : LbsViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:29
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class LbsViewModel(application: Application) : BaseViewModel(title = "LBS Location",application) {

    val mLocation = MutableLiveData<String>()
    var logs = MutableLiveData<String>()

    fun getLocation() {
        mLocation.postValue("Retrieving location information...")
        logs.postValue("Retrieving location information...")

        //TODO: Change to your own parameters
        val lbsLocationRequest = LbsLocationRequest().apply {
            output = "json"
            mnc = "0"
            ci = "46407687"
            appid = "OTA_LBS"
            mcc = "460"
            lac = "9763"
            radio = "LTE"
        }
        showLoading(LoadingOption("Retrieving location information..."))
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    StoreSdk.getInstance().lbsAbility().getLocation(lbsLocationRequest, false)
                }
                if (response == null) {
                    mLocation.postValue("get location failed, please check log!")
                    logs.postValue("get location failed, please check log!")
                    return@launch
                } else {
                    mLocation.postValue(BaseUtils.toJson(response))
                    logs.postValue("get location success: $response")
                }
            } catch (e: Exception) {
                showError(e)
            }finally {
                dismissLoading()
            }
        }
    }

    override fun showError(throwable: Throwable) {
        super.showError(throwable)
        mLocation.postValue("")
        logs.postValue("LBS Error: " + throwable.message)
    }

}
