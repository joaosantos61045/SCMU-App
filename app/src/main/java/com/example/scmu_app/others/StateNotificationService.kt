package com.example.scmu_app.others

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.scmu_app.R

class StateNotificationService(
    private val context: Context
) {

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel("state_changer", "State changer channel", importance).apply {
                    description = "A notification channel for changes of state reminders"
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBasicNotification(state: Int) {

        var string= ""
        if(state==0)string="The watering system has begun!"
        else if(state==2) string="The watering system has ended!"
        val notification = NotificationCompat.Builder(context, "state_changer")
            .setContentTitle("Watering System")
            .setContentText(string)
            .setSmallIcon(R.drawable.icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) { return }
            notify(1, notification)
        }

    }

    /*fun showExpandableNotification() {
        val image = context.bitmapFromResource(R.drawable.image01)

        val notification = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image01)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLargeIcon(image)
            .setStyle(
                NotificationCompat
                    .BigPictureStyle()
                    .bigPicture(image)
                    .bigLargeIcon(null as Bitmap?)
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showExpandableLongText() {
        val notification = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image01)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText("Very big text")
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showInboxStyleNotification() {
        val notification = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image01)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .addLine("Line 1")
                    .addLine("Line 2")
                    .addLine("Line 3")
                    .addLine("Line 4")
                    .addLine("Line 5")
                    .addLine("Line 6")
                    .addLine("Line 7")
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }

    fun showNotificationGroup() {
        val groupId = "water_group"
        val summaryId = 0

        val notification1 = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image01)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .addLine("Line 1")
            )
            .setAutoCancel(true)
            .setGroup(groupId)
            .build()

        val notification2 = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .addLine("Line 1")
                    .addLine("Line 2")
            )
            .setAutoCancel(true)
            .setGroup(groupId)
            .build()

        val summaryNotification = NotificationCompat.Builder(context, "water_reminder")
            .setContentTitle("Water Reminder")
            .setContentText("Time to drink some water!")
            .setSmallIcon(R.drawable.image)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .setSummaryText("Water reminders missed")
                    .setBigContentTitle("Water Reminders")
            )
            .setAutoCancel(true)
            .setGroup(groupId)
            .setGroupSummary(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification1
        )
        notificationManager.notify(
            Random.nextInt(),
            notification2
        )
        notificationManager.notify(
            Random.nextInt(),
            summaryNotification
        )
    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId: Int
    ) = BitmapFactory.decodeResource(
        resources,
        resId
    )

     */
}