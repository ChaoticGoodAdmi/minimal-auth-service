package ru.ushakov.authservice.auth.controller.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)