package ru.ushakov.authservice.token.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ushakov.authservice.auth.repo.AuthUserRepository
import ru.ushakov.authservice.token.controller.dto.RefreshTokenRequest
import ru.ushakov.authservice.token.domain.RefreshToken
import ru.ushakov.authservice.token.repo.RefreshTokenRepository
import java.time.LocalDateTime
import java.util.*

@Service
class TokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authUserRepository: AuthUserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun generateAndSaveRefreshToken(userId: Long): RefreshToken {
        val token = UUID.randomUUID().toString()
        val expiresAt = LocalDateTime.now().plusDays(7)

        val refreshToken = RefreshToken(
            userId = userId,
            token = token,
            expiresAt = expiresAt
        )

        return refreshTokenRepository.save(refreshToken)
    }

    @Transactional
    fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Map<String, String> {
        val storedToken = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")
        require(!storedToken.expiresAt.isBefore(LocalDateTime.now())) { "Refresh token has expired" }
        val user = authUserRepository.findById(storedToken.userId)
            .orElseThrow { IllegalArgumentException("User not found") }
        val newJwtToken = jwtTokenProvider.createToken(user.username, user.id)
        return mapOf(
            "token" to newJwtToken,
            "refreshToken" to storedToken.token
        )
    }

    fun findByToken(refreshToken: String): RefreshToken? {
        return refreshTokenRepository.findByToken(refreshToken)
    }

    fun delete(storedToken: RefreshToken) {
        refreshTokenRepository.delete(storedToken)
    }
}