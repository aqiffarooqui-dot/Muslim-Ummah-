package com.example.data.ai

import com.example.data.model.Ayah
import com.example.data.model.HadithItem
import com.example.data.model.HadithRepository
import com.example.data.model.QuranRepository
import com.example.data.model.Surah

enum class AiMode(val title: String, val description: String, val iconBadge: String) {
    GENERAL("General Q&A", "Verified answers with Quran & Hadith citations", "💬"),
    QURAN("Quran Mode", "Direct tafsir insights & Surah contextual references", "📖"),
    HADITH("Hadith Mode", "Prophetic sunnah traditions & scholarly grading", "📜"),
    STUDY("Study Mode", "Step-by-step concepts with classical jurisprudence", "🎓"),
    PERSONAL_SEARCH("Personal Search", "Semantic search across Quran, translations & notes", "🔍")
}

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "AI"
    val text: String,
    val mode: AiMode = AiMode.GENERAL,
    val quranCitations: List<String> = emptyList(),
    val hadithCitations: List<String> = emptyList(),
    val scholarlyOpinions: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Authentic, Source-Aware Islamic AI Retrieval & Generation Engine.
 * 
 * Architecture:
 * Question -> Relevant Quran & Hadith Retrieval -> Grounding Context -> Answer Generation -> Exact Citations
 * 
 * Strict Rules:
 * 1. Citations must be authentic and identify Surah, Ayah, and Para/Juz for Quran.
 * 2. Citations must identify Collection, Chapter, Hadith number/ID, and Grade for Hadith.
 * 3. Never fabricate or hallucinate citations. If no reliable evidence is found, explicitly say so.
 */
object MuslimUmmahAiEngine {

    val QUICK_PROMPTS = listOf(
        "What is the step-by-step method of Witr prayer?",
        "How is Zakat calculated on gold and savings?",
        "What are the best virtues and times for Tahajjud prayer?",
        "Explain the conditions and nullifiers of Wudu",
        "What does the Quran say about patience in hardship?",
        "What are the rights of neighbors in authentic Hadith?",
        "What is the procedure of Salat al-Istikhara?"
    )

