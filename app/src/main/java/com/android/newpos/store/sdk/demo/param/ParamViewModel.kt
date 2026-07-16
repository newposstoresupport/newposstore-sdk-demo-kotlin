package com.android.newpos.store.sdk.demo.param

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.BuildConfig
import com.android.newpos.store.sdk.demo.MainApplication
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.DownloadFileManager
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.dto.AppResponse
import com.newpos.store.android.sdk.dto.ParamDownV2Result
import com.newpos.store.android.sdk.dto.ParamDownloadRequest
import com.newpos.store.android.sdk.dto.ParamDownloadResponse
import com.newpos.store.android.sdk.dto.ParamTask
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * 参数下载（含 Param V2，依赖 Maven api 1.0.3）
 */
class ParamViewModel(application: Application) : BaseViewModel(title = "Download Parameters", application) {
    val mInfo: MutableLiveData<String> = MutableLiveData()
    private var appResponseList: List<AppResponse> = emptyList()
    private var paramTaskList: List<ParamTask> = emptyList()
    val paramDownloadResponseMutableLiveData: MutableLiveData<ParamDownloadResponse> = MutableLiveData()
    val v2ResultLiveData: MutableLiveData<List<ParamDownV2Result>> = MutableLiveData()
    val showFileContent = MutableLiveData<String>()

    fun queryParamFile() {
        showLoading(LoadingOption("Querying parameter list..."))
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    StoreSdk.getInstance().paramAbility().queryParamsList()
                }
                appResponseList = list ?: emptyList()
                if (appResponseList.isEmpty()) {
                    mInfo.postValue("There is no parameter file under the application. Please go to the cloud platform to upload it.")
                } else {
                    mInfo.postValue(appResponseList.joinToString(separator = "\n") { it.toString() })
                }
            } catch (t: Throwable) {
                showError(t)
            } finally {
                dismissLoading()
            }
        }
    }

    fun downloadParamFile() {
        showLoading(LoadingOption("Downloading parameter file..."))
        viewModelScope.launch {
            try {
                if (appResponseList.isEmpty()) {
                    mInfo.postValue("There is no parameter list, please query first!")
                    return@launch
                }
                val response = withContext(Dispatchers.IO) {
                    val paramAbility = StoreSdk.getInstance().paramAbility()
                    val saveDir = MainApplication.getInstance()
                        .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val req = ParamDownloadRequest().apply {
                        packageName = MainApplication.getInstance().packageName
                        saveFilePath = saveDir?.absolutePath
                        versionCode = BuildConfig.VERSION_CODE
                        serialNumber = paramAbility.serialNumber
                    }
                    DownloadFileManager.downloadParamToPath(req, appResponseList[0])
                }
                if (response == null) {
                    mInfo.postValue("Download parameter file failed, please check!")
                } else {
                    paramDownloadResponseMutableLiveData.postValue(response)
                }
            } catch (t: Throwable) {
                showError(t)
            } finally {
                dismissLoading()
            }
        }
    }

    fun queryParamFileV2() {
        showLoading(LoadingOption("Querying parameter(v2)..."))
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    StoreSdk.getInstance().paramAbilityV2().queryParamTask()
                }
                paramTaskList = list ?: emptyList()
                if (paramTaskList.isEmpty()) {
                    mInfo.postValue("There is no parameter file under the application. Please go to the cloud platform to config it.")
                } else {
                    mInfo.postValue(paramTaskList.joinToString(separator = "\n") { it.toString() })
                }
            } catch (t: Throwable) {
                showError(t)
            } finally {
                dismissLoading()
            }
        }
    }

    fun downloadParamFileV2() {
        showLoading(LoadingOption("Downloading parameter file(V2)..."))
        viewModelScope.launch {
            try {
                if (paramTaskList.isEmpty()) {
                    mInfo.postValue("There is no parameter list, please query first!")
                    return@launch
                }
                val results = withContext(Dispatchers.IO) {
                    val v2 = StoreSdk.getInstance().paramAbilityV2()
                    val v2Data = v2.queryParamDown(paramTaskList[0])
                    if (v2Data == null || v2Data.isEmpty()) {
                        null
                    } else {
                        DownloadFileManager.downloadParamToPathV2(MainApplication.getInstance(), v2Data)
                    }
                }
                if (results == null || results.isEmpty()) {
                    mInfo.postValue("Download parameter file failed, please check!")
                } else {
                    v2ResultLiveData.postValue(results)
                }
            } catch (t: Throwable) {
                showError(t)
            } finally {
                dismissLoading()
            }
        }
    }

    fun downloadOneKey(msg: String?) {
        showLoading(LoadingOption("Query and Download param...."))
        AppUtils.startDownloadWorker(MainApplication.getInstance(), msg)
    }

    fun dismiss() {
        dismissLoading()
    }

    fun readFile(file: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            showError(throwable)
        }) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return@launch
            }
            try {
                showFileContent.postValue(File(file).readText())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
