package com.android.newpos.store.sdk.demo.cloud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.newpos.store.sdk.demo.R
import com.android.newpos.store.sdk.demo.base.AppUtils
import com.newpos.store.android.sdk.Constant
import com.newpos.store.android.sdk.StoreSdk
import com.newpos.store.android.sdk.base.BaseLog
import org.json.JSONException
import org.json.JSONObject
/**
 * @ClassName : CloudMessageReceiver
 * @Author : zhouqiang(1376359644@qq.com)
 * @Email : newpos@newpostech.com
 * @Date : 2025/6/12-11:33
 * @Version : 1.0
 * @Description :
 * @website : <a href="https://www.newpostech.com/">...</a>
 */
class CloudMessageReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = CloudMessageReceiver::class.java.simpleName
        const val CM_CHANNEL_ID = "cloud message demo id"
        const val CM_CHANNEL_NAME = "Cloud Message Demo Name"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        Log.d(TAG, "onReceive:${intent.action}")

        if (Constant.ACTION_CLOUD_MESSAGE_ARRIVED == intent.action) {
            handleMessage(context, intent)
        }
    }

    private fun handleMessage(context: Context, intent: Intent) {
        val bundle = intent.extras ?: return
        val data = bundle.getString(Constant.CM_DATA)
        BaseLog.d("handleMessage>data:$data")
        try {
            val jsonObject = JSONObject(data)
            val cmd = jsonObject.getString("cmd")
            if (TextUtils.isEmpty(cmd)) return

            if (Constant.CLOUD_MESSAGE_TYPE_NOTIFICATION == cmd) {
                val title = jsonObject.getString("title")
                val content = jsonObject.getString("detail")
                val sound = bundle.getBoolean(Constant.CM_SOUND)
                val bubble = bundle.getBoolean(Constant.CM_BADGE)
                sendNotification(context, title, content, sound, bubble)
            }
            if (Constant.CLOUD_MESSAGE_TYPE_RKI_DOWN_CUSTOMER_KEYS == cmd) {
                val config = jsonObject.getJSONObject("config")
                val kdhUrl = config.getString("kdhUrl")
                val messageId = jsonObject.getString("messageId")
                if (TextUtils.isEmpty(kdhUrl)) return
                StoreSdk.getInstance().rkiAbility()
                    .downloadCustomerKeys(AppUtils.getClientId(), kdhUrl, messageId) { code, message, keyList ->
                        Toast.makeText(context, "onDownload:$code,$message,$keyList", Toast.LENGTH_SHORT).show()
                    }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun sendNotification(context: Context, title: String, content: String, sound: Boolean, bubble: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context, CM_CHANNEL_ID, CM_CHANNEL_NAME, sound, bubble)
        }
        val builder = createBuilder(context, title, content, CM_CHANNEL_ID, false)
        builder.setSmallIcon(R.mipmap.demo)

        var large = R.mipmap.demo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // large = R.mipmap.ic_launcher // 如有需要，兼容新版icon
        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, large))
        val notifyId = System.currentTimeMillis().toInt()
        getNotificationManager(context)?.notify(notifyId, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context, id: String, name: String, sound: Boolean, bubble: Boolean): NotificationChannel? {
        val notificationManager = getNotificationManager(context) ?: return null
        val channel = NotificationChannel(
            id, name,
            if (sound) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_MIN
        )
        channel.lightColor = Color.GREEN
        channel.enableLights(true)
        channel.setShowBadge(bubble)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channel
    }

    fun createBuilder(context: Context, title: String, content: String, channel: String, onGoing: Boolean): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.mipmap.demo)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setNumber(99)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setOngoing(onGoing)
    }

    fun getNotificationManager(context: Context): NotificationManager? {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }
}