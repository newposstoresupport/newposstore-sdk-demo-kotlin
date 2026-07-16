package com.android.newpos.store.sdk.demo.register

import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentRegisterBinding
import com.newpos.store.android.sdk.base.BaseLog

/**
 * @ClassName : RegisterFragment
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:07
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class RegisterFragment: BaseFragment(R.layout.fragment_register) {
    //you can use this object to update ui
    private val _binding by viewBinding(FragmentRegisterBinding::bind)

    //you should put business code into viewmodel
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun getVM(): RegisterViewModel = registerViewModel

    override fun init() {
        super.init()

        BaseLog.d("RegisterFragment _binding: $_binding")
        BaseLog.d("RegisterFragment registerViewModel: $registerViewModel")


        val clientIdView: TextView = _binding.clientIdView
        clientIdView.text = AppUtils.getClientId()

        _binding.register.setOnClickListener { getVM().register() }

        _binding.clientId.setOnClickListener {
            val editText = EditText(requireActivity())
            editText.setHint(R.string.client_id_hint)
            editText.setText(AppUtils.getClientId())
            editText.filters = arrayOf(InputFilter.LengthFilter(30))
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.keyListener = DigitsKeyListener.getInstance("0123456789")
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.app_name)
                .setView(editText)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    val content = editText.text.toString()
                    if (TextUtils.isEmpty(content)) {
                        return@setPositiveButton
                    }
                    AppUtils.putClientId(content)
                    Toast.makeText(requireActivity(), R.string.update_success, Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                    clientIdView.text = AppUtils.getClientId()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }
}