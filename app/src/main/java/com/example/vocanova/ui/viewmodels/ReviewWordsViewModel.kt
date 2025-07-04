package com.example.vocanova.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.vocanova.data.model.Word
import com.example.vocanova.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ReviewWordsViewModel @Inject constructor(
    wordRepository: WordRepository
) : ViewModel() {

    val savedWords: Flow<List<Word>> = wordRepository.getSavedWords()
}
