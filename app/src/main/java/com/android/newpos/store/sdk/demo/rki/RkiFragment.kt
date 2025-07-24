package com.android.newpos.store.sdk.demo.rki

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentRkiBinding
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.BaseLog

/**
 * @ClassName : RkiFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:27
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class RkiFragment : BaseFragment(R.layout.fragment_rki) {
    private val _binding by viewBinding(FragmentRkiBinding::bind)

    private val rkiViewModel: RkiViewModel by viewModels()
    override fun getVM(): BaseViewModel = rkiViewModel

    override fun init() {
        super.init()

        _binding.bind.setOnClickListener {
            val result = StoreSdk.getInstance().rkiAbility().bindRkiService()
            Toast.makeText(
                requireContext(),
                if (result) "bind success" else "bind failed",
                Toast.LENGTH_SHORT
            ).show()
        }

        _binding.downloadCustomer.setOnClickListener {
            Toast.makeText(
                requireContext(),
                R.string.download_customer_keys_prompt,
                Toast.LENGTH_SHORT
            ).show()
            val kdhUrl = "http://10.1.42.63:6700/rki/" //from newstore platform
            StoreSdk.getInstance().rkiAbility().downloadCustomerKeys(
                AppUtils.getClientId(),
                kdhUrl,
                ""
            ) { code, message, keyList ->
                BaseLog.d("onDownload:$code $message $keyList");
            }
        }

    }
}