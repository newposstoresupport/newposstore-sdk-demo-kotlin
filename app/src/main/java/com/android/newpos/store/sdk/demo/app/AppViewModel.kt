package com.android.newpos.store.sdk.demo.app

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.BaseException
import com.newpos.store.android.sdk.dto.StoreApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * @ClassName : AppViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/7/31-17:231
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class AppViewModel(application : Application) : BaseViewModel("App Management", application) {

    private val appList = MutableLiveData<List<AppDownloadStatus>>()

    fun getAppList(): MutableLiveData<List<AppDownloadStatus>> {
        return appList
    }

    /**
     * Get the application by the specified package name
     */
    fun getAppsByPackageName() {
        showLoading(LoadingOption("Loading apps..."))
        viewModelScope.launch {
            try {
                val appDownloadStatuses = withContext(Dispatchers.IO) {
                    val packList = mutableListOf<String>()
                    packList.add("com.newpos.rki")
                    packList.add("com.core.softSignTest111")

                    val storeApps = StoreSdk.getInstance().appAbility().getStoreApps(packList)
                        ?: throw BaseException("No application found")

                    storeApps.map { storeApp ->
                        AppDownloadStatus().apply {
                            this.storeApp = storeApp
                            this.pack = storeApp.packageName
                            this.downloadStatus = DownloadStatus.START
                            this.percent = "0%"
                        }
                    }
                }
                appList.postValue(appDownloadStatuses)
            } catch (e: Exception) {
                showError(e)
            }finally {
                dismissLoading()
            }
        }
    }

    /**
     * Check for new app versions
     */
    fun checkForUpdates(packageName: String) {
        showLoading(LoadingOption("Checking for updates..."))
        viewModelScope.launch {
            try {
                val newApp = withContext(Dispatchers.IO) {
                    StoreSdk.getInstance().appAbility().checkForUpdate(packageName)
                }
                if (newApp == null) {
                    throw BaseException("check failed!")
                }
                Toast.makeText(
                    getApplication(),
                    getApplication<Application>().getString(R.string.start_download, newApp.toString()),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                showError(e)
            }finally {
                dismissLoading()
            }
        }
    }

    /**
     * Download version
     * @param storeApp StoreApp
     */
    fun downloadForUpdates(storeApp: StoreApp) {
        showLoading(LoadingOption("Downloading app..."))
        viewModelScope.launch {
            try {
                val storagePath = withContext(Dispatchers.IO) {
                    FileDownloadUtils.generateFilePath(
                        FileDownloadUtils.getDefaultSaveRootPath(),
                        FileDownloadUtils.md5(
                            FileDownloadUtils.formatString(
                                "%s:%s",
                                storeApp.packageName,
                                storeApp.verCode
                            )
                        )
                    )
                }
                println(storagePath)
                println(storeApp.appFile)

                withContext(Dispatchers.IO) {
                    FileDownloader.getImpl()
                        .create(storeApp.appFile)
                        .setPath(storagePath, false)
                        .setAutoRetryTimes(3)
                        .setCallbackProgressTimes(500)
                        .setMinIntervalUpdateSpeed(500)
                        .setCallbackProgressMinInterval(500)
                        .setListener(AppStatusManager(storeApp.packageName))
                        .start()
                }

                Toast.makeText(
                    getApplication(),
                    getApplication<Application>().getString(R.string.start_download, storeApp.progName),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                showError(e)
            }finally {
                dismissLoading()
            }
        }
    }

    private inner class AppStatusManager(private val pack: String) : FileDownloadListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "pending:$soFarBytes,$totalBytes")
            val appDownloads = appList.value ?: return
            for (status in appDownloads) {
                if (Objects.equals(status.pack, pack)) {
                    status.downloadStatus = DownloadStatus.START
                    status.percent = "pending"
                }
            }
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "progress:$soFarBytes,$totalBytes")
            val appDownloads = appList.value ?: return
            for (status in appDownloads) {
                if (Objects.equals(status.pack, pack)) {
                    status.downloadStatus = DownloadStatus.DOWNLOADING
                    val percent = soFarBytes / totalBytes.toFloat()
                    status.percent = (percent * 100).toString() + "%"
                }
            }
            appList.postValue(appDownloads)
        }

        override fun completed(task: BaseDownloadTask) {
            Log.w(TAG, "completed:$task")
            val appDownloads = appList.value ?: return
            for (status in appDownloads) {
                if (Objects.equals(status.pack, pack)) {
                    status.downloadStatus = DownloadStatus.DOWNLOADED
                    status.percent = "100%"
                }
            }
            appList.postValue(appDownloads)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "paused:$soFarBytes,$totalBytes")
            val appDownloads = appList.value ?: return
            for (status in appDownloads) {
                if (Objects.equals(status.pack, pack)) {
                    status.downloadStatus = DownloadStatus.PAUSED
                    val percent = soFarBytes / totalBytes.toFloat()
                    status.percent = (percent * 100).toString() + "%"
                }
            }
            appList.postValue(appDownloads)
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            e.printStackTrace()
            Log.e(TAG, "error:$task,$e")
            val appDownloads = appList.value ?: return
            for (status in appDownloads) {
                if (Objects.equals(status.pack, pack)) {
                    status.downloadStatus = DownloadStatus.ERROR
                }
            }
            appList.postValue(appDownloads)
        }

        override fun warn(task: BaseDownloadTask) {
            Log.w(TAG, "error:$task")
        }
    }

    companion object {
        private const val TAG = "AppDownloader"
    }
}
