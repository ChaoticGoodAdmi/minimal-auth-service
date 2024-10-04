package ru.ushakov.authservice.auth.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ushakov.authservice.auth.service.AuthService
import ru.ushakov.authservice.auth.controller.dto.LoginRequest
import ru.ushakov.authservice.auth.controller.dto.LogoutRequest
import ru.ushakov.authservice.auth.controller.dto.RegisterRequest

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Any> {
        return try {
            val newUser = authService.registerUser(registerRequest)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("userId" to newUser.id))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        }
    }

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<Any> {
        return try {
            val tokens = authService.authenticate(loginRequest)
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to e.message))
        }
    }

    @PostMapping("/logout")
    fun logoutUser(@Valid @RequestBody logoutRequest: LogoutRequest): ResponseEntity<Any> {
        authService.logout(logoutRequest)
        return ResponseEntity.ok(mapOf("message" to "Successfully logged out"))
    }
}