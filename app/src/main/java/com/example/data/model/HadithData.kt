package com.example.data.model

data class HadithBook(
    val id: String,
    val nameEnglish: String,
    val nameArabic: String,
    val author: String,
    val hadithCountApprox: String,
    val description: String
)

data class HadithItem(
    val id: Int,
    val bookId: String,
    val bookName: String,
    val chapterName: String,
    val hadithNumber: String,
    val narrator: String,
    val arabicText: String,
    val englishTranslation: String,
    val urduTranslation: String,
    val grade: String = "Sahih (صحيح)"
)

object HadithRepository {

    val MAIN_BOOKS = listOf(
        HadithBook(
            id = "bukhari",
            nameEnglish = "Sahih al-Bukhari",
            nameArabic = "صحيح البخاري",
            author = "Imam Muhammad al-Bukhari (rahimahullah)",
            hadithCountApprox = "7,563 Hadiths",
            description = "The most authentic book of Hadith in Islam, universally accepted by the Ummah."
        ),
        HadithBook(
            id = "muslim",
            nameEnglish = "Sahih Muslim",
            nameArabic = "صحيح مسلم",
            author = "Imam Muslim ibn al-Hajjaj (rahimahullah)",
            hadithCountApprox = "7,500 Hadiths",
            description = "Second only to Sahih al-Bukhari in authority and rigorous authentication."
        ),
        HadithBook(
            id = "tirmidhi",
            nameEnglish = "Jami` at-Tirmidhi",
            nameArabic = "جامع الترمذي",
            author = "Imam Abu 'Isa at-Tirmidhi (rahimahullah)",
            hadithCountApprox = "3,956 Hadiths",
            description = "Distinguished by commentary on legal rulings and hadith gradings."
        ),
        HadithBook(
            id = "abudawud",
            nameEnglish = "Sunan Abi Dawud",
            nameArabic = "سنن أبي داود",
            author = "Imam Abu Dawud as-Sijistani (rahimahullah)",
            hadithCountApprox = "5,274 Hadiths",
            description = "Renowned for focus on Ahkam (legal rulings and Islamic jurisprudence)."
        ),
        HadithBook(
            id = "nasai",
            nameEnglish = "Sunan an-Nasa'i",
            nameArabic = "سنن النسائي",
            author = "Imam Ahmad an-Nasa'i (rahimahullah)",
            hadithCountApprox = "5,760 Hadiths",
            description = "Famed for the compiler's strict criteria in chain verification."
        ),
        HadithBook(
            id = "ibnmajah",
            nameEnglish = "Sunan Ibn Majah",
            nameArabic = "سنن ابن ماجه",
            author = "Imam Ibn Majah al-Qazwini (rahimahullah)",
            hadithCountApprox = "4,341 Hadiths",
            description = "The sixth book of the canonical Kutub al-Sittah."
        ),
        HadithBook(
            id = "nawawi40",
            nameEnglish = "Forty Hadith of Imam Nawawi",
            nameArabic = "الأربعون النووية",
            author = "Imam Yahya ibn Sharaf an-Nawawi (rahimahullah)",
            hadithCountApprox = "42 Hadiths",
            description = "The most famous compilation encapsulating the essential foundations of Islam."
        ),
        HadithBook(
            id = "riyad",
            nameEnglish = "Riyad as-Salihin",
            nameArabic = "رياض الصالحين",
            author = "Imam Yahya ibn Sharaf an-Nawawi (rahimahullah)",
            hadithCountApprox = "1,896 Hadiths",
            description = "The Meadows of the Righteous; essential moral, ethical, and spiritual guidance."
        )
    )

