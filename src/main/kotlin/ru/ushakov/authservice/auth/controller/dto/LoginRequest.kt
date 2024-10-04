package ru.ushakov.authservice.auth.controller.dto

data class LoginRequest(
    val username: String,
    val password: String
)
