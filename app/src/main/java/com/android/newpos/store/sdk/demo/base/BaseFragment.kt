package com.android.newpos.store.sdk.demo.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.newpos.store.sdk.demo.R

/**
 * @ClassName : BaseFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/1/24-10:39
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
abstract class BaseFragment(@LayoutRes contentId: Int): Fragment(contentId) {

    lateinit var mViewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = getVM()

        val textView = view.findViewById<TextView>(R.id.text)
        mViewModel.mText.observe(viewLifecycleOwner, textView::setText)

        mViewModel.mDialog.observe(viewLifecycleOwner) { message ->
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(R.string.i_see) { dialog, _ -> dialog.dismiss() }
                .create().show()
        }

        init()
    }

    abstract fun getVM(): BaseViewModel

    override fun onResume() {
        super.onResume()
    }

    protected open fun init(){

    }
}