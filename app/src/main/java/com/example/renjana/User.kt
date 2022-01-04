package com.example.renjana

class User {
    var userName: String? = null
    var email: String? = null
    var userId: String? = null


    constructor(){}

    constructor(userName: String?, email: String?, userId: String?) {
        this.userName = userName
        this.email = email
        this.userId = userId
    }
}