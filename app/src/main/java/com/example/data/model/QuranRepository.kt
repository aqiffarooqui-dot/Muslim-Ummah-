package com.example.data.model

object QuranRepository {

    val ALL_PARAS: List<Para> = listOf(
        Para(1, "الم (آلم)", "Alif Lam Meem", 1, "Al-Fatihah", 1, listOf(1, 2)),
        Para(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqarah", 142, listOf(2)),
        Para(3, "تِلْكَ الرُّسُلُ", "Tilka-r-Rusul", 2, "Al-Baqarah", 253, listOf(2, 3)),
        Para(4, "لَنْ تَنَالُوا", "Lan Tanaalu", 3, "Ali 'Imran", 93, listOf(3, 4)),
        Para(5, "وَالْمُحْصَنَاتُ", "Wal-Muhsanat", 4, "An-Nisa", 24, listOf(4)),
        Para(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisa", 148, listOf(4, 5)),
        Para(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", 5, "Al-Ma'idah", 82, listOf(5, 6)),
        Para(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'am", 111, listOf(6, 7)),
        Para(9, "قَالَ الْمَلَأُ", "Qalal Mala'o", 7, "Al-A'raf", 88, listOf(7, 8)),
        Para(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfal", 41, listOf(8, 9)),
        Para(11, "يَعْتَذِرُونَ", "Ya'taziroona", 9, "At-Tawbah", 93, listOf(9, 10, 11)),
        Para(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbatin", 11, "Hud", 6, listOf(11, 12)),
        Para(13, "وَمَا أُبَرِّئُ", "Wa Ma Obarri'o", 12, "Yusuf", 53, listOf(12, 13, 14)),
        Para(14, "رُبَمَا", "Rabama", 15, "Al-Hijr", 1, listOf(15, 16)),
        Para(15, "سُبْحَانَ الَّذِي", "Subhanallazi", 17, "Al-Isra", 1, listOf(17, 18)),
        Para(16, "قَالَ أَلَمْ", "Qal Alam", 18, "Al-Kahf", 75, listOf(18, 19, 20)),
        Para(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Lin-Naasi", 21, "Al-Anbiya", 1, listOf(21, 22)),
        Para(18, "قَدْ أَفْلَحَ", "Qadd Aflaha", 23, "Al-Mu'minun", 1, listOf(23, 24, 25)),
        Para(19, "وَقَالَ الَّذِينَ", "Wa Qalal Lazina", 25, "Al-Furqan", 21, listOf(25, 26, 27)),
        Para(20, "أَمَّنْ خَلَقَ", "Amman Khalaq", 27, "An-Naml", 56, listOf(27, 28, 29)),
        Para(21, "اتْلُ مَا أُوحِيَ", "Otlo Ma Oohiya", 29, "Al-'Ankabut", 46, listOf(29, 30, 31, 32, 33)),
        Para(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzab", 31, listOf(33, 34, 35, 36)),
        Para(23, "وَمَا لِيَ", "Wa Maliya", 36, "Ya-Sin", 28, listOf(36, 37, 38, 39)),
        Para(24, "فَمَنْ أَظْلَمُ", "Faman Azlamo", 39, "Az-Zumar", 32, listOf(39, 40, 41)),
        Para(25, "إِلَيْهِ يُرَدُّ", "Ilaihi Yuraddo", 41, "Fussilat", 47, listOf(41, 42, 43, 44, 45)),
        Para(26, "حم (حم عسق)", "Ha'a Meem", 46, "Al-Ahqaf", 1, listOf(46, 47, 48, 49, 50, 51)),
        Para(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, "Adh-Dhariyat", 31, listOf(51, 52, 53, 54, 55, 56, 57)),
        Para(28, "قَدْ سَمِعَ اللَّهُ", "Qadd Sami'allaho", 58, "Al-Mujadila", 1, listOf(58, 59, 60, 61, 62, 63, 64, 65, 66)),
        Para(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", 67, "Al-Mulk", 1, listOf(67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77)),
        Para(30, "عَمَّ يَتَسَاءَلُونَ", "'Amma Yatasa'aloon", 78, "An-Naba", 1, (78..114).toList())
    )

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
        Surah(62, "الجمعة", "Al-Jumu'ah", "The Congregation, Friday", 11, "Madani", 28),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", 11, "Madani", 28),
        Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", 18, "Madani", 28),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", 12, "Madani", 28),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", 12, "Madani", 28),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", 30, "Makki", 29),
        Surah(68, "القلم", "Al-Qalam", "The Pen", 52, "Makki", 29),
        Surah(69, "الحاقة", "Al-Haqqah", "The Reality", 52, "Makki", 29),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", 44, "Makki", 29),
        Surah(71, "نوح", "Nuh", "Noah", 28, "Makki", 29),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", 28, "Makki", 29),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", 20, "Makki", 29),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", 56, "Makki", 29),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", 40, "Makki", 29),
        Surah(76, "الإنسان", "Al-Insan", "The Man", 31, "Madani", 29),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", 50, "Makki", 29),
        Surah(78, "النبأ", "An-Naba", "The Tidings", 40, "Makki", 30),
        Surah(79, "النازعات", "An-Nazi'at", "Those who drag forth", 46, "Makki", 30),
        Surah(80, "عبس", "'Abasa", "He Frowned", 42, "Makki", 30),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", 29, "Makki", 30),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", 19, "Makki", 30),
        Surah(83, "المطففين", "Al-Mutaffifin", "The Defrauding", 36, "Makki", 30),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Splitting Open", 25, "Makki", 30),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", 22, "Makki", 30),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", 17, "Makki", 30),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", 19, "Makki", 30),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", 26, "Makki", 30),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", 30, "Makki", 30),
        Surah(90, "البلد", "Al-Balad", "The City", 20, "Makki", 30),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", 15, "Makki", 30),
        Surah(92, "الليل", "Al-Layl", "The Night", 21, "Makki", 30),
        Surah(93, "الضحى", "Ad-Duha", "The Morning Hours", 11, "Makki", 30),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", 8, "Makki", 30),
        Surah(95, "التين", "At-Tin", "The Fig", 8, "Makki", 30),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", 19, "Makki", 30),
        Surah(97, "القدر", "Al-Qadr", "The Power", 5, "Makki", 30),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", 8, "Madani", 30),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", 8, "Madani", 30),
        Surah(100, "العاديات", "Al-'Adiyat", "The Courser", 11, "Makki", 30),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", 11, "Makki", 30),
        Surah(102, "التكاثر", "At-Takathur", "The Rivalry in world increase", 8, "Makki", 30),
        Surah(103, "العصر", "Al-'Asr", "The Declining Day", 3, "Makki", 30),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", 9, "Makki", 30),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", 5, "Makki", 30),
        Surah(106, "قريش", "Quraysh", "Quraysh", 4, "Makki", 30),
        Surah(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", 7, "Makki", 30),
        Surah(108, "الكوثر", "Al-Kawthar", "The Abundance", 3, "Makki", 30),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", 6, "Makki", 30),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", 3, "Madani", 30),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", 5, "Makki", 30),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", 4, "Makki", 30),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", 5, "Makki", 30),
        Surah(114, "الناس", "An-Nas", "Mankind", 6, "Makki", 30)
    )

    private val SURAH_VERSES_MAP: Map<Int, List<Ayah>> = mapOf(
        1 to listOf(
            Ayah(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillāhir-Raḥmānir-Raḥīm", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے", "शुरू अल्लाह के नाम से जो बड़ा मेहरबान, निहायत रहम वाला है।", "Bismillah ir-Rahman ir-Rahim"),
            Ayah(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Al-ḥamdu lillāhi Rabbil-'ālamīn", "[All] praise is [due] to Allah, Lord of the worlds -", "سب تعریفیں اللہ ہی کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے", "सब तारीफ़ें अल्लाह ही के लिए हैं जो सारे जहाँ का रब है।", "Sab taareefein Allah hi ke liye hain jo tamaam jahanon ka paalne wala hai"),
            Ayah(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "Ar-Raḥmānir-Raḥīm", "The Entirely Merciful, the Especially Merciful,", "بڑا مہربان نہایت رحم والا", "बड़ा मेहरबान, निहायت रहम फरमाने वाला।", "Bada Meherban, nihayat rehem farmane wala"),
            Ayah(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Māliki Yawmid-Dīn", "Sovereign of the Day of Recompense.", "روز جزا کا مالک", "बदले के दिन (क़यामत) का मालिक।", "Roz-e-Jaza ka Maalik"),
            Ayah(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Iyyāka na'budu wa iyyāka nasta'īn", "It is You we worship and You we ask for help.", "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں", "हम तेरी ही इबादत करते हैं और तुझ ही से मदद चाहते हैं।", "Hum Teri hi ibadat karte hain aur Tujh hi se madad chahte hain"),
            Ayah(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Ihdinaṣ-ṣirāṭal-mustaqīm", "Guide us to the straight path -", "ہمیں سیدھے راستے کی ہدایت فرما", "हमें सीधे रास्ते की हिदायत अता फरमा।", "Humein seedhe raaste ki hidayat ata farma"),
            Ayah(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "Ṣirāṭalladhīna an'amta 'alayhim ghayril-maghḍūbi 'alayhim wa laḍ-ḍāllīn", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "ان لوگوں کا راستہ جن پر تو نے انعام فرمایا، نہ ان کا جن پر غضب نازل ہوا اور نہ گمراہوں کا۔", "उन लोगों का रास्ता जिन पर तूने इनाम फरमाया, न उनका जिन पर ग़ज़ब हुआ और न भटके हुओं का।", "Un logon ka raasta jin par Tu ne inaam farmaya, na unka jin par ghazab hua aur na bhatke huon ka.")
        ),
        2 to listOf(
            Ayah(2, 1, "الم", "Alif-Lām-Mīm", "Alif, Lam, Meem.", "الف لام میم", "अलिफ़-लाम-मीम।", "Alif Laam Meem"),
            Ayah(2, 2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "Dhālikal-Kitābu lā rayba fīhi hudal-lil-muttaqīn", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -", "یہ وہ کتاب ہے جس میں کوئی شک نہیں، پرہیزگاروں کے لیے ہدایت ہے", "यह वह किताब है जिसमें कोई शक नहीं, परहेज़गारों के लिए हिदायत है।", "Yeh woh Kitaab hai jisme koi shaq nahi, muttaqiyon ke liye hidayat hai"),
            Ayah(2, 255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ", "Allāhu lā ilāha illā Huwal-Ḥayyul-Qayyūm; lā ta'khudhuhū sinatuw-wa lā nawm; lahū mā fis-samāwāti wa mā fīl-arḍ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. (Ayat al-Kursi)", "اللہ جس کے سوا کوئی معبود نہیں، وہ ہمیشہ زندہ اور سب کا قائم رکھنے والا ہے۔ نہ اسے اونگھ آتی ہے نہ نیند۔ اسی کا ہے جو آسمانوں اور زمین میں ہے۔", "अल्लाह, जिसके सिवा कोई माबूद नहीं, वह हमेशा ज़िंदा और सबको क़ायम रखने वाला है। उसे न ऊँघ आती है न नींद।", "Allah jiske siwa koi mabood nahi, Woh hamesha zinda aur sabka qayam rakhne wala hai (Ayat al-Kursi)")
        ),
        36 to listOf(
            Ayah(36, 1, "يس", "Yā-Sīn", "Ya, Seen.", "یس", "या-सीन।", "Ya-Sin"),
            Ayah(36, 2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-Qur'ānil-Ḥakīm", "By the wise Qur'an.", "حکمت والے قرآن کی قسم", "हिकमत वाले क़ुरआन की क़सम।", "Hikmat wale Quran ki qasam"),
            Ayah(36, 3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursalīn", "Indeed you, [O Muhammad], are from among the messengers,", "بے شک آپ ضرور پیغمبروں میں سے ہیں", "बेशक आप रसूलों में से हैं।", "Beshak aap paighambaron mein se hain"),
            Ayah(36, 4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alā ṣirāṭim-mustaqīm", "On a straight path.", "سیدھے راستے پر", "सीधे रास्ते पर।", "Seedhe raaste par"),
            Ayah(36, 5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanzīlal-'Azīzir-Raḥīm", "[This is] a revelation of the Exalted in Might, the Merciful,", "یہ زبردست اور نہایت رحم فرمانے والے کا نازل کیا ہوا ہے", "यह ज़बरदस्त और निहायत रहम वाले का नाज़िल किया हुआ है।", "Yeh zabardast aur nihayat rehem farmane wale ka nazil kiya hua hai")
        ),
        67 to listOf(
            Ayah(67, 1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Tabārakalladhī biyadihil-mulku wa Huwa 'alā kulli shay'in Qadīr", "Blessed is He in whose hand is dominion, and He is over all things competent -", "بابرکت ہے وہ جس کے ہاتھ میں بادشاہی ہے اور وہ ہر چیز پر قادر ہے", "बड़ी बरकत वाला है वह जिसके हाथ में मुल्क व हुकूमत है और वह हर चीज़ पर क़ादिर है।", "Ba-barkat hai Woh jiske haath mein badshahi hai aur Woh har cheez par qaadir hai"),
            Ayah(67, 2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "Alladhī khalaqal-mawta wal-ḥayāta liyabluwakum ayyukum aḥsanu 'amalā; wa Huwal-'Azīzul-Ghafūr", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "جس نے موت اور زندگی کو پیدا کیا تاکہ تمہیں آزمائے کہ تم میں سے اچھے عمل کس کے ہیں", "जिसने मौत और ज़िन्दगी को पैदा किया ताकि तुम्हें आज़माए कि तुम में से कौन बेहतर अमल करता है।", "Jisne maut aur zindagi ko paida kiya taake tumhe aazmaye")
        ),
        112 to listOf(
            Ayah(112, 1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Qul Huwallāhu Aḥad", "Say, 'He is Allah, [who is] One,", "کہہ دیجیے کہ وہ اللہ ایک ہے", "कह दो कि वह अल्लाह एक है।", "Kaho ke Woh Allah Ek hai"),
            Ayah(112, 2, "اللَّهُ الصَّمَدُ", "Allāhuṣ-Ṣamad", "Allah, the Eternal Refuge.", "اللہ بے نیاز ہے", "अल्लाह बेनियाज़ (सबका सहारा) है।", "Allah Be-niyaz hai"),
            Ayah(112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "Lam yalid wa lam yūlad", "He neither begets nor is born,", "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے", "न उसने किसी को जना और न वह जना गया।", "Na uski koi aulaad hai aur na woh kisi ki aulaad hai"),
            Ayah(112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Wa lam yakul-lahū kufuwan aḥad", "Nor is there to Him any equivalent.'", "اور کوئی اس کا ہمسر نہیں ہے۔", "और कोई उसका हमसर (बराबरी का) नहीं।", "Aur koi uska humsar nahi hai.")
        ),
        113 to listOf(
            Ayah(113, 1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Qul a'ūdhu bi-Rabbil-falaq", "Say, 'I seek refuge in the Lord of daybreak", "کہو کہ میں صبح کے مالک کی پناہ مانگتا ہوں", "कह दो कि मैं सुबह के रब की पनाह माँगता हूँ।", "Kaho ke main subah ke Rab ki panah mangta hoon"),
            Ayah(113, 2, "مِن شَرِّ مَا خَلَقَ", "Min sharri mā khalaq", "From the evil of that which He created", "اس کی تمام مخلوقات کے شر سے", "उसकी पैदा की हुई हर चीज़ के शर्र से।", "Uski paida ki hui har cheez ke shar se"),
            Ayah(113, 3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "Wa min sharri ghāsiqin idhā waqab", "And from the evil of darkness when it settles", "اور اندھیری رات کے شر سے جب وہ چھا جائے", "और रात के अंधेरे के शर्र से जब वह छा जाए।", "Aur andheri raat ke shar se jab woh chha jaye"),
            Ayah(113, 4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "Wa min sharrin-naffāthāti fīl-'uqad", "And from the evil of the blowers in knots", "اور گرہوں میں پھونکنے والیوں کے شر سے", "और गिरहों में फूँकने वालों के शर्र से।", "Aur girhon mein phoonkne walon ke shar se"),
            Ayah(113, 5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "Wa min sharri ḥāsidin idhā ḥasad", "And from the evil of an envier when he envies.'", "اور حسد کرنے والے کے شر سے جب وہ حسد کرے۔", "और हसद (ईर्ष्या) करने वाले के शर्र से जब वह हसद करे।", "Aur hasad karne wale ke shar se jab woh hasad kare.")
        ),
        114 to listOf(
            Ayah(114, 1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Qul a'ūdhu bi-Rabbin-nās", "Say, 'I seek refuge in the Lord of mankind,", "کہو کہ میں انسانوں کے پروردگار کی پناہ مانگتا ہوں", "कह दो कि मैं इंसानों के रब की पनाह माँगता हूँ।", "Kaho ke main insaano ke Rab ki panah mangta hoon"),
            Ayah(114, 2, "مَلِكِ النَّاسِ", "Malikin-nās", "The Sovereign of mankind.", "انسانوں کے بادشاہ کی", "इंसानों के बादशाह की।", "Insaano ke Badshah ki"),
            Ayah(114, 3, "إِلَٰهِ النَّاسِ", "Ilāhin-nās", "The God of mankind,", "انسانوں کے معبود برحق کی", "इंसानों के माबूद की।", "Insaano ke Mabood-e-Haqiqi ki"),
            Ayah(114, 4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "Min sharril-waswāsil-khannās", "From the evil of the retreating whisperer -", "بار بار وسوسہ ڈال کر پیچھے ہٹ جانے والے کے شر سے", "पीछे हट जाने वाले वसवसा डालने वाले के शर्र से।", "Waswasa daal kar peeche hat jaane wale ke shar se"),
            Ayah(114, 5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Alladhī yuwaswisu fī ṣudūrin-nās", "Who whispers [evil] into the breasts of mankind -", "جو انسانوں کے دلوں میں وسوسہ ڈالتا ہے", "जो लोगों के दिलों में वसवसा डालता है।", "Jo logon ke dilon mein waswasa daalta hai"),
            Ayah(114, 6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "Minal-jinnati wan-nās", "From among the jinn and mankind.'", "خواہ وہ جنوں میں سے ہو یا انسانوں میں سے۔", "जिन्नों में से हो या इंसानों में से।", "Khuwah woh jinnaat mein se ho ya insaano mein se.")
        )
    )

    fun getVersesForSurah(surahNumber: Int): List<Ayah> {
        val predefined = SURAH_VERSES_MAP[surahNumber]
        if (!predefined.isNullOrEmpty()) {
            return predefined
        }
        val surah = ALL_SURAHS.find { it.number == surahNumber } ?: return emptyList()
        return listOf(
            Ayah(surahNumber, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Bismillāhir-Raḥmānir-Raḥīm", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے", "शुरू अल्लाह के नाम से जो बड़ा मेहरबान, निहायत रहम वाला है।", "Bismillah ir-Rahman ir-Rahim"),
            Ayah(surahNumber, 2, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "Al-ḥamdu lillāhilladhī anzala 'alā 'abdihil-kitāba wa lam yaj'al lahū 'iwajā", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.", "سب تعریفیں اللہ کے لیے ہیں جس نے اپنے بندے پر کتاب اتاری اور اس میں کوئی کجی نہ رکھی", "सब तारीफें अल्लाह के लिए हैं जिसने अपने बन्दे पर यह किताब उतारी।", "Alhamdu lillahi allazi anzala ala abdihil kitaab"),
            Ayah(surahNumber, 3, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "Qayyimal-liyunzhira ba'san shadīdam-mil-ladunhu wa yubashshiral-mu'minīnalladhīna ya'malūnaṣ-ṣāliḥāti anna lahum ajran ḥasanā", "Straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward.", "ٹھیک اور سیدھی کتاب تاکہ اپنے پاس سے سخت عذاب سے ڈرائے اور مومنوں کو خوشخبری دے۔", "सीधी सच्ची किताब ताकि सख्त अज़ाब से आगाह करे और ईमान वालों को बशारत दे।", "Seedhi sachi kitaab taake azaab se khabardaar kare aur momino ko khushkhabri de.")
        )
    }

    fun getSurahsForPara(paraNumber: Int): List<Surah> {
        val para = ALL_PARAS.find { it.number == paraNumber } ?: return emptyList()
        return ALL_SURAHS.filter { it.number in para.surahsInPara }
    }
}
