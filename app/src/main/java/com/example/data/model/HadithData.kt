package com.example.data.model

data class HadithBook(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val author: String,
    val hadithCountApprox: String,
    val description: String,
    val totalChapters: Int
)

data class HadithChapter(
    val id: Int,
    val bookId: String,
    val nameEnglish: String,
    val nameArabic: String,
    val hadithCount: Int
)

data class HadithItem(
    val id: Int,
    val bookId: String,
    val bookName: String,
    val chapterId: Int,
    val chapterName: String,
    val hadithNumber: String,
    val narrator: String,
    val arabicText: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val hindiTranslation: String,
    val grade: String = "Sahih (صحيح)",
    val reference: String = "Canonical Islamic Tradition"
)

object HadithRepository {

    val MAIN_BOOKS = listOf(
        HadithBook(
            id = "bukhari",
            nameEnglish = "Sahih al-Bukhari",
            nameArabic = "صحيح البخاري",
            author = "Imam Muhammad al-Bukhari (rahimahullah)",
            hadithCountApprox = "7,563 Hadiths",
            description = "The most authentic book of Hadith in Islam, universally accepted by the Ummah.",
            totalChapters = 97
        ),
        HadithBook(
            id = "muslim",
            nameEnglish = "Sahih Muslim",
            nameArabic = "صحيح مسلم",
            author = "Imam Muslim ibn al-Hajjaj (rahimahullah)",
            hadithCountApprox = "7,500 Hadiths",
            description = "Second only to Sahih al-Bukhari in authority and rigorous authentication.",
            totalChapters = 56
        ),
        HadithBook(
            id = "tirmidhi",
            nameEnglish = "Jami` at-Tirmidhi",
            nameArabic = "جامع الترمذي",
            author = "Imam Abu 'Isa at-Tirmidhi (rahimahullah)",
            hadithCountApprox = "3,956 Hadiths",
            description = "Distinguished by commentary on legal rulings, Hadith gradings, and comparative Fiqh.",
            totalChapters = 49
        ),
        HadithBook(
            id = "abudawud",
            nameEnglish = "Sunan Abi Dawud",
            nameArabic = "سنن أبي داود",
            author = "Imam Abu Dawud as-Sijistani (rahimahullah)",
            hadithCountApprox = "5,274 Hadiths",
            description = "Renowned for focus on Ahkam (legal rulings and practical Sunnah of worship).",
            totalChapters = 43
        ),
        HadithBook(
            id = "nasai",
            nameEnglish = "Sunan an-Nasa'i",
            nameArabic = "سنن النسائي",
            author = "Imam Ahmad an-Nasa'i (rahimahullah)",
            hadithCountApprox = "5,760 Hadiths",
            description = "Famed for the compiler's strict criteria in chain verification and legal nuances.",
            totalChapters = 51
        ),
        HadithBook(
            id = "ibnmajah",
            nameEnglish = "Sunan Ibn Majah",
            nameArabic = "سنن ابن ماجه",
            author = "Imam Ibn Majah al-Qazwini (rahimahullah)",
            hadithCountApprox = "4,341 Hadiths",
            description = "The sixth book of the canonical Kutub al-Sittah covering essential guidance.",
            totalChapters = 37
        ),
        HadithBook(
            id = "nawawi40",
            nameEnglish = "Forty Hadith of Imam Nawawi",
            nameArabic = "الأربعون النووية",
            author = "Imam Yahya ibn Sharaf an-Nawawi (rahimahullah)",
            hadithCountApprox = "42 Hadiths",
            description = "The most famous compilation encapsulating the core foundations and spirituality of Islam.",
            totalChapters = 5
        ),
        HadithBook(
            id = "riyad",
            nameEnglish = "Riyad as-Salihin",
            nameArabic = "رياض الصالحين",
            author = "Imam Yahya ibn Sharaf an-Nawawi (rahimahullah)",
            hadithCountApprox = "1,896 Hadiths",
            description = "The Meadows of the Righteous; essential moral, ethical, and spiritual wisdom.",
            totalChapters = 20
        )
    )

