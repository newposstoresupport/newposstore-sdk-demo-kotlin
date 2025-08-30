package com.android.newpos.store.sdk.demo.lbs

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun getLocation() {
        mLocation.postValue("Retrieving location information...")
        val lbsLocationRequest = LbsLocationRequest().apply {
            output = "json"
            mnc = "0"
            ci = "46407687"
            appid = "OTA_LBS"
            mcc = "460"
            lac = "9763"
            radio = "LTE"
        }  // 创建 LbsLocationRequest 对象并设置参数

//        addSubscribe(
//            Observable.just(lbsLocationRequest)
//                .observeOn(Schedulers.io())
//                .map {it -> StoreSdk.getInstance().lbsAbility().getLocation(it, false) }
//                .subscribe({ response ->
//                    if (response == null) {  // 判断返回结果是否为空
//                        mLocation.postValue("get location failed, please check log!")
//                        return@subscribe // 提前返回
//                    }
//                    mLocation.postValue(BaseUtils.toJson(response))
//                }, { throwable -> showError(throwable) })
//        )


        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // 如果 getLocation 是阻塞方法，直接这样包一层
                    StoreSdk.getInstance().lbsAbility().getLocation(lbsLocationRequest, false)
                }
                if (response == null) {
                    mLocation.postValue("get location failed, please check log!")
                } else {
                    mLocation.postValue(BaseUtils.toJson(response))
                }
            } catch (e: Exception) {
                showError(e)
            }
        }
    }

    override fun showError(throwable: Throwable) {
        super.showError(throwable)
        mLocation.postValue("")  // 清空位置显示
    }
}
