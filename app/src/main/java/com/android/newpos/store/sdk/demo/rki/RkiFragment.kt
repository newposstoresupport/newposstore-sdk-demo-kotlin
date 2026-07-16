package com.android.newpos.store.sdk.demo.rki

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.ToastUtils.showToast
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentRkiBinding

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
    override fun getVM(): RkiViewModel = rkiViewModel

    override fun init() {
        super.init()
        _binding.bind.setOnClickListener { getVM().bind() }

        _binding.downloadCustomer.setOnClickListener {rkiViewModel.download()}

        getVM().mKdh.observe(viewLifecycleOwner) {text -> appendLog(text)}
    }

    @SuppressLint("SetTextI18n")
    fun appendLog(text: String) {
        val old = _binding.tvRkiResult.text.toString()
        _binding.tvRkiResult.text = if (old.isEmpty()) {
            text
        } else {
            "$old\n$text"
        }
        _binding.scrollResult.post { _binding.scrollResult.fullScroll(View.FOCUS_DOWN) }
    }

}