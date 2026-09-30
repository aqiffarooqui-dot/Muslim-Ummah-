package com.example.ui.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.DailyPrayerTimes
import com.example.data.model.PrayerCalculator
import java.util.Calendar

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("PRAYER_NAME") ?: "Prayer"
        val arabicName = intent.getStringExtra("ARABIC_NAME") ?: "الصلاة"
        val reminderType = intent.getStringExtra("REMINDER_TYPE") ?: "ADHAN"

        if (reminderType == "DAILY_QURAN") {
            PrayerNotificationManager.showDailyReminder(
                context,
                "Quran Reminder",
                "Have you recited your daily portion of the Holy Quran today? 'Indeed, this Quran guides to that which is most suitable.'"
            )
        } else if (reminderType == "DAILY_HADITH") {
            PrayerNotificationManager.showDailyReminder(
                context,
                "Daily Hadith",
                "The Prophet (ﷺ) said: 'The best among you are those who learn the Quran and teach it.' (Sahih al-Bukhari)"
            )
        } else {
            PrayerNotificationManager.showPrayerNotification(context, prayerName, arabicName)
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_TIMEZONE_CHANGED ||
            intent.action == Intent.ACTION_TIME_CHANGED
        ) {
            Log.i("BootReceiver", "Device rebooted or timezone changed. Rescheduling prayer alarms.")
            val defaultCity = PrayerCalculator.POPULAR_CITIES.first()
            val times = PrayerCalculator.calculateTimes(
                calendar = Calendar.getInstance(),
                latitude = defaultCity.latitude,
                longitude = defaultCity.longitude,
                timezoneOffsetHours = defaultCity.timezoneOffsetHours
            )
            PrayerNotificationManager.schedulePrayerNotifications(context, times, true)
        }
    }
}

object PrayerNotificationManager {
    private const val TAG = "PrayerNotificationMgr"

    const val CHANNEL_PRAYER = "channel_prayer_times"
    const val CHANNEL_REMINDERS = "channel_daily_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER,
                "Adhan & Prayer Times",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely Adhan calls and notifications for Fajr, Dhuhr, Asr, Maghrib, and Isha."
                enableVibration(true)
            }

            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Daily Islamic Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily inspirational reminders for Quran reading and prophetic Hadith contemplation."
            }

            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }

    fun showPrayerNotification(context: Context, prayerName: String, arabicName: String) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRAYER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time for $prayerName ($arabicName)")
            .setContentText("Hayya 'ala-s-Salah! Come to prayer. Keep your connection with Allah strong.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)
    }

    fun showDailyReminder(context: Context, title: String, message: String) {
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(title.hashCode(), notification)
    }

    fun schedulePrayerNotifications(context: Context, prayerTimes: DailyPrayerTimes, enabled: Boolean) {
        createNotificationChannels(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val prayersToSchedule = listOf(
            prayerTimes.fajr,
            prayerTimes.dhuhr,
            prayerTimes.asr,
            prayerTimes.maghrib,
            prayerTimes.isha
        )

        val now = System.currentTimeMillis()

        for (prayer in prayersToSchedule) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("PRAYER_NAME", prayer.name)
                putExtra("ARABIC_NAME", prayer.arabicName)
                putExtra("REMINDER_TYPE", "ADHAN")
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayer.name.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!enabled) {
                alarmManager.cancel(pendingIntent)
            } else if (prayer.timeMillis > now) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            prayer.timeMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, prayer.timeMillis, pendingIntent)
                    }
                    Log.d(TAG, "Scheduled alarm for ${prayer.name} at ${prayer.timeString}")
                } catch (e: SecurityException) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, prayer.timeMillis, pendingIntent)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to schedule alarm for ${prayer.name}", e)
                }
            }
        }
    }

    fun scheduleDailyReminders(context: Context, quranEnabled: Boolean, hadithEnabled: Boolean) {
        createNotificationChannels(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 1. Daily Quran reminder at 09:00 AM
        val quranIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("REMINDER_TYPE", "DAILY_QURAN")
        }
        val quranPendingIntent = PendingIntent.getBroadcast(
            context,
            9001,
            quranIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!quranEnabled) {
            alarmManager.cancel(quranPendingIntent)
        } else {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            try {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    quranPendingIntent
                )
            } catch (e: Exception) {
                Log.w(TAG, "Could not set repeating Quran reminder: ${e.message}")
            }
        }

        // 2. Daily Hadith reminder at 08:00 PM
        val hadithIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("REMINDER_TYPE", "DAILY_HADITH")
        }
        val hadithPendingIntent = PendingIntent.getBroadcast(
            context,
            9002,
            hadithIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!hadithEnabled) {
            alarmManager.cancel(hadithPendingIntent)
        } else {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            try {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    hadithPendingIntent
                )
            } catch (e: Exception) {
                Log.w(TAG, "Could not set repeating Hadith reminder: ${e.message}")
            }
        }
    }
}
