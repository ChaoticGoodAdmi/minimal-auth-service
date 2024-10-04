package ru.ushakov.authservice.token.controller.dto

data class RefreshTokenRequest(
    val refreshToken: String
)