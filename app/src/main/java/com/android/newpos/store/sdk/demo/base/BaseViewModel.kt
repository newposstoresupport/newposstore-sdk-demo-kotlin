package com.android.newpos.store.sdk.demo.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.newpos.store.android.sdk.base.BaseException
import com.newpos.store.android.sdk.base.BaseLog
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * @ClassName : BaseViewModel
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:36
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
open class BaseViewModel(title: String, application: Application): AndroidViewModel(Application()) {
    val mText: MutableLiveData<String> = MutableLiveData<String>().apply { value = title }

    val mDialog = MutableLiveData<String>()
    var executorService: ExecutorService? = null

    fun getService(): ExecutorService {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool()
        }
        return executorService!!
    }

    // 协程作用域，替代RxJava进行异步任务管理
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    fun getText(): MutableLiveData<String> {
        return mText
    }

    fun getDialog(): MutableLiveData<String> {
        return mDialog
    }


    protected open fun showError(throwable: Throwable) {
        var msg = throwable.message
        if (throwable is BaseException) {
            msg = throwable.msg
        }
        mDialog.postValue(msg!!)
    }

    override fun onCleared() {
        super.onCleared()
        BaseLog.d("BaseViewModel>onCleared")
        executorService?.shutdownNow()
        viewModelScope.cancel()
    }

    class DialogInfo {
        var show: Boolean = false
        var info: String = ""

        constructor(show: Boolean) {
            this.show = show
        }

        constructor(show: Boolean, info: String) {
            this.show = show
            this.info = info
        }
    }
}