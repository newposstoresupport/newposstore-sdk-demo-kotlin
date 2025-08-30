package com.android.newpos.store.sdk.demo.param

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.newpos.store.sdk.demo.MainApplication
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.BaseLog
import com.newpos.store.android.sdk.dto.AppResponse
import com.newpos.store.android.sdk.dto.ParamDownloadRequest
import com.newpos.store.android.sdk.dto.ParamDownloadResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @ClassName : ParamViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:30
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
//
class ParamViewModel(application: Application) : BaseViewModel(title = "Download Parameters",application) {
    public val mInfo: MutableLiveData<String> = MutableLiveData()
    private lateinit var appResponseList: List<AppResponse>
    public val paramDownloadResponseMutableLiveData: MutableLiveData<ParamDownloadResponse> =
        MutableLiveData()
    public val showFileContent = MutableLiveData<String>()

    fun queryParamFile() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            showError(throwable)
        }) {
            appResponseList = StoreSdk.getInstance().paramAbility().queryParamsList()
            if (appResponseList.isEmpty()) {
                mInfo.postValue("There is no parameter file under the application. Please go to the cloud platform to upload it.")
            } else {
                val builder = StringBuilder()
                appResponseList.forEach {
                    builder.append(it.toString()).append("\n")
                }
                mInfo.postValue(builder.toString())
            }
        }
    }

    fun downloadParamFile() {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            showError(throwable)
        }) {
            if (!::appResponseList.isInitialized) {
                mInfo.postValue("There is no parameter list, please query first!")
                return@launch
            }
            val paramAbility = StoreSdk.getInstance().paramAbility()
            val paramDownloadRequest = ParamDownloadRequest().apply {
                packageName = MainApplication.getInstance().packageName
                saveFilePath = MainApplication.getInstance()
                    .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
                versionCode = 1
                serialNumber = paramAbility.serialNumber
            }
            val paramDownloadResponse =
                paramAbility.downloadParamToPath(paramDownloadRequest, appResponseList[0])
            paramDownloadResponseMutableLiveData.postValue(paramDownloadResponse)
        }
    }

    fun readFile(file: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            showError(throwable)
        }) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {//this line is unnecessary due to minSdk
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