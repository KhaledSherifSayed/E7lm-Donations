package com.meslmawy.ehlmdonations.models

data class User(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val rule: String? = null,
    val Committee : String?= null,
    var id: String? = null
)