    private val VERIFIED_TOPICS = mapOf(
        "witr" to AiMessage(
            sender = "AI",
            text = "The Witr prayer is a highly emphasized Sunnah (Sunnah Mu'akkadah in the Shafi'i, Maliki, and Hanbali schools; Wajib according to Imam Abu Hanifah). It is offered after the Isha prayer and concludes the night worship before the dawn of Fajr.",
            quranCitations = listOf(
                "Surah Al-Muzzammil (Surah 73, Ayah 20 • Juz 29): 'Indeed, your Lord knows that you stand [in prayer] almost two thirds of the night or half of it or a third of it...'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 998 (Chapter: Witr Prayer • Grade: Sahih): 'Make Witr as your last prayer at night.'",
                "Sunan Abi Dawud • Hadith 1418 (Chapter: Salat al-Witr • Grade: Sahih): 'Witr is a duty upon every Muslim; whoever wishes to pray five rak'ahs let him do so, whoever wishes to pray three let him do so, and whoever wishes to pray one let him do so.'",
                "Jami` at-Tirmidhi • Hadith 464 (Chapter: Virtues of Witr • Grade: Hasan): 'Allah is Witr (One) and loves the Witr, so pray Witr, O people of the Quran.'"
            ),
            scholarlyOpinions = "• Hanafi School: Witr is Wajib (3 rak'ahs prayed continuously like Maghrib, with Takbeer and Dua Qunut before Ruku in the 3rd rak'ah).\n• Shafi'i & Hanbali Schools: Sunnah Mu'akkadah (often prayed as 2 rak'ahs with Tasleem, followed by 1 separate final rak'ah, with Qunut after Ruku)."
        ),
        "zakat" to AiMessage(
            sender = "AI",
            text = "Zakat is the third pillar of Islam, an obligatory annual purification due on wealth that has reached the minimum threshold (Nisab) and been in one's possession for a full lunar year (Hawl). The rate on cash, gold, and trade goods is 2.5% (one fortieth).",
            quranCitations = listOf(
                "Surah Al-Baqarah (Surah 2, Ayah 43 • Juz 1): 'And establish prayer and give Zakat and bow with those who bow [in worship and obedience].'",
                "Surah At-Tawbah (Surah 9, Ayah 60 • Juz 10): 'Zakah expenditures are only for the poor and for the needy and for those employed to collect [it] and for bringing hearts together... an obligation [imposed] by Allah.'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 1395 & Sahih Muslim • Hadith 19 (Chapter: Obligation of Zakat • Grade: Sahih): 'Inform them that Allah has enjoined upon them charity to be taken from their rich and given to their poor.'",
                "Sunan Abi Dawud • Hadith 1573 (Chapter: Zakat on Wealth • Grade: Sahih): 'When you possess 200 dirhams and a year has passed, 5 dirhams are due; and there is nothing upon gold until you possess 20 dinars (approx. 85 grams), where half a dinar is due.'"
            ),
            scholarlyOpinions = "• Nisab standard: Silver standard (595g of silver) is widely recommended for cash savings to maximize benefit for the poor, whereas gold standard (85g) is also applied.\n• Recipient eligibility: Must strictly fall within the 8 categories specified in Surah At-Tawbah 9:60."
        ),
        "tahajjud" to AiMessage(
            sender = "AI",
            text = "Tahajjud (Qiyam al-Layl) is the voluntary night vigil prayer offered after awakening from sleep during the last third of the night. It is among the highest spiritual acts bringing closeness to Allah, forgiveness, and illumination of the heart.",
            quranCitations = listOf(
                "Surah Al-Isra (Surah 17, Ayah 79 • Juz 15): 'And from [part of] the night, pray with it as additional [worship] for you; it is expected that your Lord will resurrect you to a praised station.'",
                "Surah As-Sajdah (Surah 32, Ayah 16 • Juz 21): 'They forsake their beds, calling upon their Lord in fear and hope, and they spend out of what We have provided them.'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 1145 & Sahih Muslim • Hadith 758 (Chapter: Night Prayer • Grade: Sahih): 'Our Lord descends every night to the lowest heaven when the last third of the night remains and says: Who is calling upon Me that I may answer him? Who is asking of Me that I may give him? Who is seeking My forgiveness that I may forgive him?'",
                "Sahih Muslim • Hadith 1163 (Chapter: Fasting & Night Prayer • Grade: Sahih): 'The best prayer after the obligatory prayers is prayer in the middle of the night.'"
            ),
            scholarlyOpinions = "• Recommended Rak'ahs: The Prophet (ﷺ) typically prayed 8 rak'ahs in sets of two, followed by 3 rak'ahs of Witr (Total 11).\n• Timing: The last third of the night is the most meritorious time."
        ),
        "patience" to AiMessage(
            sender = "AI",
            text = "Patience (Sabr) in Islam comprises steadfastness in obeying Allah, restraint from sins and desires, and content acceptance of Allah's divine decree (Qadr) during tribulations.",
            quranCitations = listOf(
                "Surah Al-Baqarah (Surah 2, Ayah 153 • Juz 2): 'O you who have believed, seek help through patience and prayer. Indeed, Allah is with the patient.'",
                "Surah Az-Zumar (Surah 39, Ayah 10 • Juz 23): 'Indeed, the patient will be given their reward without account.'"
            ),
            hadithCitations = listOf(
                "Sahih Muslim • Hadith 2999 (Chapter: Affairs of the Believer • Grade: Sahih): 'How wonderful is the affair of the believer, for his affairs are all good... If something harmful touches him, he is patient and that is good for him.'",
                "Sahih al-Bukhari • Hadith 1302 (Chapter: Patience in Bereavement • Grade: Sahih): 'Verily, patience is at the first stroke of a calamity.'"
            ),
            scholarlyOpinions = "• Classical scholars (Ibn al-Qayyim, Imam al-Ghazali) categorize Sabr into three pillars: withholding the tongue from complaining, withholding the limbs from transgression, and keeping the heart free from resentment toward Allah."
        ),
        "neighbor" to AiMessage(
            sender = "AI",
            text = "Islam places profound emphasis on treating neighbors with kindness, respect, safety, and generosity, regardless of their faith or background.",
            quranCitations = listOf(
                "Surah An-Nisa (Surah 4, Ayah 36 • Juz 5): 'Worship Allah and associate nothing with Him, and to parents do good, and to relatives, orphans, the needy, the near neighbor, the neighbor farther away...'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 6014 & Sahih Muslim • Hadith 2624 (Chapter: Good Manners • Grade: Sahih): 'Jibreel continued to recommend me to treat neighbors kindly until I thought he would make them my heirs.'",
                "Sahih al-Bukhari • Hadith 6016 (Chapter: Sins of Harming Neighbors • Grade: Sahih): 'By Allah, he does not believe! They asked: Who, O Messenger of Allah? He said: The one whose neighbor is not safe from his harm.'"
            ),
            scholarlyOpinions = "• Jurists define the boundary of the neighborhood to include up to forty houses in each direction.\n• Rights include sharing food, visiting when sick, guarding their property, and maintaining absolute peace."
        ),
        "istikhara" to AiMessage(
            sender = "AI",
            text = "Salat al-Istikhara is the prayer for seeking Allah's guidance when making a significant decision (marriage, career, relocation, business). It consists of two voluntary (Nafl) rak'ahs followed by the specific supplication taught by the Prophet (ﷺ).",
            quranCitations = listOf(
                "Surah Ali 'Imran (Surah 3, Ayah 159 • Juz 4): 'And consult them in the matter. And when you have decided, then rely upon Allah; indeed, Allah loves those who rely [upon Him].'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 1162 (Chapter: Tahajjud & Istikhara • Grade: Sahih): 'The Messenger of Allah (ﷺ) used to teach us Istikhara in all matters just as he would teach us a Surah from the Quran...'"
            ),
            scholarlyOpinions = "• Common misconception: A dream is not required after Istikhara. Rather, Allah facilitates and eases that which is good and turns the believer away from what is harmful."
        ),
        "wudu" to AiMessage(
            sender = "AI",
            text = "Wudu (ritual ablution) is the physical and spiritual purification required before performing Salah, touching the Mushaf, or performing Tawaf around the Kaaba.",
            quranCitations = listOf(
                "Surah Al-Ma'idah (Surah 5, Ayah 6 • Juz 6): 'O you who have believed, when you rise to [perform] prayer, wash your faces and your forearms to the elbows and wipe over your heads and wash your feet to the ankles...'"
            ),
            hadithCitations = listOf(
                "Sahih al-Bukhari • Hadith 135 & Sahih Muslim • Hadith 225 (Chapter: Purification • Grade: Sahih): 'The prayer of none of you will be accepted if he relieves himself, until he performs ablution (wudu).'",
                "Sahih Muslim • Hadith 223 (Chapter: Virtues of Wudu • Grade: Sahih): 'Purity is half of faith.'"
            ),
            scholarlyOpinions = "• Four Obligatory (Fard) acts: Washing face, washing arms up to elbows, wiping over head (Masah), washing feet up to ankles.\n• Nullifiers include bodily emissions, deep sleep, loss of consciousness, and direct intimate contact."
        )
    )

