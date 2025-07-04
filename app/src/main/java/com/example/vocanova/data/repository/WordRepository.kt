package com.example.vocanova.data.repository

import com.example.vocanova.data.model.Word
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {

    private val wordsCollection = firestore.collection("words")
    private val userWordsCollection = firestore.collection("user_words")

    private val words = MutableStateFlow<List<Word>>(emptyList())
    private var lastDailyWordDate: String = ""
    private var currentDailyWord: Word? = null

    init {
        // Load comprehensive word list
        words.value = generateComprehensiveWordList()

        // Load words from Firestore (if available)
        loadWords()
    }

    private fun loadWords() {
        wordsCollection.get().addOnSuccessListener { snapshot ->
            val wordsList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Word::class.java)
            }
            if (wordsList.isNotEmpty()) {
                words.value = wordsList
            }
        }
    }

    fun getDailyWord(): Flow<Word> {
        return words.map { wordsList ->
            if (wordsList.isEmpty()) {
                generateComprehensiveWordList().first()
            } else {
                val today = getCurrentDateString()

                // If we already have a daily word for today, return it
                if (today == lastDailyWordDate && currentDailyWord != null) {
                    currentDailyWord!!
                } else {
                    // Otherwise, select a new daily word based on the date
                    val calendar = Calendar.getInstance()
                    val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
                    val wordIndex = dayOfYear % wordsList.size

                    // Update the current daily word
                    currentDailyWord = wordsList[wordIndex]
                    lastDailyWordDate = today

                    currentDailyWord!!
                }
            }
        }
    }

    private fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getSavedWords(): Flow<List<Word>> {
        val userId = authRepository.getCurrentUserId()
        if (userId != null) {
            userWordsCollection.document(userId).collection("saved_words").get()
                .addOnSuccessListener { snapshot ->
                    val savedWordIds = snapshot.documents.mapNotNull { it.id }

                    // Update the flow with saved words
                    val updatedWords = words.value.map { word ->
                        if (savedWordIds.contains(word.id.toString())) {
                            word.copy(isSaved = true)
                        } else {
                            word
                        }
                    }
                    words.value = updatedWords
                }
        }

        return words.map { wordList -> wordList.filter { it.isSaved } }
    }

    suspend fun saveWord(wordId: Int) {
        val userId = authRepository.getCurrentUserId() ?: return

        // Save to Firestore
        userWordsCollection.document(userId)
            .collection("saved_words")
            .document(wordId.toString())
            .set(mapOf("savedAt" to System.currentTimeMillis()))
            .await()

        // Update local state
        val updatedWords = words.value.map {
            if (it.id == wordId) it.copy(isSaved = true) else it
        }
        words.value = updatedWords
    }

    suspend fun removeWord(wordId: Int) {
        val userId = authRepository.getCurrentUserId() ?: return

        // Remove from Firestore
        userWordsCollection.document(userId)
            .collection("saved_words")
            .document(wordId.toString())
            .delete()
            .await()

        // Update local state
        val updatedWords = words.value.map {
            if (it.id == wordId) it.copy(isSaved = false) else it
        }
        words.value = updatedWords
    }

    private fun generateComprehensiveWordList(): List<Word> {
        return listOf(
            Word(
                id = 1,
                text = "Accurate",
                partOfSpeech = "adjective",
                pronunciation = "ˈa-kyə-rət",
                meaning = "Being in agreement with the truth or a fact or a standard",
                example = "An accurate count of the number of people coming to the wedding reception",
                synonyms = listOf("Correct", "Good", "True", "Precise"),
                antonyms = listOf("Wrong", "Incorrect", "Improper", "False")
            ),
            Word(
                id = 2,
                text = "Admire",
                partOfSpeech = "verb",
                pronunciation = "əd-ˈmī(-ə)r",
                meaning = "To think very highly or favorably of",
                example = "I admire the way you handled such a touchy situation",
                synonyms = listOf("Respect", "Regard", "Appreciate", "Praise"),
                antonyms = listOf("Hate", "Despise", "Detest", "Loathe")
            ),
            Word(
                id = 3,
                text = "Ancient",
                partOfSpeech = "adjective",
                pronunciation = "ˈān(t)-shənt",
                meaning = "Dating or surviving from the distant past",
                example = "Rome's ancient ruins remain carefully preserved even in the midst of the bustle of the modern city",
                synonyms = listOf("Venerable", "Old", "Archaic", "Antique"),
                antonyms = listOf("Modern", "Recent", "New", "Young")
            ),
            Word(
                id = 4,
                text = "Appropriate",
                partOfSpeech = "adjective",
                pronunciation = "ə-ˈprō-prē-ət",
                meaning = "Meeting the requirements of a purpose or situation",
                example = "I don't think jeans and a T-shirt are appropriate attire for a wedding",
                synonyms = listOf("Suitable", "Fitting", "Proper", "Good"),
                antonyms = listOf("Inappropriate", "Improper", "Unsuitable", "Unfit")
            ),
            Word(
                id = 5,
                text = "Arrive",
                partOfSpeech = "verb",
                pronunciation = "ə-ˈrīv",
                meaning = "To get to a destination",
                example = "When will the guests arrive?",
                synonyms = listOf("Seize", "Grab", "Convert", "Claim"),
                antonyms = listOf("Steal", "Lift", "Swipe", "Misappropriate")
            ),
            Word(
                id = 6,
                text = "Brief",
                partOfSpeech = "adjective",
                pronunciation = "ˈbrēf",
                meaning = "Marked by the use of few words to convey much information or meaning",
                example = "A brief but crucial admonition to keep quiet",
                synonyms = listOf("Concise", "Summary", "Short", "Pithy"),
                antonyms = listOf("Wordy", "Verbose", "Redundant", "Repetitious")
            ),
            Word(
                id = 7,
                text = "Bother",
                partOfSpeech = "verb",
                pronunciation = "ˈbä-t͟hər",
                meaning = "To thrust oneself upon (another) without invitation",
                example = "I am never going to get this work done if people don't stop wandering into the room and bothering me",
                synonyms = listOf("Disturb", "Annoy", "Irritate", "Trouble"),
                antonyms = listOf("Help", "Assist", "Aid", "Support")
            ),
            Word(
                id = 8,
                text = "Calm",
                partOfSpeech = "adjective",
                pronunciation = "kälm",
                meaning = "Free from storms or physical disturbance",
                example = "After a stormy night of high winds and driving rains, the day dawned on a calm sea",
                synonyms = listOf("Quiet", "Serene", "Placid", "Tranquil"),
                antonyms = listOf("Angry", "Restless", "Unsettled", "Rough")
            ),
            Word(
                id = 9,
                text = "Capable",
                partOfSpeech = "adjective",
                pronunciation = "ˈkā-pə-bəl",
                meaning = "Having the required skills for an acceptable level of performance",
                example = "A capable and efficient editor",
                synonyms = listOf("Suitable", "Competent", "Qualified", "Able"),
                antonyms = listOf("Incompetent", "Poor", "Inept", "Unskilled")
            ),
            Word(
                id = 10,
                text = "Certain",
                partOfSpeech = "adjective",
                pronunciation = "ˈsər-tᵊn",
                meaning = "Known but not named",
                example = "A certain person told me that today is your birthday",
                synonyms = listOf("One", "Unnamed", "Anonymous", "Unidentified"),
                antonyms = listOf("Known", "Specified", "Named")
            ),
            Word(
                id = 11,
                text = "Combine",
                partOfSpeech = "verb",
                pronunciation = "kəm-ˈbīn",
                meaning = "To come together to form a single unit",
                example = "The room's highly varied design elements combine to form a harmonious whole",
                synonyms = listOf("Connect", "Fuse", "Unify", "Unite"),
                antonyms = listOf("Split", "Section", "Divide", "Isolate")
            ),
            Word(
                id = 12,
                text = "Common",
                partOfSpeech = "adjective",
                pronunciation = "ˈkä-mən",
                meaning = "Often observed or encountered",
                example = "Horse ranches are a common sight in that part of the state",
                synonyms = listOf("Ubiquitous", "Commonplace", "Ordinary", "Household"),
                antonyms = listOf("Unusual", "Uncommon", "Seldom", "Infrequent")
            ),
            Word(
                id = 13,
                text = "Decrease",
                partOfSpeech = "verb",
                pronunciation = "di-ˈkrēs",
                meaning = "To make smaller in amount, volume, or extent",
                example = "Workers decreased the volume of water flowing through the pipes in order to prevent an overflow",
                synonyms = listOf("Reduce", "Diminish", "Lower", "Minimize"),
                antonyms = listOf("Increase", "Augment", "Expand", "Raise")
            ),
            Word(
                id = 14,
                text = "Defend",
                partOfSpeech = "verb",
                pronunciation = "di-ˈfend",
                meaning = "To drive danger or attack away from",
                example = "A solemn oath to defend the mother country at any cost",
                synonyms = listOf("Protect", "Guard", "Safeguard", "Shield"),
                antonyms = listOf("Attack", "Uphold", "Maintain", "Support")
            ),
            Word(
                id = 15,
                text = "Disaster",
                partOfSpeech = "noun",
                pronunciation = "di-ˈza-stər",
                meaning = "A sudden violent event that brings about great loss or destruction",
                example = "Hurricanes are natural disasters",
                synonyms = listOf("Catastrophe", "Apocalypse", "Tragedy", "Calamity"),
                antonyms = listOf("Windfall", "Godsend", "Manna")
            ),
            Word(
                id = 16,
                text = "Enormous",
                partOfSpeech = "adjective",
                pronunciation = "i-ˈnȯr-məs",
                meaning = "Unusually large",
                example = "That pumpkin is so enormous that it has to be a record holder",
                synonyms = listOf("Huge", "Tremendous", "Massive", "Immense"),
                antonyms = listOf("Tiny", "Small", "Micro", "Miniature")
            ),
            Word(
                id = 17,
                text = "Exact",
                partOfSpeech = "adjective",
                pronunciation = "ig-ˈzakt",
                meaning = "Being in agreement with the truth or a fact or a standard",
                example = "Maybe I wasn't being very exact when I said I had done it a million times—but it sure seemed like it",
                synonyms = listOf("Correct", "True", "Proper", "Accurate"),
                antonyms = listOf("Wrong", "Incorrect", "False", "Improper")
            ),
            Word(
                id = 18,
                text = "Familiar",
                partOfSpeech = "adjective",
                pronunciation = "fə-ˈmil-yər",
                meaning = "Closely acquainted",
                example = "The little inside jokes that people who have long been familiar like to share",
                synonyms = listOf("Close", "Friendly", "Inseparable", "Intimate"),
                antonyms = listOf("Distant", "Cold", "Unfriendly", "Detached")
            ),
            Word(
                id = 19,
                text = "Flexible",
                partOfSpeech = "adjective",
                pronunciation = "ˈflek-sə-bəl",
                meaning = "Capable of being readily changed",
                example = "Fortunately, that working mother has a very flexible schedule for her office job",
                synonyms = listOf("Adjustable", "Adaptable", "Elastic", "Alterable"),
                antonyms = listOf("Fixed", "Inelastic", "Inflexible", "Established")
            ),
            Word(
                id = 20,
                text = "Foolish",
                partOfSpeech = "adjective",
                pronunciation = "ˈfü-lish",
                meaning = "Showing or marked by a lack of good sense or judgment",
                example = "Foolish people who thought that the world would end in the year 2000",
                synonyms = listOf("Absurd", "Silly", "Mad", "Insane"),
                antonyms = listOf("Wise", "Prudent", "Intelligent", "Judicious")
            ),
            // Words 21-30
            Word(
                id = 21,
                text = "Generous",
                partOfSpeech = "adjective",
                pronunciation = "ˈje-nə-rəs",
                meaning = "Giving or sharing in abundance and without hesitation",
                example = "A civic leader who is very generous with his money and time",
                synonyms = listOf("Charitable", "Benevolent", "Liberal", "Bountiful"),
                antonyms = listOf("Ungenerous", "Selfish", "Stingy", "Parsimonious")
            ),
            Word(
                id = 22,
                text = "Grateful",
                partOfSpeech = "adjective",
                pronunciation = "ˈgrāt-fəl",
                meaning = "Feeling or expressing gratitude",
                example = "She was grateful for her neighbor's help after she broke her foot",
                synonyms = listOf("Thankful", "Appreciative", "Glad", "Indebted"),
                antonyms = listOf("Ungrateful", "Thankless", "Unappreciative", "Thoughtless")
            ),
            Word(
                id = 23,
                text = "Harsh",
                partOfSpeech = "adjective",
                pronunciation = "ˈhärsh",
                meaning = "Difficult to endure",
                example = "Harsh conditions in the refugee camp",
                synonyms = listOf("Tough", "Brutal", "Rough", "Hard"),
                antonyms = listOf("Easy", "Soft", "Pleasant", "Friendly")
            ),
            Word(
                id = 24,
                text = "Honest",
                partOfSpeech = "adjective",
                pronunciation = "ˈä-nəst",
                meaning = "Being in the habit of telling the truth",
                example = "At least the weatherman is honest and doesn't pretend to be able to predict the unpredictable",
                synonyms = listOf("Outspoken", "Reliable", "Genuine", "Frank"),
                antonyms = listOf("Dishonest", "Untruthful", "Mendacious", "Lying")
            ),
            Word(
                id = 25,
                text = "Ignore",
                partOfSpeech = "verb",
                pronunciation = "ig-ˈnȯr",
                meaning = "To fail to give proper attention to",
                example = "Ignoring your health now will haunt you further down the road",
                synonyms = listOf("Overlook", "Disregard", "Neglect"),
                antonyms = listOf("Tend (to)", "Appreciate", "Regard", "Remember")
            ),
            Word(
                id = 26,
                text = "Improve",
                partOfSpeech = "verb",
                pronunciation = "im-ˈprüv",
                meaning = "To make better",
                example = "A little salt would improve this bland food",
                synonyms = listOf("Enhance", "Help", "Better", "Refine", "Amend"),
                antonyms = listOf("Worsen", "Impair", "Hurt", "Harm")
            ),
            Word(
                id = 27,
                text = "Lazy",
                partOfSpeech = "adjective",
                pronunciation = "ˈlā-zē",
                meaning = "Not easily aroused to action or work",
                example = "The lazy dog just wanted to lie on the couch all day and sleep",
                synonyms = listOf("Idle", "Sleepy", "Indolent", "Shiftless"),
                antonyms = listOf("Industrious", "Ambitious", "Diligent", "Zealous")
            ),
            Word(
                id = 28,
                text = "Limit",
                partOfSpeech = "verb",
                pronunciation = "ˈli-mət",
                meaning = "To set bounds or an upper limit for",
                example = "Limit the note to a few words",
                synonyms = listOf("Restrict", "Confine", "Hold down", "Hinder"),
                antonyms = listOf("Exceed", "Broaden", "Overextend", "Expand")
            ),
            Word(
                id = 29,
                text = "Mature",
                partOfSpeech = "adjective",
                pronunciation = "mə-ˈchu̇r",
                meaning = "Fully grown or developed",
                example = "I like pears when they're still hard, before they're mature",
                synonyms = listOf("Matured", "Adult", "Ripe", "Older"),
                antonyms = listOf("Young", "Immature", "Youthful", "Juvenile")
            ),
            Word(
                id = 30,
                text = "Messy",
                partOfSpeech = "adjective",
                pronunciation = "ˈme-sē",
                meaning = "Lacking in order, neatness, and often cleanliness",
                example = "Having a messy room is virtually de rigueur for a college student",
                synonyms = listOf("Chaotic", "Cluttered", "Jumbled", "Filthy"),
                antonyms = listOf("Neat", "Tidy", "Orderly", "Organized")
            ),
            // Words 31-40
            Word(
                id = 31,
                text = "Neglect",
                partOfSpeech = "verb",
                pronunciation = "ni-ˈglekt",
                meaning = "To fail to give proper attention to",
                example = "As usual the news media neglected the real issues of the campaign and focused on personalities",
                synonyms = listOf("Ignore", "Disregard", "Forget", "Overlook"),
                antonyms = listOf("Appreciate", "Regard", "Tend(to)")
            ),
            Word(
                id = 32,
                text = "Notorious",
                partOfSpeech = "adjective",
                pronunciation = "nō-ˈtȯr-ē-əs",
                meaning = "Not respectable",
                example = "A notorious mastermind of terrorist activities",
                synonyms = listOf("Infamous", "Criminal", "Shady"),
                antonyms = listOf("Honorable", "Respectable", "Refutable")
            ),
            Word(
                id = 33,
                text = "Necessary",
                partOfSpeech = "adjective",
                pronunciation = "ˈne-sə-ˌser-ē",
                meaning = "Forcing one's compliance or participation by or as if by law",
                example = "An emissions test is necessary before you can renew the registration for your car",
                synonyms = listOf("Required", "Compulsory", "Mandatory"),
                antonyms = listOf("Optional", "Voluntary", "Unnecessary")
            ),
            Word(
                id = 34,
                text = "Obscure",
                partOfSpeech = "adjective",
                pronunciation = "əb-ˈskyu̇r",
                meaning = "Having an often intentionally veiled or uncertain meaning",
                example = "A fantasy writer who likes to put lots of obscure references and images in her tales of wizards and warlocks",
                synonyms = listOf("Ambiguous", "Cryptic", "Enigmatic"),
                antonyms = listOf("Obvious", "Clear", "Certain")
            ),
            Word(
                id = 35,
                text = "Omit",
                partOfSpeech = "verb",
                pronunciation = "ō-ˈmit",
                meaning = "To miss the opportunity or obligation",
                example = "You must not omit mentioning the sources you used in researching your paper",
                synonyms = listOf("Forget", "Fail", "Ignore"),
                antonyms = listOf("Remember", "Keep", "Observe")
            ),
            Word(
                id = 36,
                text = "Perplex",
                partOfSpeech = "verb",
                pronunciation = "pər-ˈpleks",
                meaning = "To make complex or difficult",
                example = "Let's not perplex the issue further with irrelevant concerns",
                synonyms = listOf("Complex", "Complicate", "Confuse"),
                antonyms = listOf("Simplify", "Shorten")
            ),
            Word(
                id = 37,
                text = "Polite",
                partOfSpeech = "adjective",
                pronunciation = "pə-ˈlīt",
                meaning = "Showing consideration, courtesy, and good manners",
                example = "It's only polite to hold the door for the person behind you",
                synonyms = listOf("Thoughtful", "Respectful", "Courteous"),
                antonyms = listOf("Rude", "Impolite", "Thoughtless")
            ),
            Word(
                id = 38,
                text = "Privilege",
                partOfSpeech = "noun",
                pronunciation = "ˈpriv-lij",
                meaning = "Something granted as a special favor",
                example = "The town's oldest resident will have the privilege of leading the parade kicking off the Heritage Celebration",
                synonyms = listOf("Honor"),
                antonyms = listOf("Duty", "Responsibility")
            ),
            Word(
                id = 39,
                text = "Quaint",
                partOfSpeech = "adjective",
                pronunciation = "ˈkwānt",
                meaning = "Different from the ordinary in a way that causes curiosity or suspicion",
                example = "The sudden appearance of a man dressed in quaint clothes immediately drew the notice of passersby",
                synonyms = listOf("Bizarre", "Strange", "Odd"),
                antonyms = listOf("Typical", "Ordinary", "Normal")
            ),
            Word(
                id = 40,
                text = "Quell",
                partOfSpeech = "verb",
                pronunciation = "ˈkwel",
                meaning = "To put a stop to (something) by the use of force",
                example = "The National Guard was called in to help quell the late-night disturbances downtown",
                synonyms = listOf("Repress", "Suppress"),
                antonyms = listOf("Help", "Aid", "Support")
            ),
            // Words 41-50
            Word(
                id = 41,
                text = "Reluctant",
                partOfSpeech = "adjective",
                pronunciation = "ri-ˈlək-tənt",
                meaning = "Slow to begin or proceed with a course of action because of doubts or uncertainty",
                example = "I'm reluctant to let you borrow my vintage CDs since you never give back anything I lend you",
                synonyms = listOf("Unsure", "Unwilling"),
                antonyms = listOf("Eager", "Willing")
            ),
            Word(
                id = 42,
                text = "Resilient",
                partOfSpeech = "adjective",
                pronunciation = "ri-ˈzil-yənt",
                meaning = "Able to revert to original size and shape after being stretched, squeezed, or twisted",
                example = "After being dipped in liquid nitrogen, the rubber ball's normally resilient surface is as brittle as ceramic",
                synonyms = listOf("Flexible", "Stretch"),
                antonyms = listOf("Rigid", "Stiff")
            ),
            Word(
                id = 43,
                text = "Sincere",
                partOfSpeech = "adjective",
                pronunciation = "sin-ˈsir",
                meaning = "Genuine in feeling",
                example = "She offered a sincere apology for her angry outburst",
                synonyms = listOf("Genuine", "Honest", "True"),
                antonyms = listOf("Insincere", "Feigned", "Fake")
            ),
            Word(
                id = 44,
                text = "Subtle",
                partOfSpeech = "adjective",
                pronunciation = "ˈsə-təl",
                meaning = "Made or done with extreme care and accuracy",
                example = "The subtle strokes of the painter's brush",
                synonyms = listOf("Delicate", "Nice", "Fine"),
                antonyms = listOf("Apparent", "Evident", "Plain")
            ),
            Word(
                id = 45,
                text = "Selfish",
                partOfSpeech = "adjective",
                pronunciation = "ˈsel-fish",
                meaning = "Overly concerned with one's own desires, needs, or interests",
                example = "A selfish desire to succeed at the expense of others",
                synonyms = listOf("Self-centered", "Egoistic", "Narcissistic"),
                antonyms = listOf("Selfless", "Benevolent", "Generous")
            ),
            Word(
                id = 46,
                text = "Tedious",
                partOfSpeech = "adjective",
                pronunciation = "ˈtē-dē-əs",
                meaning = "Causing weariness, restlessness, or lack of interest",
                example = "A long and tedious staff meeting",
                synonyms = listOf("Boring", "Slow", "Dull"),
                antonyms = listOf("Interesting", "Engaging", "Exciting")
            ),
            Word(
                id = 47,
                text = "Trivial",
                partOfSpeech = "adjective",
                pronunciation = "ˈtri-vē-əl",
                meaning = "Lacking importance",
                example = "Why spend so much time on trivial decisions, like whether the soda should be regular or diet?",
                synonyms = listOf("Minor", "Little", "Unimportant"),
                antonyms = listOf("Major", "Important", "Significant")
            ),
            Word(
                id = 48,
                text = "Tense",
                partOfSpeech = "adjective",
                pronunciation = "ˈten(t)s",
                meaning = "Marked by or causing agitation or uncomfortable feelings",
                example = "A tense relationship existed between the two teachers",
                synonyms = listOf("Uneasy", "Anxious", "Nervous", "Restless"),
                antonyms = listOf("Comfortable", "Peaceful", "Calming")
            ),
            Word(
                id = 49,
                text = "Unanimous",
                partOfSpeech = "adjective",
                pronunciation = "yü-ˈna-nə-məs",
                meaning = "Having or marked by agreement in feeling or action",
                example = "A unanimous vote to upgrade the school's computer facilities",
                synonyms = listOf("Compatible", "United", "Amicable"),
                antonyms = listOf("Incompatible", "Disagreeable", "Conflicting")
            ),
            Word(
                id = 50,
                text = "Unusual",
                partOfSpeech = "adjective",
                pronunciation = "ˌən-ˈyü-zhə-wəl",
                meaning = "Noticeably different from what is generally found or experienced",
                example = "We found some unusual shells by the high-tide mark while combing the beach",
                synonyms = listOf("Uncommon", "Rare", "Strange", "Weird", "Extraordinary"),
                antonyms = listOf("Common", "Usual", "Ordinary", "Plain")
            ),
            // Words 51-60
            Word(
                id = 51,
                text = "Vague",
                partOfSpeech = "adjective",
                pronunciation = "ˈvāg",
                meaning = "Not seen or understood clearly",
                example = "I have only a vague idea of what you're talking about",
                synonyms = listOf("Unclear", "Fuzzy", "Ambiguous"),
                antonyms = listOf("Clear", "Obvious", "Definite")
            ),
            Word(
                id = 52,
                text = "Valiant",
                partOfSpeech = "adjective",
                pronunciation = "ˈval-yənt",
                meaning = "Possessing or showing courage or determination",
                example = "The valiant soldiers defended their position against overwhelming odds",
                synonyms = listOf("Brave", "Fearless", "Courageous", "Heroic"),
                antonyms = listOf("Coward", "Fearful", "Timid")
            ),
            Word(
                id = 53,
                text = "Weary",
                partOfSpeech = "adjective",
                pronunciation = "ˈwir-ē",
                meaning = "Depleted in strength, energy, or freshness",
                example = "I am just too weary to do any more work tonight",
                synonyms = listOf("Tired", "Exhausted", "Drained", "Fatigued"),
                antonyms = listOf("Relax", "Tireless", "Rested")
            ),
            Word(
                id = 54,
                text = "Witty",
                partOfSpeech = "adjective",
                pronunciation = "ˈwi-tē",
                meaning = "Given to or marked by mature intelligent humor",
                example = "A witty and sardonic blogger who never fails to amuse his legion of readers",
                synonyms = listOf("Humorous", "Funny", "Clever"),
                antonyms = listOf("Stupid", "Lame", "Corny")
            ),
            Word(
                id = 55,
                text = "Xenophobic",
                partOfSpeech = "adjective",
                pronunciation = "ˌze-nə-ˈfō-bik",
                meaning = "One unduly fearful of what is foreign and especially of people of foreign origin",
                example = "Xenophobic attitudes have no place in our diverse society",
                synonyms = listOf("Nativist", "Antiforeign"),
                antonyms = listOf("Internationalist")
            ),
            Word(
                id = 56,
                text = "Xeric",
                partOfSpeech = "adjective",
                pronunciation = "ˈzir-ik",
                meaning = "Characterized by, relating to, or requiring only a small amount of moisture",
                example = "Xeric plants like cacti thrive in desert environments",
                synonyms = listOf("Arid", "Dry"),
                antonyms = listOf("Humid", "Moist")
            ),
            Word(
                id = 57,
                text = "Yelp",
                partOfSpeech = "verb",
                pronunciation = "ˈyelp",
                meaning = "To cry out loudly and emotionally",
                example = "Yelped with surprise when everything fell off the closet shelf and onto his head",
                synonyms = listOf("Scream", "Squeal", "Shout"),
                antonyms = listOf("Whisper", "Murmur", "Mutter")
            ),
            Word(
                id = 58,
                text = "Yoke",
                partOfSpeech = "noun",
                pronunciation = "ˈyōk",
                meaning = "The state of being an enslaved person",
                example = "A people able at last to throw off the yoke and to embrace freedom",
                synonyms = listOf("Slavery", "Bondage", "Servitude"),
                antonyms = listOf("Freedom", "Liberty", "Independence")
            ),
            Word(
                id = 59,
                text = "Zealous",
                partOfSpeech = "adjective",
                pronunciation = "ˈze-ləs",
                meaning = "Feeling or showing strong and energetic support for a person, cause, etc.; filled with zeal",
                example = "She was one of the president's most zealous supporters",
                synonyms = listOf("Enthusiastic", "Passionate"),
                antonyms = listOf("Objective", "Detached", "Impersonal")
            ),
            Word(
                id = 60,
                text = "Zany",
                partOfSpeech = "adjective",
                pronunciation = "ˈzā-nē",
                meaning = "Showing or marked by a lack of good sense or judgment",
                example = "A zany plan to drive cross-country on a motorized scooter",
                synonyms = listOf("Foolish", "Absurd", "Crazy", "Insane"),
                antonyms = listOf("Wise", "Sane", "Intelligent", "Clever")
            )
        )
    }
}
