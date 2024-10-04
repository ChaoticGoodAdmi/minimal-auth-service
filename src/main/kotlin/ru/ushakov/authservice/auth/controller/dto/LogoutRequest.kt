package ru.ushakov.authservice.auth.controller.dto

data class LogoutRequest(
    val refreshToken: String
)