    fun answerQuery(query: String, mode: AiMode): AiMessage {
        val q = query.trim().lowercase()

        // 1. Preset topic detection is not source verification. Do not return
        // hardcoded religious advice when its citations have not been validated.
        val matchedKey = VERIFIED_TOPICS.keys.firstOrNull { q.contains(it) }
        if (matchedKey != null) {
            return AiMessage(
                sender = "AI",
                text = "I recognized the topic \"$matchedKey\", but its saved notes are not currently verified against a complete trusted source corpus. To avoid giving you an unsupported religious answer, I am not presenting those notes as guidance. Please verify the relevant Quran ayah or original Hadith reference, or consult a qualified scholar.",
                mode = mode,
                quranCitations = emptyList(),
                hadithCitations = emptyList(),
                scholarlyOpinions = "No verified source-grounded answer was generated for this topic."
            )
        }

        // 2. RETRIEVAL STEP: Search Quran dataset
        val matchingSurah = QuranRepository.ALL_SURAHS.find {
            q.contains(it.nameEnglish.lowercase()) ||
            q.contains(it.englishMeaning.lowercase()) ||
            q.contains(it.nameArabic) ||
            q.contains("surah ${it.number}") ||
            q.contains("chapter ${it.number}")
        }

        // Search Hadith dataset
        val matchingHadiths = HadithRepository.ALL_HADITHS.filter { hadith ->
            q.contains(hadith.bookName.lowercase()) ||
            q.contains(hadith.chapterName.lowercase()) ||
            hadith.englishTranslation.lowercase().contains(q) ||
            hadith.urduTranslation.contains(q) ||
            hadith.hindiTranslation.contains(q) ||
            hadith.hinglishTranslation.lowercase().contains(q) ||
            (q.length > 4 && hadith.chapterName.lowercase().split(" ").any { word -> word.length > 4 && q.contains(word) })
        }

        // Case A: Found matching Surah metadata. Never claim a full/verified Surah
        // when the local dataset contains only selected excerpts.
        if (matchingSurah != null) {
            val verses = QuranRepository.getVersesForSurah(matchingSurah.number)
            val versePreview = verses.take(3).map {
                "Surah ${matchingSurah.nameEnglish} (Surah ${matchingSurah.number}, Ayah ${it.ayahNumber} • Juz ${matchingSurah.juz}): ${it.textArabic} — \"${it.textEnglish}\""
            }

            return AiMessage(
                sender = "AI",
                text = if (versePreview.isEmpty()) {
                    "Surah ${matchingSurah.nameEnglish} is Surah #${matchingSurah.number} and has ${matchingSurah.totalAyahs} Ayahs. The complete verse text is not available in the current local dataset, so I will not generate or substitute verses."
                } else {
                    "Surah ${matchingSurah.nameEnglish} is Surah #${matchingSurah.number} and has ${matchingSurah.totalAyahs} Ayahs. The excerpts below are only the verses currently present in the local dataset; this is not a complete Surah."
                },
                mode = mode,
                quranCitations = versePreview,
                hadithCitations = emptyList(),
                scholarlyOpinions = "The local Quran corpus is incomplete. Verify the complete Arabic text and translation against a trusted Mushaf before using this as a religious reference."
            )
        }

        // Case B: Found matching Hadiths
        if (matchingHadiths.isNotEmpty()) {
            val topHadith = matchingHadiths.first()
            val hadithCitations = matchingHadiths.filter { h ->
                h.grade != "Unverified" && h.reference != "Reference not yet verified"
            }.take(2).map { h ->
                "${h.bookName} • ${h.hadithNumber} (Chapter: ${h.chapterName} • Grade: ${h.grade}; Reference: ${h.reference}): \"${h.englishTranslation}\""
            }

            return AiMessage(
                sender = "AI",
                text = "A local Hadith record matching your query was found in ${topHadith.bookName}. The local dataset is not yet complete, and this record's source/grade must be verified before treating it as an authenticated quotation.\n\n${topHadith.englishTranslation}\n\nاردو: ${topHadith.urduTranslation}\nहिंदी: ${topHadith.hindiTranslation}\nHinglish: ${topHadith.hinglishTranslation}",
                mode = mode,
                quranCitations = emptyList(),
                hadithCitations = hadithCitations,
                scholarlyOpinions = "Narrator listed locally: ${topHadith.narrator}. Local reference status: ${topHadith.reference}. Please verify the original collection and numbering."
            )
        }

        // Case C: Personal Search Mode. Never imply that the partial local corpus or
        // user bookmarks were searched unless an actual index is connected.
        if (mode == AiMode.PERSONAL_SEARCH) {
            return AiMessage(
                sender = "AI",
                text = "No verified local text match was found for \"$query\". The Quran and Hadith corpus available offline is incomplete, and this screen does not currently search a complete online corpus or personal bookmarks. No citation is being supplied because a matching source could not be verified.",
                mode = mode,
                quranCitations = emptyList(),
                hadithCitations = emptyList(),
                scholarlyOpinions = "Search limitation: only locally stored excerpts can be searched. Verify the topic against a trusted Mushaf or the original Hadith collection."
            )
        }

        // Case D: General Islamic concepts without a verified retrieval match.
        // Do not attach generic citations that may not support the user's question.
        val generalIslamicKeywords = listOf("salah", "namaz", "dua", "allah", "quran", "sunnah", "prophet", "fasting", "ramadan", "hajj", "islam", "jannah", "iman", "taqwa")
        if (generalIslamicKeywords.any { q.contains(it) }) {
            return AiMessage(
                sender = "AI",
                text = "I understand this is an Islamic question about \"$query\". I could not retrieve enough verified source material from the current local corpus to answer it reliably, so I will not generate a ruling or attach unrelated citations. Please check a trusted primary source or consult a qualified scholar for a specific ruling.",
                mode = mode,
                quranCitations = emptyList(),
                hadithCitations = emptyList(),
                scholarlyOpinions = "The local Quran and Hadith datasets are incomplete. No source-grounded conclusion was generated."
            )
        }

        // Case E: Out of corpus / non-Islamic query - HONEST, NON-FABRICATED RESPONSE
        return AiMessage(
            sender = "AI",
            text = "No direct verified textual evidence was found in the indexed Quran and Hadith corpus for \"$query\". As an authentic Islamic assistant, we do not fabricate sources, traditions, or religious rulings. For specific personal fatawa or complex jurisprudence inquiries, please consult a qualified Islamic scholar or local mufti.",
            mode = mode,
            quranCitations = emptyList(), // Strict: No fabricated citations!
            hadithCitations = emptyList(),
            scholarlyOpinions = "Islamic scholarship strictly prohibits speaking about religion without verified authentic textual knowledge."
        )
    }
}
