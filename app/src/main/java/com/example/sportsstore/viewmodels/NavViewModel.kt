package com.example.sportsstore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavViewModel : ViewModel() {
    private val _currentFragmentId = MutableLiveData<Int>()
    val currentFragmentId: LiveData<Int> get() = _currentFragmentId

    fun setCurrentFragment(fragmentId: Int) {
        if (_currentFragmentId.value != fragmentId) { // Prevent redundant updates
            _currentFragmentId.value = fragmentId
        }
    }
}