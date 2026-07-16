package com.android.newpos.store.sdk.demo.base

import android.widget.TextView
import com.android.desert.baserecyle.BaseQuickAdapter
import com.android.desert.baserecyle.BaseViewHolder
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.app.AppDownloadStatus
import com.android.newpos.store.sdk.demo.app.AppItemListener
import com.android.newpos.store.sdk.demo.app.DownloadStatus
import com.newpos.store.android.sdk.base.BaseUtils
import com.newpos.store.android.sdk.dto.StoreApp

class AppAdapter(layoutResId: Int, data: List<AppDownloadStatus>) :
    BaseQuickAdapter<AppDownloadStatus, BaseViewHolder>(layoutResId, data) {

    private var appItemListener: AppItemListener? = null

    fun setListener(listener: AppItemListener) {
        this.appItemListener = listener
    }

    override fun convert(baseViewHolder: BaseViewHolder, appDownloadStatus: AppDownloadStatus) {
        val storeApp: StoreApp = appDownloadStatus.storeApp
        baseViewHolder.setText(R.id.text, storeApp.progName)
        baseViewHolder.setText(R.id.version, storeApp.verName)
        baseViewHolder.setText(R.id.size, BaseUtils.formatSize(storeApp.appSize))

        val textView: TextView = baseViewHolder.getView(R.id.percent)
        when (appDownloadStatus.downloadStatus) {
            DownloadStatus.START -> textView.text = "pending"
            DownloadStatus.DOWNLOADING -> textView.text = appDownloadStatus.percent
            DownloadStatus.DOWNLOADED -> textView.text = "downloaded"
            DownloadStatus.INSTALLING -> textView.text = "installing"
            DownloadStatus.INSTALLED -> textView.text = "installed"
            DownloadStatus.PAUSED -> textView.text = "paused"
            DownloadStatus.ERROR -> textView.text = "error"
            null -> TODO()
        }

        baseViewHolder.getView<TextView>(R.id.btn).setOnClickListener {
            appItemListener?.onClick(storeApp)
        }
    }
}
