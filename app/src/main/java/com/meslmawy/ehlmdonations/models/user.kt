package com.meslmawy.ehlmdonations.models

data class User(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val rule: String? = null,
    val Committee : String?= null,
    var id: String? = null
)