    val CHAPTERS = listOf(
        // Bukhari
        HadithChapter(1, "bukhari", "Revelation (Bad' al-Wahy)", "بدء الوحي", 7),
        HadithChapter(2, "bukhari", "Belief / Faith (Al-Iman)", "كتاب الإيمان", 53),
        HadithChapter(3, "bukhari", "Virtues of the Quran (Fada'il al-Qur'an)", "فضائل القرآن", 40),
        HadithChapter(4, "bukhari", "Good Manners & Etiquette (Al-Adab)", "كتاب الأدب", 128),
        
        // Muslim
        HadithChapter(5, "muslim", "Faith & Pillars of Islam (Al-Iman)", "كتاب الإيمان", 65),
        HadithChapter(6, "muslim", "Purification & Wudu (At-Taharah)", "كتاب الطهارة", 45),
        HadithChapter(7, "muslim", "Supplication, Remembrance & Knowledge", "كتاب الذكر والدعاء والعلم", 90),
        
        // Tirmidhi
        HadithChapter(8, "tirmidhi", "Righteousness & Good Character (Al-Birr)", "كتاب البر والصلة", 35),
        HadithChapter(9, "tirmidhi", "Asceticism & Reliance on Allah (Az-Zuhd)", "كتاب الزهد", 42),
        
        // Abu Dawud
        HadithChapter(10, "abudawud", "Prayer & Worship (As-Salah)", "كتاب الصلاة", 110),
        HadithChapter(11, "abudawud", "General Manners & Conduct (Al-Adab)", "كتاب الأدب", 60),
        
        // An-Nasa'i
        HadithChapter(12, "nasai", "Dutifulness to Parents & Struggle (Al-Jihad)", "كتاب الجهاد وبر الوالدين", 50),
        
        // Ibn Majah
        HadithChapter(13, "ibnmajah", "Seeking Knowledge & Sunnah (Al-Muqaddimah)", "المقدمة وفضل العلم", 32),
        
        // Nawawi's 40
        HadithChapter(14, "nawawi40", "Core Foundations & Sincerity", "أصول الدين والإخلاص", 20),
        HadithChapter(15, "nawawi40", "Mindfulness & Good Character", "التقوى وحسن الخلق", 22),
        
        // Riyad as-Salihin
        HadithChapter(16, "riyad", "Patience & Gratitude (As-Sabr wash-Shukr)", "باب الصبر والشكر", 55)
    )

