package com.example.vocanova.data.repository

import com.example.vocanova.data.model.QuestionType
import com.example.vocanova.data.model.QuizQuestion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor() {

    fun getQuestionsForLesson(quizId: String): List<QuizQuestion> {
        return when (quizId) {
            "week1" -> getWeek1Questions()
            "week2" -> getWeek2Questions()
            "week3" -> getWeek3Questions()
            "week4" -> getWeek4Questions()
            "part2" -> getPart2Questions()
            else -> getWeek1Questions() // Default to Week 1 if invalid ID
        }
    }

    private fun getWeek1Questions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "1. What is the primary purpose of using synonyms in writing?",
                options = listOf(
                    "a. to make sentences more complex",
                    "b. to make words sound more formal",
                    "c. to confuse the reader",
                    "d. to avoid repetition and improve word choice"
                ),
                correctAnswer = "d. to avoid repetition and improve word choice",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 2,
                question = "2. Which of the following words is a synonym for \"happy\"?",
                options = listOf(
                    "a. unhappy",
                    "b. miserable",
                    "c. joyful",
                    "d. sad"
                ),
                correctAnswer = "c. joyful",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 3,
                question = "3. What function do antonyms serve in language?",
                options = listOf(
                    "a. they show contrast and highlight differences",
                    "b. they make sentences longer",
                    "c. they replace repetitive words",
                    "d. they make ideas harder to understand"
                ),
                correctAnswer = "a. they show contrast and highlight differences",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 4,
                question = "4. Which of the following is an antonym of \"small\"?",
                options = listOf(
                    "a. tiny",
                    "b. petite",
                    "c. huge",
                    "d. Miniature"
                ),
                correctAnswer = "c. huge",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 5,
                question = "5. Why is word choice important when using synonyms and antonyms?",
                options = listOf(
                    "a. to confuse the audience with complex words",
                    "b. to express meanings clearly and choose the best word for a situation",
                    "c. to make the text harder to read",
                    "d. to avoid using any other words besides synonyms"
                ),
                correctAnswer = "b. to express meanings clearly and choose the best word for a situation",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 6,
                question = "6. How do synonyms help improve vocabulary?",
                options = listOf(
                    "a. they make the sentence more complicated",
                    "b. they provide words with opposite meanings",
                    "c. they offer different words with similar meanings, expanding vocabulary",
                    "d. they reduce the variety in word usage"
                ),
                correctAnswer = "c. they offer different words with similar meanings, expanding vocabulary",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 7,
                question = "7. Which of the following is a synonym for \"small\"?",
                options = listOf(
                    "a. tiny",
                    "b. enormous",
                    "c. petite",
                    "d. big"
                ),
                correctAnswer = "a. tiny",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 8,
                question = "8. How do antonyms help readers and speakers?",
                options = listOf(
                    "a. they highlight the similarities between words",
                    "b. they make words sound more positive",
                    "c. they help to emphasize differences between ideas or things",
                    "d. they help avoid using complex words"
                ),
                correctAnswer = "c. they help to emphasize differences between ideas or things",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 9,
                question = "9. What does understanding synonyms and antonyms allow you to do?",
                options = listOf(
                    "a. use words interchangeably by changing meaning",
                    "b. speak and write clearly and understand what you read more easily",
                    "c. create confusion in your writing",
                    "d. limit vocabulary use to a few words"
                ),
                correctAnswer = "b. speak and write clearly and understand what you read more easily",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 10,
                question = "10. Which of the following statements is true about synonyms and antonyms?",
                options = listOf(
                    "a. synonyms always have the exact same meaning",
                    "b. antonyms always make the language harder to understand",
                    "c. synonyms and antonyms help us use words more effectively by revealing subtle differences in meaning",
                    "d. synonyms and antonyms are the same"
                ),
                correctAnswer = "c. synonyms and antonyms help us use words more effectively by revealing subtle differences in meaning",
                type = QuestionType.SYNONYM
            )
        )
    }

    private fun getWeek2Questions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "1. The meal was good and everyone enjoyed it.",
                options = listOf(
                    "a. boring",
                    "b. excellent",
                    "c. cold",
                    "d. soft"
                ),
                correctAnswer = "b. excellent",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 2,
                question = "2. The night was cold and windy.",
                options = listOf(
                    "a. chilly",
                    "b. warm",
                    "c. hot",
                    "d. gentle"
                ),
                correctAnswer = "a. chilly",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 3,
                question = "3. She was happy to see her family again.",
                options = listOf(
                    "a. nervous",
                    "b. thoughtful",
                    "c. delighted",
                    "d. confused"
                ),
                correctAnswer = "c. delighted",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 4,
                question = "4. He walked fast to catch the bus.",
                options = listOf(
                    "a. strolled",
                    "b. crept",
                    "c. dashed",
                    "d. wandered"
                ),
                correctAnswer = "c. dashed",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 5,
                question = "5. The student gave a nice answer during the discussion.",
                options = listOf(
                    "a. silly",
                    "b. kind",
                    "c. pleasant",
                    "d. rough"
                ),
                correctAnswer = "c. pleasant",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 6,
                question = "6. The monster in the story was very big.",
                options = listOf(
                    "a. tiny",
                    "b. massive",
                    "c. short",
                    "d. flat"
                ),
                correctAnswer = "b. massive",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 7,
                question = "7. She felt tired after the long day.",
                options = listOf(
                    "a. joyful",
                    "b. exhausted",
                    "c. alert",
                    "d. energetic"
                ),
                correctAnswer = "b. exhausted",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 8,
                question = "8. The sound was too loud to ignore.",
                options = listOf(
                    "a. soft",
                    "b. noisy",
                    "c. quiet",
                    "d. faint"
                ),
                correctAnswer = "b. noisy",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 9,
                question = "9. He looked bad after the accident.",
                options = listOf(
                    "a. healthy",
                    "b. calm",
                    "c. terrible",
                    "d. kind"
                ),
                correctAnswer = "c. terrible",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 10,
                question = "10. The kitten was so small it fit in her hand.",
                options = listOf(
                    "a. gigantic",
                    "b. tiny",
                    "c. large",
                    "d. huge"
                ),
                correctAnswer = "b. tiny",
                type = QuestionType.SYNONYM
            )
        )
    }

    private fun getWeek3Questions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "1. The ancient house in Binondo, Manila ruins were built thousands of years ago.",
                options = listOf(
                    "a. old",
                    "b. large",
                    "c. strong",
                    "d. dark"
                ),
                correctAnswer = "a. old",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 2,
                question = "2. The weather was gloomy, unlike yesterday's bright and sunny day.",
                options = listOf(
                    "a. cheerful",
                    "b. rainy",
                    "c. dark",
                    "d. warm"
                ),
                correctAnswer = "c. dark",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 3,
                question = "3. Levi is very timid and avoids talking to strangers.",
                options = listOf(
                    "a. loud",
                    "b. shy",
                    "c. rude",
                    "d. happy"
                ),
                correctAnswer = "b. shy",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 4,
                question = "4. The dog was very aggressive, but the cat was calm and gentle.",
                options = listOf(
                    "a. fierce",
                    "b. playful",
                    "c. weak",
                    "d. friendly"
                ),
                correctAnswer = "a. fierce",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 5,
                question = "5. Ms. Mikasa Ackerman asked us to write a brief summary, not a long one.",
                options = listOf(
                    "a. fast",
                    "b. clear",
                    "c. short",
                    "d. confusing"
                ),
                correctAnswer = "c. short",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 6,
                question = "6. Tony Stark gave a generous donation, much more than what was expected.",
                options = listOf(
                    "a. small",
                    "b. helpful",
                    "c. kind",
                    "d. giving"
                ),
                correctAnswer = "d. giving",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 7,
                question = "7. Peter was very clumsy and kept dropping things.",
                options = listOf(
                    "a. graceful",
                    "b. careless",
                    "c. neat",
                    "d. careful"
                ),
                correctAnswer = "b. careless",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 8,
                question = "8. After running a mile, Eren was exhausted and could barely stand.",
                options = listOf(
                    "a. happy",
                    "b. sleepy",
                    "c. tired",
                    "d. excited"
                ),
                correctAnswer = "c. tired",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 9,
                question = "9. Our performance task instructions were vague, so I didn't know what to do.",
                options = listOf(
                    "a. clear",
                    "b. confusing",
                    "c. easy",
                    "d. difficult"
                ),
                correctAnswer = "b. confusing",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 10,
                question = "10. Natasha was reluctant to go on stage, unlike her eager sister.",
                options = listOf(
                    "a. scared",
                    "b. shy",
                    "c. hesitant",
                    "d. excited"
                ),
                correctAnswer = "c. hesitant",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 11,
                question = "Bonus Question 1: What is a context clue?",
                options = listOf(
                    "a) Examples that show how to use a word in a sentence.",
                    "b) Hints within a text that help readers figure out the meaning of familiar words.",
                    "c) Clues in the text that assist readers in figuring out the meaning of new or difficult words.",
                    "d) A group of vocabulary words to memorize."
                ),
                correctAnswer = "c) Clues in the text that assist readers in figuring out the meaning of new or difficult words.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 12,
                question = "Bonus Question 2: Which of the following is not an importance of context clues?",
                options = listOf(
                    "a) Enhanced Reading Comprehension",
                    "b) Increased Reading Fluency",
                    "c) Memorizing every word in the dictionary",
                    "d) Vocabulary Building"
                ),
                correctAnswer = "c) Memorizing every word in the dictionary",
                type = QuestionType.SYNONYM
            )
        )
    }

    private fun getWeek4Questions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "1. What does nuances mean?",
                options = listOf(
                    "a. It is the subtle differences in meaning between words, phrases, and ideas.",
                    "b. It is the opposite meaning of words",
                    "c. none of the above",
                    "d. all of the above"
                ),
                correctAnswer = "a. It is the subtle differences in meaning between words, phrases, and ideas.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 2,
                question = "2. Which of the following is true about word nuances?",
                options = listOf(
                    "a. it is not important",
                    "b. it affects how we understand what is being said.",
                    "c. It is somehow important",
                    "d. All of the above"
                ),
                correctAnswer = "b. it affects how we understand what is being said.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 3,
                question = "3. Is understanding word nuances important?",
                options = listOf(
                    "a. yes, because it greatly contributes to the way we understand things.",
                    "b. no, it is not important",
                    "c. maybe",
                    "d. none of the above"
                ),
                correctAnswer = "a. yes, because it greatly contributes to the way we understand things.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 4,
                question = "4. Which of the following is an effect of misunderstanding word nuances?",
                options = listOf(
                    "a. effective communication",
                    "b. grammar improvement",
                    "c. confusion or miscommunication",
                    "d. none of the above"
                ),
                correctAnswer = "c. confusion or miscommunication",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 5,
                question = "5. What is the importance of being knowledgeable about word nuances?",
                options = listOf(
                    "a. improved communication",
                    "b. emotional intelligence development",
                    "c. all of the above",
                    "d. none of the above"
                ),
                correctAnswer = "c. all of the above",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 6,
                question = "6. How can word nuances influence the tone of a conversation?",
                options = listOf(
                    "a. by changing the meaning of the message entirely.",
                    "b. by changing the mood or emotion conveyed through words.",
                    "c. by making the conversation longer.",
                    "d. none of the above"
                ),
                correctAnswer = "b. by changing the mood or emotion conveyed through words.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 7,
                question = "7. What can happen when you use words without understanding their nuances?",
                options = listOf(
                    "a. your message might be misunderstood or sound insensitive.",
                    "b. you can impress more people.",
                    "c. people will always agree with you.",
                    "d. all of the above"
                ),
                correctAnswer = "a. your message might be misunderstood or sound insensitive.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 8,
                question = "8. Which of the following pairs illustrates a nuanced difference in meaning?",
                options = listOf(
                    "a. Sad – Unhappy",
                    "b. House – Home",
                    "c. all of the above",
                    "d. none of the above"
                ),
                correctAnswer = "b. House – Home",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 9,
                question = "9. Which of the following best demonstrates the nuance between the words, confident and arrogant?",
                options = listOf(
                    "a. The word Confident is about being boastful, while arrogant always means staying humble.",
                    "b. The word Confident is about believing in oneself, while being arrogant implies superiority over others.",
                    "c. There is no difference at all.",
                    "d. None of the above"
                ),
                correctAnswer = "b. The word Confident is about believing in oneself, while being arrogant implies superiority over others.",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 10,
                question = "10. Which of the following best demonstrates the nuance between the words, brave and reckless?",
                options = listOf(
                    "a. reckless involves being courageous for a worthy cause, while brave means taking unnecessary risks without considering the consequences.",
                    "b. brave involves facing danger with courage for a worthy cause, while reckless involves taking unnecessary risks without considering the consequences.",
                    "c. they are just the same",
                    "d. none of the above"
                ),
                correctAnswer = "b. brave involves facing danger with courage for a worthy cause, while reckless involves taking unnecessary risks without considering the consequences.",
                type = QuestionType.SYNONYM
            )
        )
    }

    private fun getPart2Questions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "1. Which of the following best represents a synonym of the word brave, defined as \"feeling or displaying no fear by temperament\"?",
                options = listOf(
                    "a. timid",
                    "b. fearless",
                    "c. hesitant",
                    "d. coward"
                ),
                correctAnswer = "b. fearless",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 2,
                question = "2. If shadow is defined as \"partial darkness due to the obstruction of light rays,\" which of the following words is most opposite in meaning?",
                options = listOf(
                    "a. darkness",
                    "b. twilight",
                    "c. shade",
                    "d. light"
                ),
                correctAnswer = "d. light",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 3,
                question = "3. Which of the following best represents an antonym of the word betray, defined as \"to be unfaithful or disloyal to\"?",
                options = listOf(
                    "a. protect",
                    "b. backstab",
                    "c. trick",
                    "d. mislead"
                ),
                correctAnswer = "a. protect",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 4,
                question = "4. Which of the following best represents a synonym of the word victim, defined as \"a person or thing that is the object of abuse, criticism, or ridicule\"?",
                options = listOf(
                    "a. bystander",
                    "b. target",
                    "c. tormentor",
                    "d. opponent"
                ),
                correctAnswer = "c. tormentor",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 5,
                question = "5. \"After cleaning the classroom, Ms. Rivera instructed the students to properly throw away the trash in the bin.\" Based on the sentence, what is the synonym of 'throw away'?",
                options = listOf(
                    "a. dispose",
                    "b. collect",
                    "c. hide",
                    "d. keep"
                ),
                correctAnswer = "a. dispose",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 6,
                question = "6. \"After a long and arduous journey, the weary traveler named Erwin finally reached their destination.\" Based on the sentence, what is the antonym of 'weary'?",
                options = listOf(
                    "a. sleepy",
                    "b. healthy",
                    "c. happy",
                    "d. energetic"
                ),
                correctAnswer = "d. energetic",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 7,
                question = "7. \"During the trial, the suspect began to plead for mercy in front of the judge.\" Based on the sentence, what is the antonym of 'plead'?",
                options = listOf(
                    "a. request",
                    "b. beg",
                    "c. demand",
                    "d. ask"
                ),
                correctAnswer = "c. demand",
                type = QuestionType.ANTONYM
            ),
            QuizQuestion(
                id = 8,
                question = "8. \"Philippines condemns the use of violence on prisoners in every city jail.\" Based on the sentence, what is the synonym of 'condemns'?",
                options = listOf(
                    "a. approves",
                    "b. endorses",
                    "c. disregard",
                    "d. rejects"
                ),
                correctAnswer = "d. rejects",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 9,
                question = "9. Kael_________ Mount Ulap last Friday.",
                options = listOf(
                    "a. climbed",
                    "b. rose",
                    "c. mounted",
                    "d. none of the above"
                ),
                correctAnswer = "a. climbed",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 10,
                question = "10. Vasco looked so __________ when he saw a venomous snake in his bag.",
                options = listOf(
                    "a. surprised",
                    "b. anxious",
                    "c. scared",
                    "d. none of the above"
                ),
                correctAnswer = "c. scared",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 11,
                question = "11. Yumi felt_____for Minu when she saw his broken leg.",
                options = listOf(
                    "a. compassion",
                    "b. pity",
                    "c. heartbroken",
                    "d. none of the above"
                ),
                correctAnswer = "b. pity",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 12,
                question = "12. Jane was so ______ about the next chapter of Windbreaker.",
                options = listOf(
                    "a. analytical",
                    "b. nosy",
                    "c. wondering",
                    "d. curious"
                ),
                correctAnswer = "d. curious",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 13,
                question = "13. \"The student raised her hand to inquire about the homework assignment.\" What is the synonym for inquire based on the sentence?",
                options = listOf(
                    "a. ignore",
                    "b. ask",
                    "c. walk away",
                    "d. hide"
                ),
                correctAnswer = "b. ask",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 14,
                question = "14. After many years, she could still recognize her childhood friend in the photo. What is the synonym for recognize based on the sentence?",
                options = listOf(
                    "a. forget",
                    "b. write",
                    "c. identify",
                    "d. create"
                ),
                correctAnswer = "c. identify",
                type = QuestionType.SYNONYM
            ),
            QuizQuestion(
                id = 15,
                question = "15. The teacher's passion for science helped inspire her students to love learning. What is the antonym for inspire?",
                options = listOf(
                    "a. motivate",
                    "b. encourage",
                    "c. discourage",
                    "d. praise"
                ),
                correctAnswer = "c. discourage",
                type = QuestionType.ANTONYM
            )
        )
    }
}
