package com.meslmawy.ehlmdonations.models

import com.google.firebase.auth.AuthResult

class State(
    val status: Status,
    val error: Throwable? = null
) {
    enum class Status {
        LOADING, SUCCESS, ERROR
    }
}