    val ALL_HADITHS = listOf(
        // Bukhari
        HadithItem(
            id = 1,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterId = 1,
            chapterName = "Revelation (Bad' al-Wahy)",
            hadithNumber = "Hadith 1",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            englishTranslation = "I heard the Messenger of Allah (ﷺ) say: 'The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اعمال کا دارومدار نیتوں پر ہے، اور ہر شخص کے لیے وہی ہے جس کی اس نے نیت کی۔ پس جس کی ہجرت دنیا کمانے کے لیے ہو یا کسی عورت سے نکاح کے لیے، تو اس کی ہجرت اسی کے لیے ہے جس کی طرف اس نے ہجرت کی۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: आमाल (कर्मों) का दारोमदार नीयतों पर है, और हर इंसान को वही मिलेगा जिसकी उसने नीयत की। पस जिसकी हिजरत दुनिया कमाने या किसी औरत से निकाह के लिए हो, तो उसकी हिजरत उसी के लिए है जिसके लिए उसने हिजरत की।",
            grade = "Sahih (صحيح)",
            reference = "Sahih al-Bukhari 1 • In-book reference: Book 1, Hadith 1"
        ),
        HadithItem(
            id = 2,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterId = 2,
            chapterName = "Belief / Faith (Al-Iman)",
            hadithNumber = "Hadith 13",
            narrator = "Narrated Anas bin Malik (رضي الله عنه)",
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            englishTranslation = "The Prophet (ﷺ) said: 'None of you truly believes until he loves for his brother (or sister) what he loves for himself.'",
            urduTranslation = "نبی کریم صلی اللہ علیہ وسلم نے فرمایا: تم میں سے کوئی شخص اس وقت تک سچا مومن نہیں ہو سکتا جب تک کہ وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔",
            hindiTranslation = "नबी करीम (ﷺ) ने फरमाया: तुम में से कोई शख्स उस वक्त तक सच्चा मोमिन नहीं हो सकता जब तक कि वह अपने भाई के लिए वही पसंद न करे जो अपने लिए पसंद करता है।",
            grade = "Sahih (صحيح)",
            reference = "Sahih al-Bukhari 13 • In-book reference: Book 2, Hadith 6"
        ),
        HadithItem(
            id = 3,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterId = 3,
            chapterName = "Virtues of the Quran (Fada'il al-Qur'an)",
            hadithNumber = "Hadith 5027",
            narrator = "Narrated 'Uthman bin 'Affan (رضي الله عنه)",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            englishTranslation = "The Prophet (ﷺ) said: 'The best among you are those who learn the Quran and teach it to others.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: تم میں سے بہترین وہ شخص ہے جو قرآن سیکھے اور اسے دوسروں کو سکھائے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: तुम में सबसे बेहतर वह इंसान है जो क़ुरआन सीखे और दूसरों को सिखाए।",
            grade = "Sahih (صحيح)",
            reference = "Sahih al-Bukhari 5027 • In-book reference: Book 66, Hadith 49"
        ),
        HadithItem(
            id = 4,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterId = 4,
            chapterName = "Good Manners & Etiquette (Al-Adab)",
            hadithNumber = "Hadith 6094",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ",
            englishTranslation = "The Prophet (ﷺ) said: 'Whoever believes in Allah and the Last Day should speak good or remain silent; and whoever believes in Allah and the Last Day should be generous to his neighbor; and whoever believes in Allah and the Last Day should be hospitable to his guest.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو شخص اللہ اور آخرت کے دن پر ایمان رکھتا ہو اسے چاہیے کہ وہ بھلی بات کہے یا خاموش رہے، اور جو اللہ اور یوم آخرت پر ایمان رکھتا ہو وہ اپنے پڑوسی کی عزت کرے، اور جو اللہ اور یوم آخرت پر ایمان رکھتا ہو وہ اپنے مہمان کی مہمان نوازی کرے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: जो शख्स अल्लाह और आखिरत के दिन पर ईमान रखता हो उसे चाहिए कि वह अच्छी बात कहे या खामोश रहे, और जो अल्लाह और कयामत पर ईमान रखता हो वह अपने पड़ोसी की इज्जत करे, और जो अल्लाह और आखिरत पर ईमान रखता हो वह अपने मेहमान की खातिरदारी करे।",
            grade = "Sahih (صحيح)",
            reference = "Sahih al-Bukhari 6094 • In-book reference: Book 78, Hadith 125"
        ),

        // Muslim
        HadithItem(
            id = 5,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterId = 5,
            chapterName = "Faith & Pillars of Islam (Al-Iman)",
            hadithNumber = "Hadith 8",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "الإِسْلاَمُ أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلاً",
            englishTranslation = "The Messenger of Allah (ﷺ) said to Jibreel: 'Islam is that you testify there is no god worthy of worship except Allah and that Muhammad is the Messenger of Allah, establish Salah, pay Zakah, fast during Ramadan, and perform pilgrimage to the House if you are able to do so.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اسلام یہ ہے کہ تم گواہی دو کہ اللہ کے سوا کوئی معبود برحق نہیں اور محمد صلی اللہ علیہ وسلم اللہ کے رسول ہیں، اور نماز قائم کرو، زکوٰۃ ادا کرو، رمضان کے روزے رکھو، اور اگر استطاعت ہو تو بیت اللہ کا حج کرو۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: इस्लाम यह है कि तुम गवाही दो कि अल्लाह के सिवा कोई सच्चा माबूद नहीं और मुहम्मद (ﷺ) अल्लाह के रसूल हैं, नमाज़ कायम करो, ज़कात अदा करो, रमज़ान के रोज़े रखो, और हज करो अगर वहां तक जाने की ताकत रखते हो।",
            grade = "Sahih (صحيح)",
            reference = "Sahih Muslim 8 • In-book reference: Book 1, Hadith 1"
        ),
        HadithItem(
            id = 6,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterId = 6,
            chapterName = "Purification & Wudu (At-Taharah)",
            hadithNumber = "Hadith 223",
            narrator = "Narrated Abu Malik al-Ash'ari (رضي الله عنه)",
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ أَوْ تَمْلأُ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ، وَالصَّلاَةُ نُورٌ، وَالصَّدَقَةُ بُرْهَانٌ، وَالصَّبْرُ ضِيَاءٌ، وَالْقُرْآنُ حُجَّةٌ لَكَ أَوْ عَلَيْكَ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Purity is half of faith. Alhamdulillah fills the Scale. SubhanAllah and Alhamdulillah fill what is between the heavens and the earth. Prayer is light, charity is proof, patience is illumination, and the Quran is an argument for you or against you.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: پاکیزگی نصف ایمان ہے، اور الحمد للہ میزان کو بھر دیتا ہے، اور سبحان اللہ اور الحمد للہ آسمانوں اور زمین کے درمیانی خلا کو بھر دیتے ہیں، اور نماز نور ہے، صدقہ دلیل ہے، صبر روشنی ہے، اور قرآن تمہارے حق میں یا تمہارے خلاف حجت ہے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: पाकीज़गी आधा ईमान है, और अल्हम्दुलिल्लाह तराज़ू को भर देता है, और सुब्हानअल्लाह व अल्हम्दुलिल्लाह ज़मीन व आसमान के दरमियान की जगह को भर देते हैं, नमाज़ नूर है, सदका दलील है, सब्र रोशनी है, और क़ुरआन तुम्हारे हक़ में या तुम्हारे खिलाफ हुज्जत है।",
            grade = "Sahih (صحيح)",
            reference = "Sahih Muslim 223 • In-book reference: Book 2, Hadith 1"
        ),
        HadithItem(
            id = 7,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterId = 7,
            chapterName = "Supplication, Remembrance & Knowledge",
            hadithNumber = "Hadith 2699",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ، وَمَا اجْتَمَعَ قَوْمٌ فِي بَيْتٍ مِنْ بُيُوتِ اللَّهِ يَتْلُونَ كِتَابَ اللَّهِ وَيَتَدَارَسُونَهُ بَيْنَهُمْ إِلاَّ نَزَلَتْ عَلَيْهِمُ السَّكِينَةُ وَغَشِيَتْهُمُ الرَّحْمَةُ وَحَفَّتْهُمُ الْمَلاَئِكَةُ وَذَكَرَهُمُ اللَّهُ فِيمَنْ عِنْدَهُ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Whoever treads a path seeking knowledge, Allah makes easy for him a path to Paradise. No people gather together in one of the houses of Allah, reciting the Book of Allah and studying it between themselves, but tranquility descends upon them, mercy covers them, the angels surround them, and Allah mentions them to those near Him.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو شخص علم کی تلاش میں کسی راستے پر چلے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔ اور جب بھی کچھ لوگ اللہ کے گھروں میں سے کسی گھر میں جمع ہو کر اللہ کی کتاب کی تلاوت کرتے ہیں اور اسے آپس میں پڑھتے پڑھاتے ہیں، تو ان پر سکینت نازل ہوتی ہے، رحمت انہیں ڈھانپ لیتی ہے، فرشتے انہیں گھیر لیتے ہیں اور اللہ اپنے پاس موجود فرشتوں میں ان کا ذکر فرماتا ہے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: जो शख्स इल्म (ज्ञान) की तलाश में किसी रास्ते पर चले, अल्लाह उसके लिए जन्नत का रास्ता आसान कर देता है। और जब भी कुछ लोग अल्लाह के घरों (मस्जिदों) में जमा होकर क़ुरआन पढ़ते और एक दूसरे को सिखाते हैं, उन पर सकीना (शांति) नाज़िल होती है, रहमत उन्हें ढांप लेती है और फरिश्ते उन्हें घेर लेते हैं।",
            grade = "Sahih (صحيح)",
            reference = "Sahih Muslim 2699 • In-book reference: Book 48, Hadith 50"
        ),

        // Tirmidhi
        HadithItem(
            id = 8,
            bookId = "tirmidhi",
            bookName = "Jami` at-Tirmidhi",
            chapterId = 8,
            chapterName = "Righteousness & Good Character (Al-Birr)",
            hadithNumber = "Hadith 1956",
            narrator = "Narrated Abu Dharr (رضي الله عنه)",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Your smiling in the face of your brother is charity for you.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اپنے بھائی کے چہرے پر تمہارا مسکرانا تمہارے لیے صدقہ ہے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: अपने भाई के चेहरे को देखकर तुम्हारा मुस्कुराना भी तुम्हारे लिए सदका है।",
            grade = "Sahih (صحيح)",
            reference = "Jami` at-Tirmidhi 1956 • In-book reference: Book 27, Hadith 62"
        ),
        HadithItem(
            id = 9,
            bookId = "tirmidhi",
            bookName = "Jami` at-Tirmidhi",
            chapterId = 9,
            chapterName = "Asceticism & Reliance on Allah (Az-Zuhd)",
            hadithNumber = "Hadith 2344",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "لَوْ أَنَّكُمْ تَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
            englishTranslation = "The Prophet (ﷺ) said: 'If you were to rely upon Allah with the required reliance, He would provide for you just as He provides for the birds: they go out in the morning with empty stomachs and return in the evening full.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اگر تم اللہ پر ویسا توکل کرو جیسا کہ اس پر توکل کرنے کا حق ہے، تو وہ تمہیں اسی طرح رزق دے گا جس طرح پرندوں کو رزق دیتا ہے؛ وہ صبح خالی پیٹ نکلتے ہیں اور شام کو پیٹ بھر کر لوٹتے ہیں۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: अगर तुम अल्लाह पर वैसा भरोसा (तवक्कुल) करो जैसा भरोसा करने का हक है, तो वह तुम्हें उसी तरह रिज़्क देगा जैसे परिंदों को देता है; वे सुबह भूखे पेट निकलते हैं और शाम को पेट भर कर लौटते हैं।",
            grade = "Hasan Sahih (حسن صحيح)",
            reference = "Jami` at-Tirmidhi 2344 • In-book reference: Book 36, Hadith 41"
        ),

        // Abu Dawud
        HadithItem(
            id = 10,
            bookId = "abudawud",
            bookName = "Sunan Abi Dawud",
            chapterId = 10,
            chapterName = "Prayer & Worship (As-Salah)",
            hadithNumber = "Hadith 495",
            narrator = "Narrated 'Amr bin Shu'aib from his father from his grandfather (رضي الله عنهم)",
            arabicText = "مُرُوا أَوْلاَدَكُمْ بِالصَّلاَةِ وَهُمْ أَبْنَاءُ سَبْعِ سِنِينَ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Enjoin your children to pray when they are seven years old.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اپنی اولاد کو نماز کا حکم دو جب وہ سات سال کے ہو جائیں۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: अपनी औलाद को नमाज़ का हुक्म दो जब वे सात साल के हो जाएं।",
            grade = "Hasan Sahih (حسن صحيح)",
            reference = "Sunan Abi Dawud 495 • In-book reference: Book 2, Hadith 105"
        ),
        HadithItem(
            id = 11,
            bookId = "abudawud",
            bookName = "Sunan Abi Dawud",
            chapterId = 11,
            chapterName = "General Manners & Conduct (Al-Adab)",
            hadithNumber = "Hadith 4799",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "لاَ يَشْكُرُ اللَّهَ مَنْ لاَ يَشْكُرُ النَّاسَ",
            englishTranslation = "The Prophet (ﷺ) said: 'He who does not thank people does not thank Allah.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو انسانوں کا شکر ادا نہیں کرتا، وہ اللہ کا شکر بھی ادا نہیں کرتا۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: जो इंसानों का शुक्र अदा नहीं करता, वह अल्लाह का शुक्र भी अदा नहीं करता।",
            grade = "Sahih (صحيح)",
            reference = "Sunan Abi Dawud 4799 • In-book reference: Book 43, Hadith 27"
        ),

        // An-Nasa'i
        HadithItem(
            id = 12,
            bookId = "nasai",
            bookName = "Sunan an-Nasa'i",
            chapterId = 12,
            chapterName = "Dutifulness to Parents & Struggle (Al-Jihad)",
            hadithNumber = "Hadith 3104",
            narrator = "Narrated Mu'awiyah bin Jahimah (رضي الله عنه)",
            arabicText = "الْزَمْ رِجْلَهَا فَثَمَّ الْجَنَّةُ",
            englishTranslation = "A man asked the Prophet (ﷺ) about going on expedition, and the Prophet asked: 'Is your mother alive?' He said: 'Yes.' The Prophet (ﷺ) said: 'Stay by her feet, for Paradise is there.'",
            urduTranslation = "ایک صحابی نے رسول اللہ صلی اللہ علیہ وسلم سے جہاد میں شرکت کی اجازت چاہی تو آپ ﷺ نے پوچھا: کیا تمہاری والدہ حیات ہیں؟ انہوں نے عرض کیا: جی ہاں۔ آپ ﷺ نے فرمایا: ان کی خدمت لازم پکڑ لو، کیونکہ جنت ان کے قدموں تلے ہے۔",
            hindiTranslation = "एक सहाबी ने रसूलुल्लाह (ﷺ) से पूछा कि मैं जिहाद में जाना चाहता हूँ। आप (ﷺ) ने पूछा: क्या तुम्हारी माँ ज़िंदा हैं? उन्होंने कहा: हाँ। नबी (ﷺ) ने फरमाया: उनके पैरों से लिपटे रहो (उनकी सेवा करो), क्योंकि जन्नत वहीं है।",
            grade = "Sahih (صحيح)",
            reference = "Sunan an-Nasa'i 3104 • In-book reference: Book 25, Hadith 20"
        ),

        // Ibn Majah
        HadithItem(
            id = 13,
            bookId = "ibnmajah",
            bookName = "Sunan Ibn Majah",
            chapterId = 13,
            chapterName = "Seeking Knowledge & Sunnah (Al-Muqaddimah)",
            hadithNumber = "Hadith 224",
            narrator = "Narrated Anas bin Malik (رضي الله عنه)",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Seeking knowledge is an obligation upon every Muslim.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: علم حاصل کرنا ہر مسلمان پر فرض ہے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: इल्म (ज्ञान) हासिल करना हर मुसलमान (मर्द और औरत) पर फर्ज़ है।",
            grade = "Sahih (صحيح)",
            reference = "Sunan Ibn Majah 224 • In-book reference: Introduction, Hadith 224"
        ),

        // Nawawi's 40 Hadith
        HadithItem(
            id = 14,
            bookId = "nawawi40",
            bookName = "Forty Hadith of Imam Nawawi",
            chapterId = 14,
            chapterName = "Core Foundations & Sincerity",
            hadithNumber = "Hadith 7",
            narrator = "Narrated Tamim ad-Dari (رضي الله عنه)",
            arabicText = "الدِّينُ النَّصِيحَةُ، قُلْنَا: لِمَنْ؟ قَالَ: لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ",
            englishTranslation = "The Prophet (ﷺ) said: 'Religion is sincerity.' We said: 'To whom?' He said: 'To Allah, to His Book, to His Messenger, and to the leaders of the Muslims and their common folk.'",
            urduTranslation = "نبی کریم صلی اللہ علیہ وسلم نے فرمایا: دین سراسر خیر خواہی اور اخلاص کا نام ہے۔ ہم نے عرض کیا: کس کے لیے؟ آپ نے فرمایا: اللہ کے لیے، اس کی کتاب کے لیے، اس کے رسول کے لیے، اور مسلمانوں کے ائمہ و حکمرانوں اور عام مسلمانوں کے لیے۔",
            hindiTranslation = "नबी करीम (ﷺ) ने फरमाया: दीन भलाई और खुलूस (ईमानदारी) का नाम है। हमने पूछा: किसके लिए? फरमाया: अल्लाह के लिए, उसकी किताब के लिए, उसके रसूल के लिए, और मुसलमानों के हाकिमों और आम मुसलमानों के लिए।",
            grade = "Sahih (صحيح)",
            reference = "Sahih Muslim 55 • 40 Hadith Nawawi #7"
        ),
        HadithItem(
            id = 15,
            bookId = "nawawi40",
            bookName = "Forty Hadith of Imam Nawawi",
            chapterId = 15,
            chapterName = "Mindfulness & Good Character",
            hadithNumber = "Hadith 18",
            narrator = "Narrated Abu Dharr and Mu'adh bin Jabal (رضي الله عنهما)",
            arabicText = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Fear Allah wherever you may be; follow up a bad deed with a good deed which will wipe it out; and behave toward people with good character.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: تم جہاں کہیں بھی ہو اللہ سے ڈرو، اور برائی کے بعد نیکی کرو وہ اسے مٹا دے گی، اور لوگوں کے ساتھ حسن اخلاق سے پیش آؤ۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: तुम जहाँ कहीं भी रहो अल्लाह से डरो, और बुराई के बाद नेकी करो वह उसे मिटा देगी, और लोगों के साथ अच्छे अखलाक (सद्व्यवहार) से पेश आओ।",
            grade = "Hasan (حسن)",
            reference = "Jami` at-Tirmidhi 1987 • 40 Hadith Nawawi #18"
        ),

        // Riyad as-Salihin
        HadithItem(
            id = 16,
            bookId = "riyad",
            bookName = "Riyad as-Salihin",
            chapterId = 16,
            chapterName = "Patience & Gratitude (As-Sabr wash-Shukr)",
            hadithNumber = "Hadith 27",
            narrator = "Narrated Suhaib (رضي الله عنه)",
            arabicText = "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'How wonderful is the affair of the believer, for his affairs are all good, and this is for no one except a believer: If something good happens to him, he is thankful and that is good for him; and if something harmful touches him, he is patient and that is good for him.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: مومن کا معاملہ بھی کتنا عجیب و غریب ہے! اس کا ہر معاملہ اس کے لیے خیر ہی خیر ہے، اور یہ فضیلت مومن کے سوا کسی کو حاصل نہیں: اگر اسے کوئی خوشی پہنچے تو وہ شکر ادا کرتا ہے تو یہ اس کے لیے خیر ہوتا ہے، اور اگر اسے کوئی تکلیف پہنچے تو وہ صبر کرتا ہے تو یہ بھی اس کے لیے خیر ہوتا ہے۔",
            hindiTranslation = "रसूलुल्लाह (ﷺ) ने फरमाया: मोमिन का मामला भी कितना अजीब है! उसका हर मामला उसके लिए भलाई ही भलाई है, और यह बात मोमिन के अलावा किसी को हासिल नहीं: अगर उसे कोई खुशी पहुँचे तो वह शुक्र अदा करता है तो यह उसके लिए भलाई है, और अगर कोई परेशानी पहुँचे तो वह सब्र करता है तो यह भी उसके लिए भलाई है।",
            grade = "Sahih (صحيح)",
            reference = "Sahih Muslim 2999 • Riyad as-Salihin #27"
        )
    )

    fun getChaptersForBook(bookId: String): List<HadithChapter> {
        return CHAPTERS.filter { it.bookId == bookId }
    }

    fun getHadithsForChapter(chapterId: Int): List<HadithItem> {
        return ALL_HADITHS.filter { it.chapterId == chapterId }
    }

    fun getHadithsForBook(bookId: String): List<HadithItem> {
        return ALL_HADITHS.filter { it.bookId == bookId }
    }
}
