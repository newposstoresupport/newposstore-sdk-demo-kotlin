package com.android.newpos.store.sdk.demo.ota

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.BaseFragment
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import com.android.newpos.store.sdk.demo.base.viewBinding
import com.android.newpos.store.sdk.demo.databinding.FragmentOtaBinding

/**
 * OTA 固件演示页
 */
class OTAFragment : BaseFragment(R.layout.fragment_ota) {
    private val _binding by viewBinding(FragmentOtaBinding::bind)

    private val otaViewModel: OTAViewModel by viewModels()
    override fun getVM(): BaseViewModel = otaViewModel

    override fun init() {
        super.init()

        _binding.query.setOnClickListener { otaViewModel.queryFirmware() }
        _binding.download.setOnClickListener { otaViewModel.downloadFirmware() }
        _binding.install.setOnClickListener {
            // SystemManager.updateFirmware() — 按终端 ROM 能力自行接入
        }

        otaViewModel.mInfo.observe(viewLifecycleOwner) { content ->
            AlertDialog.Builder(requireActivity())
                .setTitle("Query Firmware Result:")
                .setMessage(content)
                .create().show()
        }

        otaViewModel.statusMutableLiveData.observe(viewLifecycleOwner) { status ->
            _binding.speed.text = status.speed
            _binding.url.text = status.firmwareInfo?.url
            if (status.total == -1) {
                _binding.progressBar.isIndeterminate = true
            } else {
                _binding.progressBar.max = status.total
                _binding.progressBar.progress = status.sofar
            }
            _binding.detail.text = String.format(
                "sofar: %d total: %d percent:%s",
                status.sofar, status.total, status.percent
            )
        }
    }
}
