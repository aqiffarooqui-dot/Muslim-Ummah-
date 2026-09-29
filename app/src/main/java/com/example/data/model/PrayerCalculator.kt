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
    MUSLIM_WORLD_LEAGUE("Muslim World League", 18.0, 17.0),
    ISNA("Islamic Society of North America (ISNA)", 15.0, 15.0),
    UMM_AL_QURA("Umm Al-Qura University, Makkah", 18.5, 0.0, ishaIntervalMin = 90),
    EGYPTIAN("Egyptian General Authority of Survey", 19.5, 17.5),
    KARACHI("University of Islamic Sciences, Karachi", 18.0, 18.0),
    DUBAI("Dubai (UAE Awqaf)", 18.2, 18.2),
    KUWAIT("Kuwait", 18.0, 17.5)
}

enum class JuristicMethod(val title: String, val shadowFactor: Double) {
    STANDARD("Shafi, Maliki, Hanbali (Standard)", 1.0),
    HANAFI("Hanafi", 2.0)
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
        CityLocation("Makkah", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityLocation("Madinah", "Saudi Arabia", 24.5247, 39.5692, 3.0),
        CityLocation("Cairo", "Egypt", 30.0444, 31.2357, 2.0),
        CityLocation("Istanbul", "Turkey", 41.0082, 28.9784, 3.0),
        CityLocation("Dubai", "UAE", 25.2048, 55.2708, 4.0),
        CityLocation("Karachi", "Pakistan", 24.8607, 67.0011, 5.0),
        CityLocation("Jakarta", "Indonesia", -6.2088, 106.8456, 7.0),
        CityLocation("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, 8.0),
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        CityLocation("New York", "United States", 40.7128, -74.0060, -5.0),
        CityLocation("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        CityLocation("Paris", "France", 48.8566, 2.3522, 1.0),
        CityLocation("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        CityLocation("Singapore", "Singapore", 1.3521, 103.8198, 8.0)
    )

    fun calculateTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        method: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
        juristic: JuristicMethod = JuristicMethod.STANDARD
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
        val l = fixAngle(q + 1.915 * sin(degToRad(g)) + 0.020 * sin(degToRad(2 * g)))
        val e = 23.439 - 0.00000036 * d
        val ra = radToDeg(atan2(cos(degToRad(e)) * sin(degToRad(l)), cos(degToRad(l)))) / 15.0
        val declination = asin(sin(degToRad(e)) * sin(degToRad(l)))
        val eqt = q / 15.0 - fixHour(ra)

        // Noon time
        val noon = fixHour(12.0 + timezoneOffsetHours - (longitude / 15.0) - eqt)

        // Sunrise & Sunset angle (approx 0.833 degrees for atmospheric refraction + sun disc)
        val sunriseAngle = 0.833
        val sunriseHour = noon - calculateHourAngle(latitude, declination, sunriseAngle) / 15.0
        val sunsetHour = noon + calculateHourAngle(latitude, declination, sunriseAngle) / 15.0

        // Fajr
        val fajrHour = noon - calculateHourAngle(latitude, declination, method.fajrAngle) / 15.0

        // Asr
        val asrAngle = -radToDeg(atan(1.0 / (juristic.shadowFactor + tan(abs(degToRad(latitude) - declination)))))
        val asrHour = noon + calculateHourAngle(latitude, declination, asrAngle) / 15.0

        // Maghrib
        val maghribHour = sunsetHour

        // Isha
        val ishaHour = if (method.ishaIntervalMin != null) {
            maghribHour + (method.ishaIntervalMin.toDouble() / 60.0)
        } else {
            noon + calculateHourAngle(latitude, declination, method.ishaAngle) / 15.0
        }

        fun toMillis(hourFrac: Double): Long {
            val cal = calendar.clone() as Calendar
            val totalSeconds = (hourFrac * 3600.0).roundToInt()
            val hours = (totalSeconds / 3600).coerceIn(0, 23)
            val minutes = ((totalSeconds % 3600) / 60).coerceIn(0, 59)
            val seconds = (totalSeconds % 60).coerceIn(0, 59)
            cal.set(Calendar.HOUR_OF_DAY, hours)
            cal.set(Calendar.MINUTE, minutes)
            cal.set(Calendar.SECOND, seconds)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun formatTime(hourFrac: Double): String {
            val totalSeconds = (hourFrac * 3600.0).roundToInt()
            val h24 = (totalSeconds / 3600).coerceIn(0, 23)
            val m = ((totalSeconds % 3600) / 60).coerceIn(0, 59)
            val ampm = if (h24 >= 12) "PM" else "AM"
            val h12 = when {
                h24 == 0 -> 12
                h24 > 12 -> h24 - 12
                else -> h24
            }
            return String.format(Locale.getDefault(), "%02d:%02d %s", h12, m, ampm)
        }

        val fajrMillis = toMillis(fajrHour)
        val sunriseMillis = toMillis(sunriseHour)
        val dhuhrMillis = toMillis(noon)
        val asrMillis = toMillis(asrHour)
        val maghribMillis = toMillis(maghribHour)
        val ishaMillis = toMillis(ishaHour)

        val now = System.currentTimeMillis()

        val pFajr = PrayerTime("Fajr", "الفجر", fajrMillis, formatTime(fajrHour), now > fajrMillis)
        val pSunrise = PrayerTime("Sunrise", "الشروق", sunriseMillis, formatTime(sunriseHour), now > sunriseMillis)
        val pDhuhr = PrayerTime("Dhuhr", "الظهر", dhuhrMillis, formatTime(noon), now > dhuhrMillis)
        val pAsr = PrayerTime("Asr", "العصر", asrMillis, formatTime(asrHour), now > asrMillis)
        val pMaghrib = PrayerTime("Maghrib", "المغرب", maghribMillis, formatTime(maghribHour), now > maghribMillis)
        val pIsha = PrayerTime("Isha", "العشاء", ishaMillis, formatTime(ishaHour), now > ishaMillis)

        val times = listOf(pFajr, pSunrise, pDhuhr, pAsr, pMaghrib, pIsha)

        var currentPrayer = pIsha
        var nextPrayer = pFajr
        var timeRemaining = 0L
        var progress = 0f

        if (now < pFajr.timeMillis) {
            currentPrayer = pIsha
            nextPrayer = pFajr
            timeRemaining = pFajr.timeMillis - now
            val totalSpan = (pFajr.timeMillis - (pIsha.timeMillis - 24 * 3600 * 1000L)).coerceAtLeast(1L)
            val elapsed = now - (pIsha.timeMillis - 24 * 3600 * 1000L)
            progress = (elapsed.toFloat() / totalSpan.toFloat()).coerceIn(0f, 1f)
        } else if (now < pSunrise.timeMillis) {
            currentPrayer = pFajr
            nextPrayer = pSunrise
            timeRemaining = pSunrise.timeMillis - now
            val total = (pSunrise.timeMillis - pFajr.timeMillis).coerceAtLeast(1L)
            progress = ((now - pFajr.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else if (now < pDhuhr.timeMillis) {
            currentPrayer = pSunrise
            nextPrayer = pDhuhr
            timeRemaining = pDhuhr.timeMillis - now
            val total = (pDhuhr.timeMillis - pSunrise.timeMillis).coerceAtLeast(1L)
            progress = ((now - pSunrise.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else if (now < pAsr.timeMillis) {
            currentPrayer = pDhuhr
            nextPrayer = pAsr
            timeRemaining = pAsr.timeMillis - now
            val total = (pAsr.timeMillis - pDhuhr.timeMillis).coerceAtLeast(1L)
            progress = ((now - pDhuhr.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else if (now < pMaghrib.timeMillis) {
            currentPrayer = pAsr
            nextPrayer = pMaghrib
            timeRemaining = pMaghrib.timeMillis - now
            val total = (pMaghrib.timeMillis - pAsr.timeMillis).coerceAtLeast(1L)
            progress = ((now - pAsr.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else if (now < pIsha.timeMillis) {
            currentPrayer = pMaghrib
            nextPrayer = pIsha
            timeRemaining = pIsha.timeMillis - now
            val total = (pIsha.timeMillis - pMaghrib.timeMillis).coerceAtLeast(1L)
            progress = ((now - pMaghrib.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else {
            currentPrayer = pIsha
            // Tomorrow's Fajr
            val tomorrowFajr = pFajr.timeMillis + 24 * 3600 * 1000L
            nextPrayer = pFajr.copy(timeMillis = tomorrowFajr)
            timeRemaining = tomorrowFajr - now
            val total = (tomorrowFajr - pIsha.timeMillis).coerceAtLeast(1L)
            progress = ((now - pIsha.timeMillis).toFloat() / total.toFloat()).coerceIn(0f, 1f)
        }

        val enrichedFajr = pFajr.copy(isCurrent = currentPrayer.name == "Fajr", isNext = nextPrayer.name == "Fajr")
        val enrichedSunrise = pSunrise.copy(isCurrent = currentPrayer.name == "Sunrise", isNext = nextPrayer.name == "Sunrise")
        val enrichedDhuhr = pDhuhr.copy(isCurrent = currentPrayer.name == "Dhuhr", isNext = nextPrayer.name == "Dhuhr")
        val enrichedAsr = pAsr.copy(isCurrent = currentPrayer.name == "Asr", isNext = nextPrayer.name == "Asr")
        val enrichedMaghrib = pMaghrib.copy(isCurrent = currentPrayer.name == "Maghrib", isNext = nextPrayer.name == "Maghrib")
        val enrichedIsha = pIsha.copy(isCurrent = currentPrayer.name == "Isha", isNext = nextPrayer.name == "Isha")

        return DailyPrayerTimes(
            date = calendar.time,
            fajr = enrichedFajr,
            sunrise = enrichedSunrise,
            dhuhr = enrichedDhuhr,
            asr = enrichedAsr,
            maghrib = enrichedMaghrib,
            isha = enrichedIsha,
            currentPrayer = currentPrayer,
            nextPrayer = nextPrayer,
            timeRemainingMillis = timeRemaining,
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
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun calculateHourAngle(lat: Double, decl: Double, angle: Double): Double {
        val cosHourAngle = (-sin(degToRad(angle)) - sin(degToRad(lat)) * sin(decl)) /
                (cos(degToRad(lat)) * cos(decl))
        if (cosHourAngle > 1.0) return 0.0
        if (cosHourAngle < -1.0) return 180.0
        return radToDeg(acos(cosHourAngle))
    }

    private fun degToRad(deg: Double) = deg * Math.PI / 180.0
    private fun radToDeg(rad: Double) = rad * 180.0 / Math.PI
    private fun fixAngle(angle: Double) = (angle % 360.0 + 360.0) % 360.0
    private fun fixHour(hour: Double) = (hour % 24.0 + 24.0) % 24.0
}
