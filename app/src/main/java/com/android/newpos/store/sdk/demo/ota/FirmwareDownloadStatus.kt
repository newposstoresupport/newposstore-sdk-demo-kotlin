package com.android.newpos.store.sdk.demo.ota

import com.android.newpos.store.sdk.demo.app.DownloadStatus
import com.newpos.store.android.sdk.dto.FirmwareInfo

/**
 * 固件下载状态（对齐 Java demo FirmwareDownloadStatus）
 */
class FirmwareDownloadStatus {
    var firmwareInfo: FirmwareInfo? = null
    var percent: String? = null
    var speed: String? = null
    var sofar: Int = 0
    var total: Int = 0
    var downloadStatus: DownloadStatus? = null

    override fun toString(): String {
        return "FirmwareDownloadStatus{" +
                "firmwareInfo=$firmwareInfo" +
                ", percent='$percent'" +
                ", speed='$speed'" +
                ", sofar='$sofar'" +
                ", total='$total'" +
                ", downloadStatus=$downloadStatus" +
                '}'
    }
}
