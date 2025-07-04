package com.example.vocanova.data.model

data class Word(
    val id: Int,
    val text: String,
    val partOfSpeech: String = "",
    val pronunciation: String = "",
    val meaning: String,
    val example: String = "",
    val synonyms: List<String>,
    val antonyms: List<String>,
    val isSaved: Boolean = false
)
