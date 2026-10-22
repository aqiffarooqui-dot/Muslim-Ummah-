package com.example.ui.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
        } else if (reminderType == "DAILY_DUA") {
            PrayerNotificationManager.showDailyReminder(
                context,
                "Daily Dua Reminder",
                "Take a moment for dhikr and supplication. Verify dua wording and references in a trusted source."
            )
        } else if (reminderType == "TAHAJJUD") {
            PrayerNotificationManager.showDailyReminder(
                context,
                "Tahajjud Reminder",
                "A gentle reminder: prepare for voluntary night prayer before Fajr."
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
            val prefs = context.getSharedPreferences("muslim_ummah_prayer", Context.MODE_PRIVATE)
            val savedCity = PrayerCalculator.POPULAR_CITIES.firstOrNull {
                it.name == prefs.getString("city_name", null) &&
                    it.country == prefs.getString("city_country", null)
            }
            val latitude = prefs.getFloat("city_latitude", (savedCity ?: PrayerCalculator.POPULAR_CITIES.first()).latitude.toFloat()).toDouble()
            val longitude = prefs.getFloat("city_longitude", (savedCity ?: PrayerCalculator.POPULAR_CITIES.first()).longitude.toFloat()).toDouble()
            val timezoneOffset = prefs.getFloat("city_timezone_offset", (savedCity ?: PrayerCalculator.POPULAR_CITIES.first()).timezoneOffsetHours.toFloat()).toDouble()
            val times = PrayerCalculator.calculateTimes(
                calendar = Calendar.getInstance(),
                latitude = latitude,
                longitude = longitude,
                timezoneOffsetHours = timezoneOffset
            )
            PrayerNotificationManager.schedulePrayerNotifications(
                context,
                times,
                prefs.getBoolean("prayer_notifications_enabled", true)
            )
            PrayerNotificationManager.scheduleDailyReminders(
                context,
                prefs.getBoolean("daily_quran_reminder_enabled", true),
                prefs.getBoolean("daily_hadith_reminder_enabled", true),
                prefs.getBoolean("daily_dua_reminder_enabled", true)
            )
            PrayerNotificationManager.scheduleTahajjudReminder(
                context,
                times.fajr.timeMillis,
                prefs.getBoolean("tahajjud_reminder_enabled", false),
                latitude,
                longitude,
                timezoneOffset
            )
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
        if (!canPostNotifications(context)) {
            Log.w(TAG, "Skipping prayer notification because POST_NOTIFICATIONS permission is not granted.")
            return
        }
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
        if (!canPostNotifications(context)) {
            Log.w(TAG, "Skipping reminder because POST_NOTIFICATIONS permission is not granted.")
            return
        }
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

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
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

    fun scheduleDailyReminders(
        context: Context,
        quranEnabled: Boolean,
        hadithEnabled: Boolean,
        duaEnabled: Boolean = false
    ) {
        createNotificationChannels(context)
        scheduleDailyReminder(context, quranEnabled, 9, 0, 9001, "DAILY_QURAN")
        scheduleDailyReminder(context, hadithEnabled, 20, 0, 9002, "DAILY_HADITH")
        scheduleDailyReminder(context, duaEnabled, 12, 0, 9004, "DAILY_DUA")
    }

    private fun scheduleDailyReminder(
        context: Context,
        enabled: Boolean,
        hour: Int,
        minute: Int,
        requestCode: Int,
        reminderType: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val reminderIntent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("REMINDER_TYPE", reminderType)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not schedule $reminderType: ${e.message}")
        }
    }

    fun scheduleTahajjudReminder(
        context: Context,
        fajrMillis: Long,
        enabled: Boolean,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra("REMINDER_TYPE", "TAHAJJUD")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9003,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }
        val reminderOffset = 30L * 60L * 1000L
        var triggerAt = fajrMillis - reminderOffset
        if (triggerAt <= System.currentTimeMillis()) {
            val tomorrow = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            triggerAt = PrayerCalculator.calculateTimes(
                calendar = tomorrow,
                latitude = latitude,
                longitude = longitude,
                timezoneOffsetHours = timezoneOffsetHours
            ).fajr.timeMillis - reminderOffset
        }
        if (triggerAt > System.currentTimeMillis()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                }
            } catch (e: SecurityException) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } catch (e: Exception) {
                Log.w(TAG, "Could not schedule Tahajjud reminder: ${e.message}")
            }
        }
    }

}
