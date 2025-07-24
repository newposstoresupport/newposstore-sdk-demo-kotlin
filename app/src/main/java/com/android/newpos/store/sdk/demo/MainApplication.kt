package com.android.newpos.store.sdk.demo

import android.app.Application
import android.os.RemoteException
import android.text.TextUtils
import android.widget.Toast
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.inquirer.AppInquirerViewModel
import com.newpos.store.android.sdk.IAppInquirer
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.SPreference
import com.newpos.store.android.sdk.dto.AppElements
import com.newpos.store.android.sdk.dto.AuthenticationRequest
import com.newpos.store.android.sdk.listener.IStoreCallback
import com.tencent.mmkv.MMKV

/**
 * @ClassName : MainApplication
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-9:58
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        mainApplication = this
        MMKV.initialize(this)
        initStoreSdk(AppUtils.getClientId())
        SPreference.I().init(applicationContext)
    }

    fun initStoreSdk(clientId: String?){
        //TODO step 2
        // download newpos store sdk from github
        // and init StoreSdk by appid & appkey & appsecret
        val elements = AppElements().apply {
            appId = APPID
            appKey = APPKEY
            appSecret = APPSECRET
        }

        val request: AuthenticationRequest? = if(!TextUtils.isEmpty(clientId)){
            AuthenticationRequest().apply {
                remark = null
                setClientId(clientId)
            }
        } else null

        //TODO Recommendation: Initialize the interface only once in the application
        StoreSdk.getInstance().init(applicationContext, elements, request, object : IStoreCallback {
            override fun initSuccess() {
                Toast.makeText(applicationContext, R.string.init_success, Toast.LENGTH_SHORT).show()
                initAppInquirer()
            }

            override fun initFailed(remoteException: RemoteException) {
                Toast.makeText(applicationContext,
                    "${remoteException.message}\n${getString(R.string.newstore_lost)}",
                    Toast.LENGTH_SHORT).show()
                //showDialog(message)
            }
        })
    }

    private fun initAppInquirer(){
        //TODO step 3
        // init app inquirer to Listen to your application update status and management policies
        StoreSdk.getInstance().initAppInquirer(applicationContext, object : IAppInquirer.Stub() {

            override fun onReadyToUpdate(): Boolean {
                val isReady = SPreference.I().getBoolean(AppInquirerViewModel.key)
                println("app is ready to update: $isReady")
                return isReady
            }
        });
    }

    companion object {
        //TODO step 1
        // make sure to replace with your own appid & appkey & appsecret
        private const val APPID = "32292cc7e05a2b86ddea1d6746210283"
        private const val APPKEY = "c3ff54daf66bbbd2e8d8dbf08172a5aa"
        private const val APPSECRET = "a8e2188e9474692ea6a538f0a4f955ab"

        private var mainApplication: MainApplication? = null
        fun getInstance() = mainApplication!!
    }
}