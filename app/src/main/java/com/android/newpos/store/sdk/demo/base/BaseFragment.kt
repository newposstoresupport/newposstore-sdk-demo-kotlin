package com.android.newpos.store.sdk.demo.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.newpos.store.sdk.demo.MainActivity
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
        setHasOptionsMenu(true)
        (activity as MainActivity).updateHeaderVisibility(false)
        val textView = view.findViewById<TextView>(R.id.text)
        mViewModel = getVM()

        mViewModel.mText.observe(viewLifecycleOwner) { textView.text = it }
        mViewModel.mDialog.observe(viewLifecycleOwner){
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.error_title)
                .setMessage(it)
                .setPositiveButton(R.string.i_see, null)
                .show()
        }
        mViewModel.getLoading().observe(viewLifecycleOwner){
            if (it.loading) {
                LoadingDialogManage.getInstance()
                    .show(requireActivity(), it.loadingText)
            } else {
                LoadingDialogManage.getInstance().dismiss()
            }
        }

        init()
    }

    abstract fun getVM(): BaseViewModel

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).updateHeaderVisibility(true)
        mViewModel.dismissLoading()
        mViewModel.getService().shutdownNow()
    }

    protected open fun init(){
    }
}