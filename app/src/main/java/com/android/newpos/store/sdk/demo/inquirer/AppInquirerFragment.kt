package com.android.newpos.store.sdk.demo.inquirer

import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentAppInquirerBinding

/**
 * @ClassName : AppInquirerFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:22
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class AppInquirerFragment: BaseFragment(R.layout.fragment_app_inquirer) {
    private val _binding by viewBinding(FragmentAppInquirerBinding::bind)

    private val appInquirerViewModel: AppInquirerViewModel by viewModels()  // 获取 ViewModel 实例
    override fun getVM(): BaseViewModel = appInquirerViewModel

    override fun init() {
        super.init()

        appInquirerViewModel.queryStatus()
        appInquirerViewModel.getStatus().observe(viewLifecycleOwner) { status ->
            val title = "App Inquirer Status:"
            _binding.status.text = "$title$status"
            _binding.ready.isEnabled = status
            _binding.ready.isChecked = appInquirerViewModel.isReadyToUpdate()
        }

        appInquirerViewModel.getAppStatus().observe(viewLifecycleOwner) { appIsReadyToUpdate ->
            if (appIsReadyToUpdate) {
                _binding.appStatus.text = "App is idl, could be updated by STORE CLIENT APP now!"
            } else {
                _binding.appStatus.text = "App is busy, can not be updated by STORE CLIENT APP now!"
            }
        }

        _binding.ready.setOnCheckedChangeListener { _, isChecked ->
            appInquirerViewModel.updateAppStatus(isChecked)
        }
    }
}