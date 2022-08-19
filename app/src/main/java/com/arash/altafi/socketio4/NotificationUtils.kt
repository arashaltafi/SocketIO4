package com.arash.altafi.socketio4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

object NotificationUtils {

    fun send(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        bodyBig: String? = null,
        button: String? = null,
        image: Bitmap? = null,
        notifId: Int = (0..1000000).random(),
        pendingIntent: PendingIntent? = null,
        buttonPendingIntent: PendingIntent? = null,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setChannelId(channelId)

            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(image)

            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)

            .setContentTitle(setPersianDigits(title))
            .setContentText(setPersianDigits(body))

            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setSound(defaultSoundUri)// api.version < android.O
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(bodyBig.takeIf { it.isNullOrEmpty() })
            )

        button?.let {
            notificationBuilder
                .addAction(
                    R.drawable.shp_transparent,
                    button,
                    buttonPendingIntent
                )
        }

        notificationBuilder.setContentIntent(pendingIntent)
        notificationManager.notify(notifId, notificationBuilder.build())
    }

    /**
     * create channel if is not created
     *
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(
        context: Context,
        channelId: String,
        name: String,
        descriptionText: String = "",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        visibility: Int = Notification.VISIBILITY_PUBLIC,
        groupId: String? = null,
        soundUri: Uri? = null,
        showBadge: Boolean = true,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val isChannelCreated = try {
            val notificationChannel = notificationManager.getNotificationChannel(channelId)
            Log.d("notificationUtils", "channel is created= ${notificationChannel.name}")
            true
        } catch (e: Exception) {
            false
        }

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val soundURI = soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (isChannelCreated.not()) {
            val mChannel = NotificationChannel(channelId, name, importance).apply {
                groupId?.let {
                    group = it
                }
                description = descriptionText
                lockscreenVisibility = visibility
                when (importance) {
                    NotificationManager.IMPORTANCE_HIGH,
                    NotificationManager.IMPORTANCE_DEFAULT -> {
                        setSound(soundURI, audioAttributes)
                    }
                    NotificationManager.IMPORTANCE_LOW -> {
                        if (soundUri != null)
                            setSound(soundURI, audioAttributes)
                    }
                }
                setShowBadge(showBadge)
            }
            notificationManager.createNotificationChannel(mChannel)
        }

    }

    fun setPersianDigits(src: String?): String? {
        val result = StringBuilder("")
        var unicode = 0
        if (src != null) {
            for (i in src.indices) {
                unicode = src[i].code
                if (unicode in 48..57) {
                    result.append((unicode + 1728).toChar())

                    // Log.e("unicode", unicode + "");
                    // Log.e("persian character", (unicode + 1728) + " " + (char) (unicode + 1728));
                } else {
                    result.append(src[i])
                }
            }
        }
        return result.toString()
    }

}