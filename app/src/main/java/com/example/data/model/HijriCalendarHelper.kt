package com.example.data.model

import java.util.Calendar

data class HijriDate(
    val day: Int,
    val monthName: String,
    val arabicMonthName: String,
    val monthNumber: Int,
    val year: Int
) {
    val formatted: String
        get() = "$day $monthName $year AH"

    val formattedArabic: String
        get() = "$day $arabicMonthName $year هـ"
}

data class IslamicEvent(
    val name: String,
    val arabicName: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val description: String,
    val isFastingDay: Boolean = false
)

object HijriCalendarHelper {

    private val HIJRI_MONTHS = listOf(
        "Muharram" to "محرم",
        "Safar" to "صفر",
        "Rabi' al-Awwal" to "ربيع الأول",
        "Rabi' al-Thani" to "ربيع الثاني",
        "Jumada al-Awwal" to "جمادى الأولى",
        "Jumada al-Thani" to "جمادى الثانية",
        "Rajab" to "رجب",
        "Sha'ban" to "شعبان",
        "Ramadan" to "رمضان",
        "Shawwal" to "شوال",
        "Dhu al-Qi'dah" to "ذو القعدة",
        "Dhu al-Hijjah" to "ذو الحجة"
    )

    val ISLAMIC_EVENTS = listOf(
        IslamicEvent("Islamic New Year", "رأس السنة الهجرية", 1, 1, "First day of the Islamic lunar calendar year.", isFastingDay = false),
        IslamicEvent("Day of Ashura", "يوم عاشوراء", 10, 1, "Day of fasting commemorating Musa (Moses) peace be upon him being saved by Allah.", isFastingDay = true),
        IslamicEvent("Mawlid al-Nabi", "المولد النبوي الشريف", 12, 3, "Birth anniversary of the Prophet Muhammad ﷺ.", isFastingDay = false),
        IslamicEvent("Isra and Mi'raj", "الإسراء والمعراج", 27, 7, "The miraculous Night Journey and Ascension of the Prophet Muhammad ﷺ.", isFastingDay = false),
        IslamicEvent("Mid-Sha'ban (Shab-e-Barat)", "ليلة النصف من شعبان", 15, 8, "Night of records and seeking forgiveness.", isFastingDay = true),
        IslamicEvent("Start of Ramadan", "بداية شهر رمضان المبارك", 1, 9, "First day of the holy month of fasting and revelation of the Holy Quran.", isFastingDay = true),
        IslamicEvent("Laylat al-Qadr", "ليلة القدر", 27, 9, "The Night of Decree/Power, better than a thousand months.", isFastingDay = true),
        IslamicEvent("Eid al-Fitr", "عيد الفطر المبارك", 1, 10, "Festival of breaking the fast after the blessed month of Ramadan.", isFastingDay = false),
        IslamicEvent("Day of Arafah", "يوم عرفة", 9, 12, "The greatest day of Hajj; fasting on this day expiates sins of previous and coming year.", isFastingDay = true),
        IslamicEvent("Eid al-Adha", "عيد الأضحى المبارك", 10, 12, "Feast of Sacrifice honoring the obedience of Ibrahim (Abraham) peace be upon him.", isFastingDay = false),
        IslamicEvent("Days of Tashreeq", "أيام التشريق", 11, 12, "Days of remembrance and celebration during Hajj.", isFastingDay = false)
    )

    /**
     * Converts a Gregorian calendar date to Hijri date using accurate astronomical arithmetic
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance()): HijriDate {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)

        var year = y
        var month = m
        if (month < 3) {
            year -= 1
            month += 12
        }

        val a = Math.floor(year / 100.0)
        val b = 2 - a + Math.floor(a / 4.0)
        val jd = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + d + b - 1524.5

        // Epoch of Islamic Calendar is July 16, 622 CE (Julian day 1948439.5)
        val z = jd - 1948439.5
        val cyc = Math.floor(z / 10631.0)
        val r = z - 10631.0 * cyc
        val j = Math.floor((r - 0.1335) / 354.366)
        val mYear = (30 * cyc + j).toInt() + 1
        val remDays = r - Math.floor(j * 354.366 + 0.1335)
        var mMonth = Math.floor((remDays + 0.85) / 29.5).toInt() + 1
        var mDay = (remDays - Math.floor((mMonth - 1) * 29.5) + 1).toInt()

        if (mDay <= 0) {
            mMonth -= 1
            mDay = 30 + mDay
        }
        if (mMonth > 12) {
            mMonth = 12
        }
        val safeMonthIndex = (mMonth - 1).coerceIn(0, 11)
        val (monthName, arabicName) = HIJRI_MONTHS[safeMonthIndex]

        return HijriDate(
            day = mDay.coerceIn(1, 30),
            monthName = monthName,
            arabicMonthName = arabicName,
            monthNumber = mMonth,
            year = mYear
        )
    }
}
