package com.android.newpos.store.sdk.demo.lbs

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentLbsBinding


/**
 * @ClassName : LbsFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:23
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class LbsFragment : BaseFragment(R.layout.fragment_lbs) {
    private val _binding by viewBinding(FragmentLbsBinding::bind)

    private val lbsViewModel: LbsViewModel by viewModels()
    override fun getVM(): LbsViewModel = lbsViewModel

    override fun init() {
        super.init()
        lbsViewModel.mLocation.observe(viewLifecycleOwner) { location ->
            _binding.location.text = location
        }

        _binding.getLocation.setOnClickListener {
            lbsViewModel.getLocation()
        }

        getVM().logs.observe(viewLifecycleOwner){ text -> appendLog(text)}
    }

    @SuppressLint("SetTextI18n")
    fun appendLog(text: String) {
        val old: String = _binding.tvLbsResult.text.toString()
        _binding.tvLbsResult.text = if (old.isEmpty()) {
            text
        } else {
            "$old\n$text"
        }
        _binding.scrollResult.post { _binding.scrollResult.fullScroll(View.FOCUS_DOWN) }
    }
}