    val ALL_HADITHS = listOf(
        // Bukhari
        HadithItem(
            id = 1,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterName = "Revelation (بدء الوحي)",
            hadithNumber = "Hadith 1",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            englishTranslation = "I heard the Messenger of Allah (ﷺ) say: 'The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اعمال کا دارومدار نیتوں پر ہے، اور ہر شخص کے لیے وہی ہے جس کی اس نے نیت کی۔ پس جس کی ہجرت دنیا کمانے کے لیے ہو یا کسی عورت سے نکاح کے لیے، تو اس کی ہجرت اسی کے لیے ہے جس کی طرف اس نے ہجرت کی۔"
        ),
        HadithItem(
            id = 2,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterName = "Belief / Faith (كتاب الإيمان)",
            hadithNumber = "Hadith 13",
            narrator = "Narrated Anas bin Malik (رضي الله عنه)",
            arabicText = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            englishTranslation = "The Prophet (ﷺ) said: 'None of you truly believes until he loves for his brother (or sister) what he loves for himself.'",
            urduTranslation = "نبی کریم صلی اللہ علیہ وسلم نے فرمایا: تم میں سے کوئی شخص اس وقت تک سچا مومن نہیں ہو سکتا جب تک کہ وہ اپنے بھائی کے لیے وہی پسند نہ کرے جو اپنے لیے پسند کرتا ہے۔"
        ),
        HadithItem(
            id = 3,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterName = "Virtues of the Quran (فضائل القرآن)",
            hadithNumber = "Hadith 5027",
            narrator = "Narrated 'Uthman bin 'Affan (رضي الله عنه)",
            arabicText = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            englishTranslation = "The Prophet (ﷺ) said: 'The best among you are those who learn the Quran and teach it to others.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: تم میں سے بہترین وہ شخص ہے جو قرآن سیکھے اور اسے دوسروں کو سکھائے۔"
        ),
        HadithItem(
            id = 4,
            bookId = "bukhari",
            bookName = "Sahih al-Bukhari",
            chapterName = "Good Manners (كتاب الأدب)",
            hadithNumber = "Hadith 6094",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ جَارَهُ، وَمَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيُكْرِمْ ضَيْفَهُ",
            englishTranslation = "The Prophet (ﷺ) said: 'Whoever believes in Allah and the Last Day should speak good or remain silent; and whoever believes in Allah and the Last Day should be generous to his neighbor; and whoever believes in Allah and the Last Day should be hospitable to his guest.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو شخص اللہ اور آخرت کے دن پر ایمان رکھتا ہو اسے چاہیے کہ وہ بھلی بات کہے یا خاموش رہے، اور جو اللہ اور یوم آخرت پر ایمان رکھتا ہو وہ اپنے پڑوسی کی عزت کرے، اور جو اللہ اور یوم آخرت پر ایمان رکھتا ہو وہ اپنے مہمان کی مہمان نوازی کرے۔"
        ),

        // Muslim
        HadithItem(
            id = 5,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterName = "Faith / The Hadith of Jibreel (كتاب الإيمان)",
            hadithNumber = "Hadith 8",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "الإِسْلاَمُ أَنْ تَشْهَدَ أَنْ لاَ إِلَهَ إِلاَّ اللَّهُ وَأَنَّ مُحَمَّدًا رَسُولُ اللَّهِ، وَتُقِيمَ الصَّلاَةَ، وَتُؤْتِيَ الزَّكَاةَ، وَتَصُومَ رَمَضَانَ، وَتَحُجَّ الْبَيْتَ إِنِ اسْتَطَعْتَ إِلَيْهِ سَبِيلاً",
            englishTranslation = "The Messenger of Allah (ﷺ) said to Jibreel: 'Islam is that you testify there is no god worthy of worship except Allah and that Muhammad is the Messenger of Allah, establish Salah, pay Zakah, fast during Ramadan, and perform pilgrimage to the House if you are able to do so.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اسلام یہ ہے کہ تم گواہی دو کہ اللہ کے سوا کوئی معبود برحق نہیں اور محمد صلی اللہ علیہ وسلم اللہ کے رسول ہیں، اور نماز قائم کرو، زکوٰۃ ادا کرو، رمضان کے روزے رکھو، اور اگر استطاعت ہو تو بیت اللہ کا حج کرو۔"
        ),
        HadithItem(
            id = 6,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterName = "Purification (كتاب الطهارة)",
            hadithNumber = "Hadith 223",
            narrator = "Narrated Abu Malik al-Ash'ari (رضي الله عنه)",
            arabicText = "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ، وَسُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ تَمْلآنِ أَوْ تَمْلأُ مَا بَيْنَ السَّمَاوَاتِ وَالأَرْضِ، وَالصَّلاَةُ نُورٌ، وَالصَّدَقَةُ بُرْهَانٌ، وَالصَّبْرُ ضِيَاءٌ، وَالْقُرْآنُ حُجَّةٌ لَكَ أَوْ عَلَيْكَ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Purity is half of faith. Alhamdulillah fills the Scale. SubhanAllah and Alhamdulillah fill what is between the heavens and the earth. Prayer is light, charity is proof, patience is illumination, and the Quran is an argument for you or against you.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: پاکیزگی نصف ایمان ہے، اور الحمد للہ میزان کو بھر دیتا ہے، اور سبحان اللہ اور الحمد للہ آسمانوں اور زمین کے درمیانی خلا کو بھر دیتے ہیں، اور نماز نور ہے، صدقہ دلیل ہے، صبر روشنی ہے، اور قرآن تمہارے حق میں یا تمہارے خلاف حجت ہے۔"
        ),
        HadithItem(
            id = 7,
            bookId = "muslim",
            bookName = "Sahih Muslim",
            chapterName = "Supplication & Dhikr (كتاب الذكر والدعاء)",
            hadithNumber = "Hadith 2699",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ، وَمَا اجْتَمَعَ قَوْمٌ فِي بَيْتٍ مِنْ بُيُوتِ اللَّهِ يَتْلُونَ كِتَابَ اللَّهِ وَيَتَدَارَسُونَهُ بَيْنَهُمْ إِلاَّ نَزَلَتْ عَلَيْهِمُ السَّكِينَةُ وَغَشِيَتْهُمُ الرَّحْمَةُ وَحَفَّتْهُمُ الْمَلاَئِكَةُ وَذَكَرَهُمُ اللَّهُ فِيمَنْ عِنْدَهُ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Whoever treads a path seeking knowledge, Allah makes easy for him a path to Paradise. No people gather together in one of the houses of Allah, reciting the Book of Allah and studying it between themselves, but tranquility descends upon them, mercy covers them, the angels surround them, and Allah mentions them to those near Him.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو شخص علم کی تلاش میں کسی راستے پر چلے، اللہ اس کے لیے جنت کا راستہ آسان فرما دیتا ہے۔ اور جب بھی کچھ لوگ اللہ کے گھروں میں سے کسی گھر میں جمع ہو کر اللہ کی کتاب کی تلاوت کرتے ہیں اور اسے آپس میں پڑھتے پڑھاتے ہیں، تو ان پر سکینت نازل ہوتی ہے، رحمت انہیں ڈھانپ لیتی ہے، فرشتے انہیں گھیر لیتے ہیں اور اللہ اپنے پاس موجود فرشتوں میں ان کا ذکر فرماتا ہے۔"
        ),

        // Tirmidhi
        HadithItem(
            id = 8,
            bookId = "tirmidhi",
            bookName = "Jami` at-Tirmidhi",
            chapterName = "Righteousness & Good Character (كتاب البر والصلة)",
            hadithNumber = "Hadith 1956",
            narrator = "Narrated Abu Dharr (رضي الله عنه)",
            arabicText = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Your smiling in the face of your brother is charity for you.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اپنے بھائی کے چہرے پر تمہارا مسکرانا تمہارے لیے صدقہ ہے۔"
        ),
        HadithItem(
            id = 9,
            bookId = "tirmidhi",
            bookName = "Jami` at-Tirmidhi",
            chapterName = "Trust in Allah (كتاب الزهد)",
            hadithNumber = "Hadith 2344",
            narrator = "Narrated 'Umar bin Al-Khattab (رضي الله عنه)",
            arabicText = "لَوْ أَنَّكُمْ تَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
            englishTranslation = "The Prophet (ﷺ) said: 'If you were to rely upon Allah with the required reliance, He would provide for you just as He provides for the birds: they go out in the morning with empty stomachs and return in the evening full.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اگر تم اللہ پر ویسا توکل کرو جیسا کہ اس پر توکل کرنے کا حق ہے، تو وہ تمہیں اسی طرح رزق دے گا جس طرح پرندوں کو رزق دیتا ہے؛ وہ صبح خالی پیٹ نکلتے ہیں اور شام کو پیٹ بھر کر لوٹتے ہیں۔"
        ),

        // Abu Dawud
        HadithItem(
            id = 10,
            bookId = "abudawud",
            bookName = "Sunan Abi Dawud",
            chapterName = "Prayer (كتاب الصلاة)",
            hadithNumber = "Hadith 495",
            narrator = "Narrated 'Amr bin Shu'aib from his father from his grandfather (رضي الله عنهم)",
            arabicText = "مُرُوا أَوْلاَدَكُمْ بِالصَّلاَةِ وَهُمْ أَبْنَاءُ سَبْعِ سِنِينَ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Enjoin your children to pray when they are seven years old.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: اپنی اولاد کو نماز کا حکم دو جب وہ سات سال کے ہو جائیں۔"
        ),
        HadithItem(
            id = 11,
            bookId = "abudawud",
            bookName = "Sunan Abi Dawud",
            chapterName = "Good Manners (كتاب الأدب)",
            hadithNumber = "Hadith 4799",
            narrator = "Narrated Abu Hurairah (رضي الله عنه)",
            arabicText = "لاَ يَشْكُرُ اللَّهَ مَنْ لاَ يَشْكُرُ النَّاسَ",
            englishTranslation = "The Prophet (ﷺ) said: 'He who does not thank people does not thank Allah.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: جو انسانوں کا شکر ادا نہیں کرتا، وہ اللہ کا شکر بھی ادا نہیں کرتا۔"
        ),

        // An-Nasa'i
        HadithItem(
            id = 12,
            bookId = "nasai",
            bookName = "Sunan an-Nasa'i",
            chapterName = "Jihad / Dutifulness to Parents (كتاب الجهاد)",
            hadithNumber = "Hadith 3104",
            narrator = "Narrated Mu'awiyah bin Jahimah (رضي الله عنه)",
            arabicText = "الْزَمْ رِجْلَهَا فَثَمَّ الْجَنَّةُ",
            englishTranslation = "A man asked the Prophet (ﷺ) about going on expedition, and the Prophet asked: 'Is your mother alive?' He said: 'Yes.' The Prophet (ﷺ) said: 'Stay by her feet, for Paradise is there.'",
            urduTranslation = "ایک صحابی نے رسول اللہ صلی اللہ علیہ وسلم سے جہاد میں شرکت کی اجازت چاہی تو آپ ﷺ نے پوچھا: کیا تمہاری والدہ حیات ہیں؟ انہوں نے عرض کیا: جی ہاں۔ آپ ﷺ نے فرمایا: ان کی خدمت لازم پکڑ لو، کیونکہ جنت ان کے قدموں تلے ہے۔"
        ),

        // Ibn Majah
        HadithItem(
            id = 13,
            bookId = "ibnmajah",
            bookName = "Sunan Ibn Majah",
            chapterName = "Introduction to Sunnah (المقدمة)",
            hadithNumber = "Hadith 224",
            narrator = "Narrated Anas bin Malik (رضي الله عنه)",
            arabicText = "طَلَبُ الْعِلْمِ فَرِيضَةٌ عَلَى كُلِّ مُسْلِمٍ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Seeking knowledge is an obligation upon every Muslim.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: علم حاصل کرنا ہر مسلمان پر فرض ہے۔"
        ),

        // Nawawi's 40 Hadith
        HadithItem(
            id = 14,
            bookId = "nawawi40",
            bookName = "Forty Hadith of Imam Nawawi",
            chapterName = "Sincerity / An-Nasihah",
            hadithNumber = "Hadith 7",
            narrator = "Narrated Tamim ad-Dari (رضي الله عنه)",
            arabicText = "الدِّينُ النَّصِيحَةُ، قُلْنَا: لِمَنْ؟ قَالَ: لِلَّهِ وَلِكِتَابِهِ وَلِرَسُولِهِ وَلأَئِمَّةِ الْمُسْلِمِينَ وَعَامَّتِهِمْ",
            englishTranslation = "The Prophet (ﷺ) said: 'Religion is sincerity.' We said: 'To whom?' He said: 'To Allah, to His Book, to His Messenger, and to the leaders of the Muslims and their common folk.'",
            urduTranslation = "نبی کریم صلی اللہ علیہ وسلم نے فرمایا: دین سراسر خیر خواہی اور اخلاص کا نام ہے۔ ہم نے عرض کیا: کس کے لیے؟ آپ نے فرمایا: اللہ کے لیے، اس کی کتاب کے لیے، اس کے رسول کے لیے، اور مسلمانوں کے ائمہ و حکمرانوں اور عام مسلمانوں کے لیے۔"
        ),
        HadithItem(
            id = 15,
            bookId = "nawawi40",
            bookName = "Forty Hadith of Imam Nawawi",
            chapterName = "Mindfulness / Taqwa",
            hadithNumber = "Hadith 18",
            narrator = "Narrated Abu Dharr and Mu'adh bin Jabal (رضي الله عنهما)",
            arabicText = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا، وَخَالِقِ النَّاسَ بِخُلُقٍ حَسَنٍ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'Fear Allah wherever you may be; follow up a bad deed with a good deed which will wipe it out; and behave toward people with good character.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: تم جہاں کہیں بھی ہو اللہ سے ڈرو، اور برائی کے بعد نیکی کرو وہ اسے مٹا دے گی، اور لوگوں کے ساتھ حسن اخلاق سے پیش آؤ۔"
        ),

        // Riyad as-Salihin
        HadithItem(
            id = 16,
            bookId = "riyad",
            bookName = "Riyad as-Salihin",
            chapterName = "Patience (باب الصبر)",
            hadithNumber = "Hadith 27",
            narrator = "Narrated Suhaib (رضي الله عنه)",
            arabicText = "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، وَلَيْسَ ذَاكَ لأَحَدٍ إِلاَّ لِلْمُؤْمِنِ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
            englishTranslation = "The Messenger of Allah (ﷺ) said: 'How wonderful is the affair of the believer, for his affairs are all good, and this is for no one except a believer: If something good happens to him, he is thankful and that is good for him; and if something harmful touches him, he is patient and that is good for him.'",
            urduTranslation = "رسول اللہ صلی اللہ علیہ وسلم نے فرمایا: مومن کا معاملہ بھی کتنا عجیب و غریب ہے! اس کا ہر معاملہ اس کے لیے خیر ہی خیر ہے، اور یہ فضیلت مومن کے سوا کسی کو حاصل نہیں: اگر اسے کوئی خوشی پہنچے تو وہ شکر ادا کرتا ہے تو یہ اس کے لیے خیر ہوتا ہے، اور اگر اسے کوئی تکلیف پہنچے تو وہ صبر کرتا ہے تو یہ بھی اس کے لیے خیر ہوتا ہے۔"
        )
    )
}
