package com.android.newpos.store.sdk.demo.rki

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.dto.QueryKdhurlRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext


/**
 * @ClassName : RkiViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:31
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class RkiViewModel(application: Application) : BaseViewModel(title = "Remote Key Injection",application) {

    var mKdh: MutableLiveData<String> = MutableLiveData<String>()
    @OptIn(ExperimentalCoroutinesApi::class)
    fun download() {
        mKdh.postValue("Retrieving KDH information...")

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val kdhRequest = QueryKdhurlRequest()
                    StoreSdk.getInstance().rkiAbility().getKdhUrl(kdhRequest)
                }

                if (response == null) {
                    mKdh.postValue("get KDH failed, please check response!")
                    return@launch
                }

                val jsonObject = response.data
                if (jsonObject == null) {
                    mKdh.postValue("get KDH failed, please check response data!")
                    return@launch
                }

                if (!jsonObject.has("kdhUrl")) {
                    mKdh.postValue("get KDH failed, please check response data, kdhUrl not found!")
                    return@launch
                }

                val kdhUrl = jsonObject.get("kdhUrl").asString
                Log.d("KdhUrl", "KDH URL: $kdhUrl")
                suspendCancellableCoroutine<Unit> { cont ->
                    StoreSdk.getInstance().rkiAbility().downloadCustomerKeys(
                        AppUtils.getClientId(), kdhUrl, "",
                    ) { code, message, keyList ->
                        Log.e("IRkiCallback", "onDownload: $code, $message, $keyList")
                        mKdh.postValue("Download result: $code, $message, $keyList")
                        if (cont.isActive) cont.resume(Unit) {}
                    }
                }

            } catch (e: Exception) {
                showError(e)
            }
        }
    }
}