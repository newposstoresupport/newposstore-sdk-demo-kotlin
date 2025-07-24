package com.android.newpos.store.sdk.demo.base

import com.tencent.mmkv.MMKV

/**
 * @ClassName : AppUtils
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:01
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
object AppUtils {
    private val mmkv = MMKV.defaultMMKV()

    private const val CLIENT_ID = "clientId"

    fun putClientId(clientId: String) = mmkv.encode(CLIENT_ID, clientId)

    fun getClientId() = mmkv.decodeString(CLIENT_ID)
}