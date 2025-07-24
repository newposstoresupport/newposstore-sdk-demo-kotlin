package com.android.newpos.store.sdk.demo.param

import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentParamBinding
import com.newpos.store.android.sdk.base.BaseLog
import com.newpos.store.android.sdk.dto.AttachFile
import com.newpos.store.android.sdk.dto.ParamDownloadResponse

/**
 * @ClassName : ParamFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:26
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */

class ParamFragment : BaseFragment(R.layout.fragment_param) {
    private val _binding by viewBinding(FragmentParamBinding::bind)

    private val paramViewModel: ParamViewModel by viewModels()
    override fun getVM(): BaseViewModel = paramViewModel

    override fun init() {
        super.init()

        BaseLog.d("ParamFragment _binding: $_binding")
        BaseLog.d("ParamFragment paramViewModel: $paramViewModel")

        _binding.query.setOnClickListener { paramViewModel.queryParamFile() }
        _binding.download.setOnClickListener { paramViewModel.downloadParamFile() }

        paramViewModel.mInfo.observe(viewLifecycleOwner) {
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.query_params_result)
                .setMessage(it)
                .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        paramViewModel.paramDownloadResponseMutableLiveData.observe(getViewLifecycleOwner()) { paramDownloadResponse ->
            AlertDialog.Builder(requireActivity())
                .setTitle("Download Params Result:")
                .setMessage(paramDownloadResponse.toString())
                .setPositiveButton("View Files") { dialog, _ ->
                    dialog.dismiss();
                    viewFiles(paramDownloadResponse)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        paramViewModel.showFileContent.observe(getViewLifecycleOwner()) {
            AlertDialog.Builder(requireActivity())
                .setMessage(it)
                .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                .create().show()
        }
    }

    fun viewFiles(paramDownloadResponse: ParamDownloadResponse) {
        val attachFiles: List<AttachFile> = paramDownloadResponse.attachFiles ?: run {
            Toast.makeText(context, "attachFile is null!", Toast.LENGTH_SHORT).show()
            return
        }
        val strings = mutableListOf<String>()
        attachFiles.forEach {
            strings.add(it.filePath)
        }
        val listView = ListView(requireActivity())
        listView.adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, strings)
        listView.setOnItemClickListener { _, _, position, _ ->
            paramViewModel.readFile(strings[position])
        }
        AlertDialog.Builder(requireActivity())
            .setTitle(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?.absolutePath
            )
            .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
            .setView(listView)
            .create().show()
    }
}