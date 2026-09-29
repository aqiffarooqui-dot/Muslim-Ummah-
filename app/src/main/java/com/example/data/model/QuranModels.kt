package com.example.data.model

enum class QuranTranslationLanguage(val displayName: String, val author: String) {
    ENGLISH("English", "Sahih International"),
    URDU("Urdu (اردو)", "Fateh Muhammad Jalandhari"),
    HINDI("Hindi (हिन्दी)", "Muhammad Farooq Khan"),
    HINGLISH("Hinglish", "Roman Script Translation")
}

data class Para(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyahNumber: Int,
    val surahsInPara: List<Int>
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalAyahs: Int,
    val revelationType: String, // "Makki" or "Madani"
    val juz: Int = 1
) {
    fun getAudioUrl(reciterIdentifier: String = "ar.alafasy"): String {
        return String.format("https://cdn.islamic.network/quran/audio-surah/128/%s/%d.mp3", reciterIdentifier, number)
    }
}

data class QuranReciter(
    val id: String,
    val name: String,
    val style: String,
    val isPremium: Boolean = false
) {
    companion object {
        val ALL_RECITERS = listOf(
            QuranReciter("ar.alafasy", "Sheikh Mishary Rashid Alafasy", "Murattal • High Clarity", false),
            QuranReciter("ar.abdulbasit", "Sheikh Abdul Basit Abdul Samad", "Mujawwad • Historic Classic", true),
            QuranReciter("ar.sudais", "Sheikh Abdur-Rahman As-Sudais", "Makkah Haram Imam", true),
            QuranReciter("ar.shuraim", "Sheikh Saud Ash-Shuraim", "Makkah Haram Classic", true),
            QuranReciter("ar.ghamdi", "Sheikh Saad Al-Ghamdi", "Calm Melodic Murattal", true)
        )
    }
}

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val textTransliteration: String,
    val textEnglish: String,
    val textUrdu: String = "",
    val textHindi: String = "",
    val textHinglish: String = "",
    val audioUrl: String = ""
) {
    fun getTranslation(language: QuranTranslationLanguage): String {
        return when (language) {
            QuranTranslationLanguage.ENGLISH -> textEnglish
            QuranTranslationLanguage.URDU -> textUrdu.ifBlank { textEnglish }
            QuranTranslationLanguage.HINDI -> textHindi.ifBlank { textEnglish }
            QuranTranslationLanguage.HINGLISH -> textHinglish.ifBlank { textEnglish }
        }
    }
}
