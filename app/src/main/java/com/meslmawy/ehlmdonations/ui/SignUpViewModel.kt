package com.meslmawy.ehlmdonations.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meslmawy.ehlmdonations.models.SingleLiveEvent
import com.meslmawy.ehlmdonations.models.State
import com.meslmawy.ehlmdonations.models.User
import kotlinx.coroutines.launch

class SignUpViewModel() : ViewModel() {

    private val saveLoginEvent = SingleLiveEvent<State>()
    fun state(): LiveData<State> = saveLoginEvent
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var  mFirestore = FirebaseFirestore.getInstance()

    private val toastMessage = MutableLiveData<String>()
    val showToast: LiveData<String>
        get() = toastMessage


    fun signUp(user: User) {
        saveLoginEvent.postValue(State(State.Status.LOADING))
        viewModelScope.launch {
            try {
                user.email?.let {
                    user.password?.let { it1 ->
                        firebaseAuth.createUserWithEmailAndPassword(
                            it,
                            it1
                        ).addOnSuccessListener {
                            val userid = firebaseAuth.currentUser!!.uid
                            user.id = userid
                            val users = mFirestore.collection("Users")
                            // Add a new document with a generated ID
                            user.id?.let {
                                users.document(it).set(user)
                                saveLoginEvent.postValue(State(State.Status.SUCCESS))
                            }
                        }.addOnFailureListener {
                            saveLoginEvent.postValue(State(State.Status.ERROR, error = it))
                        }
                    }
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
