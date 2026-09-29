package com.example.data.model

data class DhikrPreset(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val defaultTarget: Int = 33
)

object TasbihData {
    val PRESETS = listOf(
        DhikrPreset(
            title = "SubhanAllah",
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "Subḥānallāh",
            meaning = "Glory be to Allah",
            defaultTarget = 33
        ),
        DhikrPreset(
            title = "Alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Al-ḥamdu lillāh",
            meaning = "Praise be to Allah",
            defaultTarget = 33
        ),
        DhikrPreset(
            title = "Allahu Akbar",
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allāhu Akbar",
            meaning = "Allah is the Greatest",
            defaultTarget = 34
        ),
        DhikrPreset(
            title = "Astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullāh",
            meaning = "I seek forgiveness from Allah",
            defaultTarget = 100
        ),
        DhikrPreset(
            title = "La ilaha illallah",
            arabic = "لَا إِلَهَ إِلَّا اللَّهُ",
            transliteration = "Lā ilāha illallāh",
            meaning = "There is no deity worthy of worship except Allah",
            defaultTarget = 100
        ),
        DhikrPreset(
            title = "SubhanAllahi wa bihamdihi",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            transliteration = "Subḥānallāhi wa bi-ḥamdih",
            meaning = "Glory be to Allah and His is the praise",
            defaultTarget = 100
        ),
        DhikrPreset(
            title = "Salawat upon the Prophet ﷺ",
            arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ",
            transliteration = "Allāhumma ṣalli 'alā Muḥammad",
            meaning = "O Allah, send blessings upon Muhammad",
            defaultTarget = 100
        ),
        DhikrPreset(
            title = "La hawla wa la quwwata illa billah",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Lā ḥawla wa lā quwwata illā billāh",
            meaning = "There is no power and no might except with Allah",
            defaultTarget = 33
        ),
        DhikrPreset(
            title = "Hasbunallahu wa ni'mal wakeel",
            arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
            transliteration = "Ḥasbunallāhu wa ni'mal-wakīl",
            meaning = "Sufficient for us is Allah, and He is the best Disposer of affairs",
            defaultTarget = 33
        )
    )

    val TARGET_OPTIONS = listOf(33, 99, 100, 500, 1000)
}
