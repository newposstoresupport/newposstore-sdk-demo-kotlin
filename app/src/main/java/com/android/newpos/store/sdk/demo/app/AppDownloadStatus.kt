package com.android.newpos.store.sdk.demo.app

import com.newpos.store.android.sdk.dto.StoreApp

/**
 * @ClassName : AppDownloadStatus
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/8/5-16:55
 * @Version : 1.0
 * @Description :
 * @website : [...](https://www.newpostech.com/)
 */
class AppDownloadStatus {
    lateinit var storeApp: StoreApp
    var pack: String? = null
    var percent: String? = null
    var downloadStatus: DownloadStatus? = null

    override fun toString(): String {
        return "AppDownloadStatus{" +
                "pack='" + pack + '\'' +
                ", percent='" + percent + '\'' +
                ", downloadStatus=" + downloadStatus +
                '}'
    }
}