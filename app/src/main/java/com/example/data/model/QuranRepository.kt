package com.example.data.model

object QuranRepository {

    val ALL_SURAHS: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opener", 7, "Makki", 1),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", 286, "Madani", 1),
        Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", 200, "Madani", 3),
        Surah(4, "النساء", "An-Nisa", "The Women", 176, "Madani", 4),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", 120, "Madani", 6),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", 165, "Makki", 7),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", 206, "Makki", 8),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", 75, "Madani", 9),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", 129, "Madani", 10),
        Surah(10, "يونس", "Yunus", "Jonah", 109, "Makki", 11),
        Surah(11, "هود", "Hud", "Hud", 123, "Makki", 11),
        Surah(12, "يوسف", "Yusuf", "Joseph", 111, "Makki", 12),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", 43, "Madani", 13),
        Surah(14, "إبراهيم", "Ibrahim", "Abraham", 52, "Makki", 13),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", 99, "Makki", 14),
        Surah(16, "النحل", "An-Nahl", "The Bee", 128, "Makki", 14),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", 111, "Makki", 15),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", 110, "Makki", 15),
        Surah(19, "مريم", "Maryam", "Mary", 98, "Makki", 16),
        Surah(20, "طه", "Taha", "Ta-Ha", 135, "Makki", 16),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", 112, "Makki", 17),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", 78, "Madani", 17),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", 118, "Makki", 18),
        Surah(24, "النور", "An-Nur", "The Light", 64, "Madani", 18),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", 77, "Makki", 18),
        Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", 227, "Makki", 19),
        Surah(27, "النمل", "An-Naml", "The Ants", 93, "Makki", 19),
        Surah(28, "القصص", "Al-Qasas", "The Stories", 88, "Makki", 20),
        Surah(29, "العنكبوت", "Al-'Ankabut", "The Spider", 69, "Makki", 20),
        Surah(30, "الروم", "Ar-Rum", "The Romans", 60, "Makki", 21),
        Surah(31, "لقمان", "Luqman", "Luqman", 34, "Makki", 21),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", 30, "Makki", 21),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", 73, "Madani", 21),
        Surah(34, "سبأ", "Saba", "Sheba", 54, "Makki", 22),
        Surah(35, "فاطر", "Fatir", "The Originator", 45, "Makki", 22),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", 83, "Makki", 22),
        Surah(37, "الصافات", "As-Saffat", "Those who set the Ranks", 182, "Makki", 23),
        Surah(38, "ص", "Sad", "The Letter Sad", 88, "Makki", 23),
        Surah(39, "الزمر", "Az-Zumar", "The Troops", 75, "Makki", 23),
        Surah(40, "غافر", "Ghafir", "The Forgiver", 85, "Makki", 24),
        Surah(41, "فصلت", "Fussilat", "Explained in Detail", 54, "Makki", 24),
        Surah(42, "الشورى", "Ash-Shura", "The Consultation", 53, "Makki", 25),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", 89, "Makki", 25),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", 59, "Makki", 25),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", 37, "Makki", 25),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", 35, "Makki", 26),
        Surah(47, "محمد", "Muhammad", "Muhammad", 38, "Madani", 26),
        Surah(48, "الفتح", "Al-Fath", "The Victory", 29, "Madani", 26),
        Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", 18, "Madani", 26),
        Surah(50, "ق", "Qaf", "The Letter Qaf", 45, "Makki", 26),
        Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", 60, "Makki", 26),
        Surah(52, "الطور", "At-Tur", "The Mount", 49, "Makki", 27),
        Surah(53, "النجم", "An-Najm", "The Star", 62, "Makki", 27),
        Surah(54, "القمر", "Al-Qamar", "The Moon", 55, "Makki", 27),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", 78, "Madani", 27),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", 96, "Makki", 27),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", 29, "Madani", 27),
        Surah(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", 22, "Madani", 28),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", 24, "Madani", 28),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", 13, "Madani", 28),
        Surah(61, "الصف", "As-Saff", "The Ranks", 14, "Madani", 28),
        Surah(62, "الجمعة", "Al-Jumu'ah", "Friday", 11, "Madani", 28),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Madani", 28),
        Surah(64, "التغابن", "At-Taghabun", "Mutual Loss and Gain", 18, "Madani", 28),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", 12, "Madani", 28),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Madani", 28),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Makki", 29),
        Surah(68, "القلم", "Al-Qalam", "The Pen", 52, "Makki", 29),
        Surah(69, "الحاقة", "Al-Haqqah", "The Inevitable Reality", 52, "Makki", 29),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Makki", 29),
        Surah(71, "نوح", "Nuh", "Noah", 28, "Makki", 29),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", 28, "Makki", 29),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Makki", 29),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Makki", 29),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Makki", 29),
        Surah(76, "الإنسان", "Al-Insan", "Man", 31, "Madani", 29),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Makki", 29),
        Surah(78, "النبأ", "An-Naba", "The Tidings", 40, "Makki", 30),
        Surah(79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Makki", 30),
        Surah(80, "عبس", "'Abasa", "He Frowned", 42, "Makki", 30),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Makki", 30),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Makki", 30),
        Surah(83, "المطففين", "Al-Mutaffifin", "Defrauding", 36, "Makki", 30),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Splitting Asunder", 25, "Makki", 30),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Makki", 30),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", 17, "Makki", 30),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, "Makki", 30),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming Event", 26, "Makki", 30),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", 30, "Makki", 30),
        Surah(90, "البلد", "Al-Balad", "The City", 20, "Makki", 30),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", 15, "Makki", 30),
        Surah(92, "الليل", "Al-Layl", "The Night", 21, "Makki", 30),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Brightness", 11, "Makki", 30),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", 8, "Makki", 30),
        Surah(95, "التين", "At-Tin", "The Fig", 8, "Makki", 30),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", 19, "Makki", 30),
        Surah(97, "القدر", "Al-Qadr", "The Night of Decree", 5, "Makki", 30),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Evidence", 8, "Madani", 30),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Madani", 30),
        Surah(100, "العاديات", "Al-'Adiyat", "The Courser", 11, "Makki", 30),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Makki", 30),
        Surah(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", 8, "Makki", 30),
        Surah(103, "العصر", "Al-'Asr", "The Declining Day", 3, "Makki", 30),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Makki", 30),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", 5, "Makki", 30),
        Surah(106, "قريش", "Quraysh", "Quraysh", 4, "Makki", 30),
        Surah(107, "الماعون", "Al-Ma'un", "Small Kindnesses", 7, "Makki", 30),
        Surah(108, "الكوثر", "Al-Kawthar", "Abundance", 3, "Makki", 30),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Makki", 30),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", 3, "Madani", 30),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Makki", 30),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Makki", 30),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Makki", 30),
        Surah(114, "الناس", "An-Nas", "Mankind", 6, "Makki", 30)
    )

    private val SURAH_VERSES_MAP: Map<Int, List<Ayah>> = mapOf(
        1 to listOf(
            Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillāhir-Raḥmānir-Raḥīm", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-ḥamdu lillāhi Rabbil-'ālamīn", "[All] praise is [due] to Allah, Lord of the worlds -"),
            Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-Raḥmānir-Raḥīm", "The Entirely Merciful, the Especially Merciful,"),
            Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Māliki Yawmid-Dīn", "Sovereign of the Day of Recompense."),
            Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyāka na'budu wa iyyāka nasta'īn", "It is You we worship and You we ask for help."),
            Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinaṣ-ṣirāṭal-mustaqīm", "Guide us to the straight path -"),
            Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Ṣirāṭalladhīna an'amta 'alayhim ghayril-maghḍūbi 'alayhim wa laḍ-ḍāllīn", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.")
        ),
        2 to listOf(
            Ayah(2, 1, "الم", "Alif-Lam-Meem", "Alif, Lam, Meem."),
            Ayah(2, 2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "Dhālikal-kitābu lā rayba fīh, hudal-lil-muttaqīn", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -"),
            Ayah(2, 3, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ", "Alladhīna yu'minūna bil-ghaybi wa yuqīmūnaṣ-ṣalāta wa mimmā razaqnāhum yunfiqūn", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,"),
            Ayah(2, 255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allāhu lā ilāha illā Huwal-Ḥayyul-Qayyūm. Lā ta'khudhuhū sinatuw-wa lā nawm. Lahū mā fis-samāwāti wa mā fil-arḍ. Man dhal-ladhī yashfa'u 'indahū illā bi-idhnih. Ya'lamu mā bayna aydīhim wa mā khalfahum, wa lā yuḥīṭūna bishay'im-min 'ilmihī illā bimā shā'. Wasi'a kursiyyuhus-samāwāti wal-arḍ, wa lā ya'ūduhū ḥifẓuhumā, wa Huwal-'Aliyyul-'Aẓīm.", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great. (Ayat al-Kursi)"),
            Ayah(2, 285, "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ", "Āmanar-Rasūlu bimā unzila ilayhi mir-Rabbihī wal-mu'minūn...", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers..."),
            Ayah(2, 286, "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ", "Lā yukallifullāhu nafsan illā wus'ahā...", "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained, and it will bear [the consequence of] what [evil] it has earned...")
        ),
        36 to listOf(
            Ayah(36, 1, "يس", "Yā-Sīn", "Ya-Sin."),
            Ayah(36, 2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-Qur'ānil-Ḥakīm", "By the wise Qur'an."),
            Ayah(36, 3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursalīn", "Indeed you, [O Muhammad], are from among the messengers,"),
            Ayah(36, 4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alā ṣirāṭim-mustaqīm", "On a straight path."),
            Ayah(36, 58, "سَلَامٌ قَوْلًا مِّن رَّبٍّ رَّحِيمٍ", "Salāmun qawlam-mir-Rabbir-Raḥīm", "[And] 'Peace,' a word from a Merciful Lord.")
        ),
        55 to listOf(
            Ayah(55, 1, "الرَّحْمَٰنُ", "Ar-Raḥmān", "The Most Merciful"),
            Ayah(55, 2, "عَلَّمَ الْقُرْآنَ", "'Allamal-Qur'ān", "Taught the Qur'an,"),
            Ayah(55, 3, "خَلَقَ الْإِنسَانَ", "Khalaqal-insān", "Created man,"),
            Ayah(55, 4, "عَلَّمَهُ الْبَيَانَ", "'Allamahul-bayān", "[And] taught him speech."),
            Ayah(55, 13, "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ", "Fabi'ayyi ālā'i Rabbikumā tukadhdhibān", "So which of the favors of your Lord would you deny?")
        ),
        67 to listOf(
            Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabārakalladhī biyadihil-mulku wa Huwa 'alā kulli shay'in Qadīr", "Blessed is He in whose hand is dominion, and He is over all things competent -"),
            Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladhī khalaqal-mawta wal-ḥayāta liyabluwakum ayyukum aḥsanu 'amalā; wa Huwal-'Azīzul-Ghafūr", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -"),
            Ayah(67, 3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ", "Alladhī khalaqa sab'a samāwātin ṭibāqā; mā tarā fī khalqir-Raḥmāni min tafāwut", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.")
        ),
        93 to listOf(
            Ayah(93, 1, "وَالضُّحَىٰ", "Waḍ-ḍuḥā", "By the morning brightness"),
            Ayah(93, 2, "وَاللَّيْلِ إِذَا سَجَىٰ", "Wal-layli idhā sajā", "And [by] the night when it covers with darkness,"),
            Ayah(93, 3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Mā wadda'aka Rabbuka wa mā qalā", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you]."),
            Ayah(93, 4, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "Wa lal-ākhiratu khayrul-laka minal-ūlā", "And the Hereafter is better for you than the first [life]."),
            Ayah(93, 5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "Wa lasawfa yu'ṭīka Rabbuka fatarḍā", "And your Lord is going to give you, and you will be satisfied.")
        ),
        97 to listOf(
            Ayah(97, 1, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Innā anzalnāhu fī laylatil-qadr", "Indeed, We sent the Qur'an down during the Night of Decree."),
            Ayah(97, 2, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "Wa mā adrāka mā laylatul-qadr", "And what can make you know what is the Night of Decree?"),
            Ayah(97, 3, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "Laylatul-qadri khayrum-min alfi shahr", "The Night of Decree is better than a thousand months."),
            Ayah(97, 4, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "Tanazzalul-malā'ikatu war-rūḥu fīhā bi'idhni Rabbihim min kulli amr", "The angels and the Spirit descend therein by permission of their Lord for every matter."),
            Ayah(97, 5, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Salāmun hiya ḥattā maṭla'il-fajr", "Peace it is until the emergence of dawn.")
        ),
        103 to listOf(
            Ayah(103, 1, "وَالْعَصْرِ", "Wal-'aṣr", "By time,"),
            Ayah(103, 2, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Innal-insāna lafī khusr", "Indeed, mankind is in loss,"),
            Ayah(103, 3, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Illalladhīna āmanū wa 'amiluṣ-ṣāliḥāti wa tawāṣaw bil-ḥaqqi wa tawāṣaw biṣ-ṣabr", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.")
        ),
        108 to listOf(
            Ayah(108, 1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Innā a'ṭaynākal-kawthar", "Indeed, We have granted you, [O Muhammad], al-Kawthar."),
            Ayah(108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "Faṣalli li-Rabbika wan-ḥar", "So pray to your Lord and sacrifice [to Him alone]."),
            Ayah(108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Inna shāni'aka huwal-abtar", "Indeed, your enemy is the one cut off.")
        ),
        112 to listOf(
            Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwallāhu Aḥad", "Say, 'He is Allah, [who is] One,"),
            Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allāhuṣ-Ṣamad", "Allah, the Eternal Refuge."),
            Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yūlad", "He neither begets nor is born,"),
            Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahū kufuwan aḥad", "Nor is there to Him any equivalent.'")
        ),
        113 to listOf(
            Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'ūdhu bi-Rabbil-falaq", "Say, 'I seek refuge in the Lord of daybreak"),
            Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "Min sharri mā khalaq", "From the evil of that which He created"),
            Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghāsiqin idhā waqab", "And from the evil of darkness when it settles"),
            Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffāthāti fīl-'uqad", "And from the evil of the blowers in knots"),
            Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri ḥāsidin idhā ḥasad", "And from the evil of an envier when he envies.'")
        ),
        114 to listOf(
            Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'ūdhu bi-Rabbin-nās", "Say, 'I seek refuge in the Lord of mankind,"),
            Ayah(114, 2, "مَلِكِ النَّاسِ", "Malikin-nās", "The Sovereign of mankind."),
            Ayah(114, 3, "إِلَٰهِ النَّاسِ", "Ilāhin-nās", "The God of mankind,"),
            Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswāsil-khannās", "From the evil of the retreating whisperer -"),
            Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladhī yuwaswisu fī ṣudūrin-nās", "Who whispers [evil] into the breasts of mankind -"),
            Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-nās", "From among the jinn and mankind.'")
        )
    )

    fun getVersesForSurah(surahNumber: Int): List<Ayah> {
        val predefined = SURAH_VERSES_MAP[surahNumber]
        if (!predefined.isNullOrEmpty()) {
            return predefined
        }
        val surah = ALL_SURAHS.find { it.number == surahNumber } ?: return emptyList()
        // Provide standard Bismillah and core passage opening
        return listOf(
            Ayah(surahNumber, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillāhir-Raḥmānir-Raḥīm", "In the name of Allah, the Entirely Merciful, the Especially Merciful."),
            Ayah(surahNumber, 2, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Al-ḥamdu lillāhilladhī anzala 'alā 'abdihil-kitāba wa lam yaj'al lahū 'iwajā", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance."),
            Ayah(surahNumber, 3, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal-liyunzhira ba'san shadīdam-mil-ladunhu wa yubashshiral-mu'minīnalladhīna ya'malūnaṣ-ṣāliḥāti anna lahum ajran ḥasanā", "Straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward.")
        )
    }
}
