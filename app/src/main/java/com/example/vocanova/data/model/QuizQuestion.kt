package com.example.vocanova.data.model

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val type: QuestionType
)
