package com.android.newpos.store.sdk.demo.rki

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentRkiBinding
import com.newpos.store.android.sdk.StoreSdk

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

            rkiViewModel.download()
        }

        getVM().mKdh.observe(viewLifecycleOwner) { text ->
            appendLog(text)
        }
    }

    @SuppressLint("SetTextI18n")
    fun appendLog(text: String) {
        val old: String = _binding.tvRkiResult.text.toString()
        _binding.tvRkiResult.setText(
            """
            $old
            $text
        """.trimIndent()
        )
        _binding.scrollResult.post { _binding.scrollResult.fullScroll(View.FOCUS_DOWN) }
    }

}