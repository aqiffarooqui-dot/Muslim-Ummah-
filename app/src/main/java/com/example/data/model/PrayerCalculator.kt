package com.example.data.model

import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

enum class CalculationMethod(
    val title: String,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaIntervalMin: Int? = null
) {
    KARACHI("University of Islamic Sciences, Karachi (India/Pakistan Standard)", 18.0, 18.0),
    MUSLIM_WORLD_LEAGUE("Muslim World League", 18.0, 17.0),
    ISNA("Islamic Society of North America (ISNA)", 15.0, 15.0),
    UMM_AL_QURA("Umm Al-Qura University, Makkah", 18.5, 0.0, ishaIntervalMin = 90),
    EGYPTIAN("Egyptian General Authority of Survey", 19.5, 17.5),
    DUBAI("Dubai (UAE Awqaf)", 18.2, 18.2),
    KUWAIT("Kuwait", 18.0, 17.5)
}

enum class JuristicMethod(val title: String, val shadowFactor: Double) {
    HANAFI("Hanafi (Standard in India & Subcontinent)", 2.0),
    STANDARD("Shafi, Maliki, Hanbali", 1.0)
}

data class PrayerTime(
    val name: String,
    val arabicName: String,
    val timeMillis: Long,
    val timeString: String,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false,
    val isNext: Boolean = false
)

data class DailyPrayerTimes(
    val date: Date,
    val fajr: PrayerTime,
    val sunrise: PrayerTime,
    val dhuhr: PrayerTime,
    val asr: PrayerTime,
    val maghrib: PrayerTime,
    val isha: PrayerTime,
    val currentPrayer: PrayerTime,
    val nextPrayer: PrayerTime,
    val timeRemainingMillis: Long,
    val progressFraction: Float
) {
    val allList: List<PrayerTime>
        get() = listOf(fajr, sunrise, dhuhr, asr, maghrib, isha)
}

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

object PrayerCalculator {

    val POPULAR_CITIES = listOf(
        // India - Major Metros & States
        CityLocation("Mumbai", "India", 19.0760, 72.8777, 5.5),
        CityLocation("New Delhi", "India", 28.6139, 77.2090, 5.5),
        CityLocation("Bengaluru (Bangalore)", "India", 12.9716, 77.5946, 5.5),
        CityLocation("Hyderabad", "India", 17.3850, 78.4867, 5.5),
        CityLocation("Chennai", "India", 13.0827, 80.2707, 5.5),
        CityLocation("Kolkata", "India", 22.5726, 88.3639, 5.5),
        CityLocation("Ahmedabad", "India", 23.0225, 72.5714, 5.5),
        CityLocation("Pune", "India", 18.5204, 73.8567, 5.5),
        CityLocation("Lucknow", "India", 26.8467, 80.9462, 5.5),
        CityLocation("Srinagar", "India", 34.0837, 74.7973, 5.5),
        CityLocation("Jaipur", "India", 26.9124, 75.7873, 5.5),
        CityLocation("Patna", "India", 25.5941, 85.1376, 5.5),
        CityLocation("Bhopal", "India", 23.2599, 77.4126, 5.5),
        CityLocation("Kozhikode (Calicut)", "India", 11.2588, 75.7804, 5.5),
        CityLocation("Kochi", "India", 9.9312, 76.2673, 5.5),
        CityLocation("Surat", "India", 21.1702, 72.8311, 5.5),
        CityLocation("Chhatrapati Sambhaji Nagar (Aurangabad)", "India", 19.8762, 75.3433, 5.5),
        CityLocation("Kanpur", "India", 26.4499, 80.3319, 5.5),
        CityLocation("Nagpur", "India", 21.1458, 79.0882, 5.5),
        CityLocation("Guwahati", "India", 26.1445, 91.7362, 5.5),
        CityLocation("Varanasi", "India", 25.3176, 82.9739, 5.5),
        CityLocation("Ranchi", "India", 23.3441, 85.3096, 5.5),
        CityLocation("Thiruvananthapuram", "India", 8.5241, 76.9366, 5.5),
        // Islamic Holy Cities
        CityLocation("Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityLocation("Madinah", "Saudi Arabia", 24.5247, 39.5692, 3.0),
        // International reference
        CityLocation("Dubai", "UAE", 25.2048, 55.2708, 4.0),
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, 0.0)
    )

