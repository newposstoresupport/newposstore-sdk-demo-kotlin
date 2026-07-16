package com.android.newpos.store.sdk.demo.ota

import android.app.Application
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.newpos.store.sdk.demo.MainApplication
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.app.DownloadStatus
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.LoadingOption
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.dto.Firmware
import com.newpos.store.android.sdk.dto.FirmwareInfo
import com.newpos.store.android.sdk.dto.QueryFirmwareRequest
import com.android.newpos.store.sdk.demo.base.RomDeviceReflect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * OTA / 固件查询与下载（对齐 StoreSdk Java demo，依赖 api 1.0.3）
 */
class OTAViewModel(application: Application) : BaseViewModel(title = "OTA Upgrade", application) {

    private var firmwareInfo: FirmwareInfo? = null
    val mInfo: MutableLiveData<String> = MutableLiveData()
    val statusMutableLiveData: MutableLiveData<FirmwareDownloadStatus> = MutableLiveData()

    fun queryFirmware() {
        showLoading(LoadingOption("Querying firmware information..."))
        viewModelScope.launch {
            try {
                val info = withContext(Dispatchers.IO) {
                    val firmwareRequest = QueryFirmwareRequest()
                    val firmware = Firmware()
                    firmware.custom = RomDeviceReflect.getCustomerName()
                    firmware.firmwareId = RomDeviceReflect.getFirmwareId()
                    firmware.version = RomDeviceReflect.getFirmwareVersion()
                    firmwareRequest.firmware = firmware
                    StoreSdk.getInstance().otaAbility().queryFirmware(firmwareRequest)
                }
                firmwareInfo = info
                if (info == null) {
                    mInfo.postValue("There is no new firmware. Please go to the cloud platform to upload it.")
                } else {
                    mInfo.postValue(info.toString())
                }
            } catch (t: Throwable) {
                showError(t)
            } finally {
                dismissLoading()
            }
        }
    }

    fun downloadFirmware() {
        val info = firmwareInfo
        if (info == null || TextUtils.isEmpty(info.url) ||
            TextUtils.isEmpty(info.hash) || TextUtils.isEmpty(info.version)
        ) {
            mInfo.postValue("There is no firmware, please query first!")
            return
        }

        showLoading(LoadingOption("Downloading firmware file..."))
        val storagePath = FileDownloadUtils.generateFilePath(
            FileDownloadUtils.getDefaultSaveRootPath(),
            FileDownloadUtils.md5(
                FileDownloadUtils.formatString("%s:%s", info.hash, info.version)
            )
        )

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    FileDownloader.getImpl()
                        .create(info.url)
                        .setPath(storagePath, false)
                        .setAutoRetryTimes(3)
                        .setCallbackProgressTimes(500)
                        .setMinIntervalUpdateSpeed(500)
                        .setCallbackProgressMinInterval(500)
                        .setListener(FirmwareStatusManager())
                        .start()
                }
                Toast.makeText(
                    MainApplication.getInstance(),
                    MainApplication.getInstance().getString(R.string.start_download_firm, info.version),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Throwable) {
                showError(e)
            } finally {
                dismissLoading()
            }
        }
    }

    private inner class FirmwareStatusManager : FileDownloadListener() {
        private fun postValue(
            downloadStatus: DownloadStatus,
            percent: String,
            speed: String,
            sofar: Int,
            total: Int
        ) {
            val status = FirmwareDownloadStatus()
            status.firmwareInfo = firmwareInfo
            status.percent = percent
            status.speed = speed
            status.sofar = sofar
            status.total = total
            status.downloadStatus = downloadStatus
            statusMutableLiveData.postValue(status)
        }

        override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "pending:$soFarBytes,$totalBytes")
            postValue(DownloadStatus.START, "pending", "0 KB/s", soFarBytes, totalBytes)
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "progress:$soFarBytes,$totalBytes")
            val percent = soFarBytes / totalBytes.toFloat()
            postValue(
                DownloadStatus.DOWNLOADING,
                (percent * 100).toString() + "%",
                task.speed.toString() + " KB/s",
                soFarBytes,
                totalBytes
            )
        }

        override fun completed(task: BaseDownloadTask) {
            Log.w(TAG, "completed:$task")
            postValue(DownloadStatus.DOWNLOADED, "100%", task.speed.toString() + " KB/s", -1, -1)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
            Log.w(TAG, "paused:$soFarBytes,$totalBytes")
            val percent = soFarBytes / totalBytes.toFloat()
            postValue(
                DownloadStatus.PAUSED,
                (percent * 100).toString() + "%",
                task.speed.toString() + " KB/s",
                soFarBytes,
                totalBytes
            )
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            e.printStackTrace()
            Log.e(TAG, "error:$task,$e")
            postValue(DownloadStatus.ERROR, "", e.message ?: "", -1, -1)
        }

        override fun warn(task: BaseDownloadTask) {
            Log.w(TAG, "warn:$task")
        }
    }

    companion object {
        private const val TAG = "FirmwareDownloader"
    }
}
