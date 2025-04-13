package com.example.loginpage.view.profile.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    // Các MutableStateFlow để lưu trữ dữ liệu
    private val _name = MutableStateFlow("Emilia Clarke")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("1/1/2001")
    val dateOfBirth: StateFlow<String> = _dateOfBirth.asStateFlow()

    private val _mobile = MutableStateFlow("0987654321")
    val mobile: StateFlow<String> = _mobile.asStateFlow()

    // Hàm để cập nhật dữ liệu
    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateDateOfBirth(newDob: String) {
        _dateOfBirth.value = newDob
    }

    fun updateMobile(newMobile: String) {
        _mobile.value = newMobile
    }
}