package com.android.newpos.store.sdk.demo

import android.app.Application
import android.os.RemoteException
import android.text.TextUtils
import com.android.newpos.store.sdk.demo.base.InitCallback
import com.android.newpos.store.sdk.demo.base.LoadingDialogManage
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.ToastUtils.showToast
import com.android.newpos.store.sdk.demo.inquirer.AppInquirerViewModel
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.liulishuo.filedownloader.services.DefaultIdGenerator
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.liulishuo.filedownloader.util.FileDownloadUtils.formatString
import com.newpos.store.android.sdk.IAppInquirer
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.SPreference
import com.newpos.store.android.sdk.dto.AppElements
import com.newpos.store.android.sdk.dto.AuthenticationRequest
import com.newpos.store.android.sdk.listener.IStoreCallback
import com.pos.device.SDKManager
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        println("MainApplication>>onCreate")
        mainApplication = this
        MMKV.initialize(this)
        SPreference.I().init(applicationContext)
        val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        appScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    initDownloader()
                    initStoreSdk(AppUtils.getClientId(), null)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        SDKManager.init(applicationContext) {}
    }

    fun initStoreSdk(clientId: String?, callback: InitCallback? = null) {
        //TODO step 2
        // download newpos store sdk from github
        // and init StoreSdk by appid & appkey & appsecret
        val elements = AppElements().apply {
            appId = BuildConfig.APPID
            appKey = BuildConfig.APPKEY
            appSecret = BuildConfig.APPSECRET
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
                showToast(R.string.init_success)
                initAppInquirer()
                callback?.onFinished()
            }

            override fun initFailed(remoteException: RemoteException) {
                showToast(remoteException.message + getString(R.string.newstore_lost))
                callback?.onFinished()
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

    private fun initDownloader() {
        FileDownloader.setupOnApplicationOnCreate(this)
            .connectionCreator(
                FileDownloadUrlConnection.Creator(
                    FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                )
            ).idGenerator(object : DefaultIdGenerator() {
                override fun transOldId(
                    oldId: Int,
                    url: String,
                    path: String,
                    pathAsDirectory: Boolean
                ): Int {
                    return generateId(url, path, pathAsDirectory)
                }

                override fun generateId(url: String, path: String, pathAsDirectory: Boolean): Int {
                    return FileDownloadUtils.md5(formatString("path:%s", path)).hashCode()
                }
            })
    }

    companion object {
        private var mainApplication: MainApplication? = null
        val ld: LoadingDialogManage? = null

        @JvmStatic
        fun getInstance() = mainApplication!!

        @JvmStatic
        fun getContext() = mainApplication?.applicationContext
    }
}
