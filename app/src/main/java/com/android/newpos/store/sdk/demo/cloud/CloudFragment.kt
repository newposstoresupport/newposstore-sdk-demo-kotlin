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

    private val _binding by viewBinding(FragmentCloudBinding::bind)   // 使用 viewBinding 绑定布局
    private val cloudViewModel: CloudViewModel by viewModels()   // 获取 ViewModel 实例
    override fun getVM(): BaseViewModel = cloudViewModel   // 返回 ViewModel 实例

    private var cloudMessageReceiver: CloudMessageReceiver? = null  // 云消息接收器 可以为 null

    // 权限申请Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        Toast.makeText(requireContext(), "registerForActivityResult:$result", Toast.LENGTH_SHORT).show()
    }

    override fun init() {
        super.init()

        val filter = IntentFilter().apply {
            addAction(Constant.ACTION_CLOUD_MESSAGE_ARRIVED)
            addAction(Constant.ACTION_CLOUD_MESSAGE_CLICKED)
        }  // 创建 IntentFilter，添加云消息相关的 Action
        cloudMessageReceiver = CloudMessageReceiver()   // 实例化云消息接收器

        // Android 13+ 检查通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission()
        }  // 检查通知权限

        _binding.register.setOnClickListener {
            requireActivity().registerReceiver(
                cloudMessageReceiver,
                filter,
                Constant.PERMISSION_RECEIVE_CLOUD_MESSAGE,
                null,
                Context.RECEIVER_NOT_EXPORTED
            )  // 注册云消息接收器
            Toast.makeText(requireContext(), R.string.register_cloud_success, Toast.LENGTH_SHORT).show()
        }

        _binding.unRegister.setOnClickListener {
            requireActivity().unregisterReceiver(cloudMessageReceiver)
            Toast.makeText(requireContext(), R.string.un_register, Toast.LENGTH_SHORT).show()
        }  // 取消注册云消息接收器
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
