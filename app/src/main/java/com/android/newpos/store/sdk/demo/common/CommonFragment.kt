package com.android.newpos.store.sdk.demo.common

import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentCommonBinding
import com.newpos.store.android.sdk.StoreSdk

/**
 * @ClassName : CommonFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/7/31-17:04
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class CommonFragment : BaseFragment(R.layout.fragment_common) {

    private val _binding by viewBinding(FragmentCommonBinding::bind)

    private val commonviewmodel: CommonViewModel by viewModels()
    override fun getVM(): BaseViewModel = commonviewmodel

    override fun init() {
        super.init()

        // TODO 考虑增加输入包名或者选择列表
        // TODO Consider adding input package name or selecting from a list
        _binding.openAppDetail.setOnClickListener {
            StoreSdk.getInstance().openAppDetail("com.newpos.rki")
        }

        _binding.openDownloadList.setOnClickListener {
            StoreSdk.getInstance().openDownloadList()
        }

        _binding.openSystemUpdate.setOnClickListener {
            StoreSdk.getInstance().openOtaUpdate()
        }
    }
}
