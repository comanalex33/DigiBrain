package com.dig.digibrain.services.notifications

import android.app.*
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.os.Build
import android.os.Handler
import com.dig.digibrain.R
import com.dig.digibrain.activities.LoginActivity

const val THREE_DAYS_MILLIS = 3 * 24 * 60 * 60 * 1000   // Three days in millis

class UserActivityService: Service() {

    private val TAG = "UserActivityService"
    private val handler = Handler()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduleTask()
        return START_STICKY
    }

    private fun createNotification(): Notification {

        createNotificationChannel()

        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_book)
            .setContentTitle(applicationContext.getString(R.string.user_not_active_title))
            .setContentText(applicationContext.getString(R.string.user_not_active_short_message))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(applicationContext.getString(R.string.user_not_active_message)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    private fun scheduleTask() {
        handler.postDelayed({
            val settings: SharedPreferences = this.getSharedPreferences("application", MODE_PRIVATE)

            if (settings.getBoolean("notifications", true)) {
                if (settings.getLong("lastLoginTime", Long.MAX_VALUE) < System.currentTimeMillis() - THREE_DAYS_MILLIS) {
                    val notification = createNotification()
                    startForeground(1, notification)
                    Log.i(TAG, "Notification sent")
                }

            } else {
                Log.i(TAG, "Notifications are disabled")
            }

            scheduleTask()
        }, THREE_DAYS_MILLIS.toLong())
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= 26) {
            val name = "DigiBrainNotification"
            val description = "DigiBrain notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel("channel_id", name, importance)
            notificationChannel.description = description

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}