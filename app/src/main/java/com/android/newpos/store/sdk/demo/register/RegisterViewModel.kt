package com.android.newpos.store.sdk.demo.register

import androidx.lifecycle.viewModelScope
import com.android.newpos.store.sdk.demo.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @ClassName : RegisterViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:17
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class RegisterViewModel: BaseViewModel(title = "Registration") {
    //you should use coroutines to dev async business
    fun testCoroutines() = viewModelScope.launch {
        delay(1000)
        mDialog.postValue("Delay 1 second pop-up window!")
    }
}