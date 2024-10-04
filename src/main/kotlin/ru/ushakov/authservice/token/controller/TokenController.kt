package ru.ushakov.authservice.token.controller

import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ushakov.authservice.token.controller.dto.RefreshTokenRequest
import ru.ushakov.authservice.token.service.JwtTokenProvider
import ru.ushakov.authservice.token.service.TokenService

@RestController
@RequestMapping("/api/v1/token")
class TokenController(
    private val tokenService: TokenService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<Any> {
        val tokens = tokenService.refreshToken(refreshTokenRequest)
        return ResponseEntity.ok(tokens)
    }

    @GetMapping("/validate")
    fun validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String?): ResponseEntity<Any> {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Authorization header")
        }

        val token = authHeader.substring(7)
        val isValid = jwtTokenProvider.validateToken(token)

        return if (isValid) {
            ResponseEntity.ok(mapOf("message" to "Token is valid"))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid or expired")
        }
    }
}