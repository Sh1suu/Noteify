package edu.cit.gaviola.noteify.feature.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.cit.gaviola.noteify.feature.auth.data.UserRepository
import edu.cit.gaviola.noteify.core.data.AppDatabase
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _loginResult = MutableLiveData<UserEntity?>()
    val loginResult: LiveData<UserEntity?> = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> = _registerResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.loginUser(email, password)
            if (user != null) {
                _loginResult.postValue(user)
            } else {
                _loginResult.postValue(null)
                _errorMessage.postValue("Invalid email or password")
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            val success = repository.registerUser(fullName, email, password)
            if (success) {
                _registerResult.postValue(true)
            } else {
                _registerResult.postValue(false)
                _errorMessage.postValue("Email already exists")
            }
        }
    }
}