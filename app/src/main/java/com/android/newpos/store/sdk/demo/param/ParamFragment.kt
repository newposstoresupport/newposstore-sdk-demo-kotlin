package com.android.newpos.store.sdk.demo.param

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.DownloadWorker
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentParamBinding
import com.newpos.store.android.sdk.base.BaseLog
import com.newpos.store.android.sdk.dto.AttachFile
import com.newpos.store.android.sdk.dto.ParamDownV2Result
import com.newpos.store.android.sdk.dto.ParamDownloadResponse
import java.io.File

/**
 * 参数下载演示（含 Param V2）
 */
class ParamFragment : BaseFragment(R.layout.fragment_param) {
    private val _binding by viewBinding(FragmentParamBinding::bind)
    private var msgId: String? = null

    private val paramViewModel: ParamViewModel by viewModels()
    override fun getVM(): BaseViewModel = paramViewModel

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadWorker.ACTION_DOWNLOAD_FINISH) {
                BaseLog.w("download worker has finished")
                paramViewModel.dismiss()
                val result = intent.getBooleanExtra(DownloadWorker.KEY_RESULT, false)
                if (result) {
                    val dir = intent.getStringExtra(DownloadWorker.KEY_PATH_DIR)
                    if (!TextUtils.isEmpty(dir)) {
                        viewFilesV2Ex(dir!!)
                    }
                } else {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Download Params Result(V2):")
                        .setMessage("Download failed, please check log!")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create().show()
                }
            }
        }
    }

    override fun init() {
        super.init()
        BaseLog.d("ParamFragment msgId:$msgId")

        requireContext().registerReceiver(receiver, IntentFilter(DownloadWorker.ACTION_DOWNLOAD_FINISH))

        _binding.query.setOnClickListener { paramViewModel.queryParamFile() }
        _binding.download.setOnClickListener { paramViewModel.downloadParamFile() }
        _binding.v2Query.setOnClickListener { paramViewModel.queryParamFileV2() }
        _binding.v2Download.setOnClickListener { paramViewModel.downloadParamFileV2() }
        _binding.v2DownloadOnekey.setOnClickListener { paramViewModel.downloadOneKey(msgId) }

        paramViewModel.mInfo.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.query_params_result)
                .setMessage(it)
                .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        paramViewModel.paramDownloadResponseMutableLiveData.observe(viewLifecycleOwner) { paramDownloadResponse ->
            AlertDialog.Builder(requireActivity())
                .setTitle("Download Params Result:")
                .setMessage(paramDownloadResponse.toString())
                .setPositiveButton("View Files") { dialog, _ ->
                    dialog.dismiss()
                    viewFiles(paramDownloadResponse)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        paramViewModel.v2ResultLiveData.observe(viewLifecycleOwner) { results ->
            AlertDialog.Builder(requireActivity())
                .setTitle("Download Params Result(V2):")
                .setMessage(results.toString())
                .setPositiveButton("View Files") { dialog, _ ->
                    dialog.dismiss()
                    viewFilesV2(results)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        paramViewModel.showFileContent.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireActivity())
                .setMessage(it)
                .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        if (!TextUtils.isEmpty(msgId)) {
            paramViewModel.downloadOneKey(msgId)
        }
    }

    override fun onDestroyView() {
        try {
            requireContext().unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
        super.onDestroyView()
    }

    fun viewFiles(paramDownloadResponse: ParamDownloadResponse) {
        val attachFiles: List<AttachFile> = paramDownloadResponse.attachFiles ?: run {
            Toast.makeText(context, "attachFile is null!", Toast.LENGTH_SHORT).show()
            return
        }
        val strings = mutableListOf<String>()
        attachFiles.forEach { it.filePath?.let { path -> strings.add(path) } }
        val listView = ListView(requireActivity())
        listView.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, strings)
        listView.setOnItemClickListener { _, _, position, _ ->
            paramViewModel.readFile(strings[position])
        }
        AlertDialog.Builder(requireActivity())
            .setTitle(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath)
            .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
            .setView(listView)
            .create().show()
    }

    fun viewFilesV2(paramDownV2Results: List<ParamDownV2Result>) {
        val strings = mutableListOf<String>()
        val filePaths = mutableListOf<String>()
        for (v2Result in paramDownV2Results) {
            for (attachFile in v2Result.attachFiles) {
                val dir = attachFile.fileDir ?: continue
                val files = File(dir).listFiles() ?: continue
                for (file in files) {
                    strings.add(v2Result.packageName + "[" + file + "]")
                    filePaths.add(file.absolutePath)
                }
            }
        }
        val listView = ListView(requireActivity())
        listView.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, strings)
        listView.setOnItemClickListener { _, _, position, _ ->
            paramViewModel.readFile(filePaths[position])
        }
        AlertDialog.Builder(requireActivity())
            .setTitle(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath)
            .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
            .setView(listView)
            .create().show()
    }

    fun viewFilesV2Ex(dir: String) {
        val files = File(dir).listFiles() ?: return
        val strings = files.map { it.absolutePath }
        val listView = ListView(requireActivity())
        listView.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, strings)
        listView.setOnItemClickListener { _, _, position, _ ->
            paramViewModel.readFile(strings[position])
        }
        AlertDialog.Builder(requireActivity())
            .setTitle(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath)
            .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
            .setView(listView)
            .create().show()
    }
}
