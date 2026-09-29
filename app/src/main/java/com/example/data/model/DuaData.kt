package com.example.data.model

data class Dua(
    val id: Int,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val english: String,
    val reference: String,
    val repeatCount: Int = 1
)

object DuaRepository {
    val CATEGORIES = listOf(
        "All",
        "Morning & Evening",
        "Salah (Prayer)",
        "Forgiveness & Repentance",
        "Protection & Ruqyah",
        "Travel & Leaving Home",
        "Anxiety & Hardship",
        "Daily Life"
    )

    val ALL_DUAS = listOf(
        Dua(
            id = 1,
            category = "Morning & Evening",
            title = "Morning Supplication (Asbahna wa Asbahal Mulk)",
            arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Aṣbaḥnā wa aṣbaḥal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā sharīka lah, lahul-mulku wa lahul-ḥamdu wa Huwa 'alā kulli shay'in Qadīr.",
            reference = "Muslim 4/2088",
            english = "We have reached the morning and the kingdom belongs to Allah, all praise is due to Allah. None has the right to be worshipped but Allah alone, Who has no partner. His was the dominion and His is the praise, and He has power over all things."
        ),
        Dua(
            id = 2,
            category = "Forgiveness & Repentance",
            title = "Sayyid al-Istighfar (Chief of Prayers for Forgiveness)",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            transliteration = "Allāhumma Anta Rabbī lā ilāha illā Anta, khalaqtanī wa anā 'abduka, wa anā 'alā 'ahdika wa wa'dika mastaṭa'tu, a'ūdhu bika min sharri mā ṣana'tu, abū'u laka bini'matika 'alayya, wa abū'u bidhambī faghfir lī fa-innahū lā yaghfirudh-dhunūba illā Anta.",
            reference = "Sahih al-Bukhari 7/150",
            english = "O Allah! You are my Lord; there is no deity except You. You have created me and I am Your slave. I adhere to Your covenant and Your promise as much as I am able. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me, and I acknowledge my sin, so forgive me, for none can forgive sins except You."
        ),
        Dua(
            id = 3,
            category = "Protection & Ruqyah",
            title = "Protection Against All Harm (Bismillahilladhi...)",
            arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillāhilladhī lā yaḍurru ma'as-mihī shay'un fil-arḍi wa lā fis-samā'i wa Huwas-Samī'ul-'Alīm.",
            reference = "Abu Dawud & At-Tirmidhi",
            english = "In the Name of Allah, with Whose Name nothing upon the earth or in the heavens can cause harm, and He is the All-Hearing, the All-Knowing.",
            repeatCount = 3
        ),
        Dua(
            id = 4,
            category = "Anxiety & Hardship",
            title = "Dua of Prophet Yunus (In Distress)",
            arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            transliteration = "Lā ilāha illā Anta Subḥānaka innī kuntu minaẓ-ẓālimīn.",
            reference = "Surah Al-Anbiya 21:87",
            english = "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers."
        ),
        Dua(
            id = 5,
            category = "Travel & Leaving Home",
            title = "Dua for Travelling (Riding a Vehicle)",
            arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            transliteration = "Subḥānalladhī sakh-khara lanā hādhā wa mā kunnā lahū muqrinīn, wa innā ilā Rabbinā lamunqalibūn.",
            reference = "Surah Az-Zukhruf 43:13-14",
            english = "Glory to Him Who has subjected this to us, and we could never have it by our efforts. And verily, unto our Lord we shall return."
        ),
        Dua(
            id = 6,
            category = "Travel & Leaving Home",
            title = "When Leaving the House",
            arabic = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "Bismillāh, tawakkaltu 'alallāh, wa lā ḥawla wa lā quwwata illā billāh.",
            reference = "Abu Dawud 4/325",
            english = "In the name of Allah, I trust in Allah; there is no power and no might except with Allah."
        ),
        Dua(
            id = 7,
            category = "Salah (Prayer)",
            title = "After the Salutation of Prayer (Istighfar & Peace)",
            arabic = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            transliteration = "Astaghfirullāh (3x). Allāhumma Antas-Salāmu wa minkas-salām, tabārakta yā Dhal-Jalāli wal-Ikrām.",
            reference = "Muslim 1/414",
            english = "I ask Allah for forgiveness (3x). O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of majesty and honor."
        ),
        Dua(
            id = 8,
            category = "Daily Life",
            title = "Before Sleeping",
            arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            transliteration = "Bismika Rabbī waḍa'tu jambi, wa bika arfa'uh, fa-in amsakta nafsī farḥamhā, wa in arsaltahā faḥfaẓhā bimā taḥfaẓu bihī 'ibādakaṣ-ṣāliḥīn.",
            reference = "Sahih al-Bukhari 11/126",
            english = "In Your name, my Lord, I lay down my side and in Your name I raise it. If You take my soul, have mercy on it, and if You release it, protect it as You protect Your righteous slaves."
        ),
        Dua(
            id = 9,
            category = "Daily Life",
            title = "Upon Waking Up",
            arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            transliteration = "Al-ḥamdu lillāhilladhī aḥyānā ba'da mā amātanā wa ilayhin-nushūr.",
            reference = "Sahih al-Bukhari 11/113",
            english = "All praise is for Allah who gave us life after having taken it from us, and unto Him is the resurrection."
        ),
        Dua(
            id = 10,
            category = "Anxiety & Hardship",
            title = "Supplication for Removing Anxiety and Sorrow",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allāhumma innī a'ūdhu bika minal-hammi wal-ḥazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa ḍala'id-dayni wa ghalabatir-rijāl.",
            reference = "Sahih al-Bukhari 7/158",
            english = "O Allah, I seek refuge in You from anxiety and sorrow, weakness and laziness, miserliness and cowardice, the burden of debts and from being overpowered by men."
        )
    )
}