    fun calculateTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        method: CalculationMethod = CalculationMethod.KARACHI,
        juristic: JuristicMethod = JuristicMethod.HANAFI
    ): DailyPrayerTimes {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Julian Day calculation
        val jd = calculateJulianDay(year, month, day) - (longitude / (15.0 * 24.0))

        // Sun's declination & Equation of time
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = fixAngle(Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l))))) / 15.0
        val equationOfTime = (q / 15.0) - ra
        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))

        // Dhuhr is Solar Noon
        val solarNoon = fixHour(12.0 + timezoneOffsetHours - (longitude / 15.0) - equationOfTime)
        val dhuhr = solarNoon

        // Sunrise & Sunset angle (accounting for refraction and sun diameter: 0.833 degrees)
        val sunriseHourAngle = calculateHourAngle(-0.833, latitude, declination)
        val sunrise = fixHour(solarNoon - sunriseHourAngle / 15.0)
        val sunset = fixHour(solarNoon + sunriseHourAngle / 15.0)

        // Fajr
        val fajrHourAngle = calculateHourAngle(-method.fajrAngle, latitude, declination)
        val fajr = fixHour(solarNoon - fajrHourAngle / 15.0)

        // Asr calculation based on Shadow Factor (1 for Shafi, 2 for Hanafi)
        val asrAngle = -Math.toDegrees(atan(1.0 / (juristic.shadowFactor + tan(Math.toRadians(abs(latitude - declination))))))
        val asrHourAngle = calculateHourAngle(asrAngle, latitude, declination)
        val asr = fixHour(solarNoon + asrHourAngle / 15.0)

        // Maghrib is identical to Sunset in standard Sunni jurisprudence
        val maghrib = sunset

        // Isha
        val isha = if (method.ishaIntervalMin != null) {
            fixHour(maghrib + (method.ishaIntervalMin.toDouble() / 60.0))
        } else {
            val ishaHourAngle = calculateHourAngle(-method.ishaAngle, latitude, declination)
            fixHour(solarNoon + ishaHourAngle / 15.0)
        }

        val baseCal = calendar.clone() as Calendar
        baseCal.set(Calendar.HOUR_OF_DAY, 0)
        baseCal.set(Calendar.MINUTE, 0)
        baseCal.set(Calendar.SECOND, 0)
        baseCal.set(Calendar.MILLISECOND, 0)
        val midnightMillis = baseCal.timeInMillis

        fun toPrayerTime(name: String, arabic: String, hourFraction: Double): PrayerTime {
            val totalSeconds = (hourFraction * 3600).roundToInt()
            val hours = (totalSeconds / 3600) % 24
            val minutes = (totalSeconds % 3600) / 60
            val millis = midnightMillis + (hours * 3600L + minutes * 60L) * 1000L

            val calTime = Calendar.getInstance().apply { timeInMillis = millis }
            val formatted = String.format(
                Locale.US,
                "%02d:%02d %s",
                if (calTime.get(Calendar.HOUR) == 0) 12 else calTime.get(Calendar.HOUR),
                calTime.get(Calendar.MINUTE),
                if (calTime.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            )
            return PrayerTime(name, arabic, millis, formatted)
        }

        val fajrPt = toPrayerTime("Fajr", "الفجر", fajr)
        val sunrisePt = toPrayerTime("Sunrise", "الشروق", sunrise)
        val dhuhrPt = toPrayerTime("Dhuhr", "الظهر", dhuhr)
        val asrPt = toPrayerTime("Asr", "العصر", asr)
        val maghribPt = toPrayerTime("Maghrib", "المغرب", maghrib)
        val ishaPt = toPrayerTime("Isha", "العشاء", isha)

        val nowMillis = System.currentTimeMillis()
        val allTimes = listOf(fajrPt, sunrisePt, dhuhrPt, asrPt, maghribPt, ishaPt)

        var nextIndex = allTimes.indexOfFirst { it.timeMillis > nowMillis }
        if (nextIndex == -1) nextIndex = 0

        val nextPt = allTimes[nextIndex].copy(isNext = true)
        val currentIndex = if (nextIndex == 0) allTimes.lastIndex else nextIndex - 1
        val currentPt = allTimes[currentIndex].copy(isCurrent = true)

        val remaining = if (nextPt.timeMillis > nowMillis) {
            nextPt.timeMillis - nowMillis
        } else {
            (nextPt.timeMillis + 24 * 3600 * 1000L) - nowMillis
        }

        val totalInterval = if (nextPt.timeMillis > currentPt.timeMillis) {
            (nextPt.timeMillis - currentPt.timeMillis).toFloat()
        } else {
            (24 * 3600 * 1000L).toFloat()
        }
        val elapsed = (nowMillis - currentPt.timeMillis).toFloat()
        val progress = (elapsed / totalInterval).coerceIn(0f, 1f)

        return DailyPrayerTimes(
            date = calendar.time,
            fajr = fajrPt.copy(isPassed = nowMillis > fajrPt.timeMillis),
            sunrise = sunrisePt.copy(isPassed = nowMillis > sunrisePt.timeMillis),
            dhuhr = dhuhrPt.copy(isPassed = nowMillis > dhuhrPt.timeMillis),
            asr = asrPt.copy(isPassed = nowMillis > asrPt.timeMillis),
            maghrib = maghribPt.copy(isPassed = nowMillis > maghribPt.timeMillis),
            isha = ishaPt.copy(isPassed = nowMillis > ishaPt.timeMillis),
            currentPrayer = currentPt,
            nextPrayer = nextPt,
            timeRemainingMillis = remaining,
            progressFraction = progress
        )
    }

    private fun calculateJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun calculateHourAngle(alpha: Double, latitude: Double, declination: Double): Double {
        val cosHourAngle = (sin(Math.toRadians(alpha)) - sin(Math.toRadians(latitude)) * sin(Math.toRadians(declination))) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination)))
        val clamped = cosHourAngle.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped))
    }

    private fun fixAngle(a: Double): Double {
        val b = a - 360.0 * floor(a / 360.0)
        return if (b < 0) b + 360.0 else b
    }

    private fun fixHour(h: Double): Double {
        val b = h - 24.0 * floor(h / 24.0)
        return if (b < 0) b + 24.0 else b
    }
}
