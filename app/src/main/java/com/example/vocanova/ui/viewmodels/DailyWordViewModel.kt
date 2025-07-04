package com.example.vocanova.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.Word
import com.example.vocanova.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _dailyWord = MutableStateFlow<Word?>(null)
    val dailyWord: StateFlow<Word?> = _dailyWord

    init {
        loadDailyWord()
    }

    private fun loadDailyWord() {
        viewModelScope.launch {
            wordRepository.getDailyWord().collectLatest { word ->
                _dailyWord.value = word
            }
        }
    }

    fun refreshDailyWord() {
        loadDailyWord()
    }

    fun saveWord(wordId: Int) {
        viewModelScope.launch {
            wordRepository.saveWord(wordId)
        }
    }

    fun removeWord(wordId: Int) {
        viewModelScope.launch {
            wordRepository.removeWord(wordId)
        }
    }
}
