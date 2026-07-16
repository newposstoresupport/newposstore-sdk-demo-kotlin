package com.android.newpos.store.sdk.demo.cloud

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.ToastUtils.showToast
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentCloudBinding
import com.newpos.store.android.sdk.Constant

/**
 * @ClassName : CloudFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:20
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class CloudFragment : BaseFragment(R.layout.fragment_cloud) {

    private val _binding by viewBinding(FragmentCloudBinding::bind)
    private val cloudViewModel: CloudViewModel by viewModels()
    override fun getVM(): BaseViewModel = cloudViewModel

    private var cloudMessageReceiver: CloudMessageReceiver? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        showToast("registerForActivityResult:$result")
    }

    override fun init() {
        super.init()

        val filter = IntentFilter().apply {
            addAction(Constant.ACTION_CLOUD_MESSAGE_ARRIVED)
            addAction(Constant.ACTION_CLOUD_MESSAGE_CLICKED)
        }
        cloudMessageReceiver = CloudMessageReceiver()   // 实例化云消息接收器

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission()
        }

        _binding.register.setOnClickListener {
            requireActivity().registerReceiver(
                cloudMessageReceiver,
                filter,
                Constant.PERMISSION_RECEIVE_CLOUD_MESSAGE,
                null,
                Context.RECEIVER_NOT_EXPORTED
            )
            Toast.makeText(requireContext(), R.string.register_cloud_success, Toast.LENGTH_SHORT).show()
        }

        _binding.unRegister.setOnClickListener {
            try {
                requireActivity().unregisterReceiver(cloudMessageReceiver)
                Toast.makeText(requireContext(), R.string.un_register, Toast.LENGTH_SHORT).show()
            } catch (e: IllegalArgumentException) {
                showToast("Receiver unregisted")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cloudMessageReceiver?.let {
                requireActivity().unregisterReceiver(it)
            }
        } catch (_: Exception) { }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
        val manager = requireContext().getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (manager?.areNotificationsEnabled() == false) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
