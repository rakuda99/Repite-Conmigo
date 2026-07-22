package com.repite.conmigo.models

data class Verb(
    val id: Int,
    val infinitive: String,
    val meaning: String,
    val past: String,
    val present: String,
    val future: String,
    val past_meaning: String,
    val present_meaning: String,
    val future_meaning: String
)
