package com.android.newpos.store.sdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.newpos.store.sdk.demo.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.newpos.store.android.sdk.StoreSdk

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            Snackbar.make(it,
                getString(R.string.sdk_version, StoreSdk.getInstance().version),
                Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle(R.string.result_title)
//                    .setMessage(auth)
//                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
//                    .create().show();
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment())
                .commit()

        //TODO 跳转github
    }
}