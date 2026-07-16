package com.android.newpos.store.sdk.demo.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.AppAdapter
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentAppBinding

class AppFragment : BaseFragment(R.layout.fragment_app) {

    private val _binding by viewBinding(FragmentAppBinding::bind)

    private val appViewModel: AppViewModel by viewModels()
    override fun getVM(): BaseViewModel = appViewModel

    override fun init(){
        super.init()
        _binding.getApps.setOnClickListener{
            appViewModel.getAppsByPackageName()
        }
    }
}
