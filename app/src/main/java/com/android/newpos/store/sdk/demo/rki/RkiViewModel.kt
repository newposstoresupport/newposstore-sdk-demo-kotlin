package com.android.newpos.store.sdk.demo.rki

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.ToastUtils.showToast
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

    fun bind() {
        showLoading(LoadingOption("Binding RKI Service..."))
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    showToast(if (StoreSdk.getInstance().rkiAbility().bindRkiService()) "bind success" else "bind failed")
                }
            } catch (e: Exception) {
                showError(e)
            } finally {
                dismissLoading()
            }
        }
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    fun download() {
        mKdh.postValue("Retrieving KDH information...")
        showLoading(LoadingOption("Retrieving KDH information..."))
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
                mKdh.postValue("KDH URL: $kdhUrl")


                // TODO: Pay attention to these two parameters: clientID and KDHUrl
                showToast(StoreSdk.getInstance().rkiAbility().downloadCustomerKeys(
                    AppUtils.getClientId(), kdhUrl, ""){code, message, keyList ->
                    Log.e("IRkiCallback", "onDownload: $code, $message, $keyList")
                    mKdh.postValue("Download result: $code, $message, $keyList")
                }.message)

            } catch (e: Exception) {
                showError(e)
            }finally {
                dismissLoading()
            }
        }
    }
}