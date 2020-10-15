package com.example.bloodapp.data

class UserData(var imageUrl: String, val name: String, val bloodGroup: String, val mobile: String, val email: String, val uid: String) {
    constructor() : this("", "", "", "", "", "")
}

class Loc(val latitude: Double, val longitude: Double){
    constructor(): this(0.0, 0.0)
}