package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalAyahs: Int,
    val revelationType: String, // "Makki" or "Madani"
    val juz: Int = 1
) {
    val audioUrl: String
        get() = String.format("https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/%d.mp3", number)
}

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textTransliteration: String,
    val textEnglish: String,
    val audioUrl: String = ""
)
