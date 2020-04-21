package com.meslmawy.ehlmdonations.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.meslmawy.ehlmdonations.models.SingleLiveEvent
import com.meslmawy.ehlmdonations.models.State
import kotlinx.coroutines.*

class LoginViewModel() : ViewModel() {


    private val saveLoginEvent = SingleLiveEvent<State>()
    fun state(): LiveData<State> = saveLoginEvent
    private var firebaseAuth = FirebaseAuth.getInstance()

    private val toastMessage = MutableLiveData<String>()
    val showToast: LiveData<String>
        get() = toastMessage

    fun signIn(email: String, password: String) {
        saveLoginEvent.postValue(State(State.Status.LOADING))
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        saveLoginEvent.postValue(State(State.Status.SUCCESS))
                    }.addOnFailureListener {
                        saveLoginEvent.postValue(State(State.Status.ERROR, error = it))
                    }

            } catch (e: Exception) {
                saveLoginEvent.postValue(State(State.Status.ERROR, error = e))
            }
        }
    }

    fun showToast(string: String){
        toastMessage.value = string
    }
}

