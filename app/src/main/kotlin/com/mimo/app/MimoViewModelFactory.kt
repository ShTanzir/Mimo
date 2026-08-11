package com.mimo.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Small generic factory so screens can build ViewModels that need a Context-derived repo. */
class MimoViewModelFactory<T : ViewModel>(private val creator: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <U : ViewModel> create(modelClass: Class<U>): U = creator() as U
}
