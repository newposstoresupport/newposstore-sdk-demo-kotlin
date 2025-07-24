package com.android.newpos.store.sdk.demo.ota

import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentOtaBinding

/**
 * @ClassName : OtaFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:25
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class OTAFragment: BaseFragment(R.layout.fragment_ota) {
    private val _binding by viewBinding(FragmentOtaBinding::bind)

    private val otaViewModel: OTAViewModel by viewModels()
    override fun getVM(): BaseViewModel = otaViewModel

    override fun init() {
        super.init()
    }
}