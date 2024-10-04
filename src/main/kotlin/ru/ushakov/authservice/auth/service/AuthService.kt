package ru.ushakov.authservice.auth.service
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ushakov.authservice.auth.controller.dto.LoginRequest
import ru.ushakov.authservice.auth.controller.dto.LogoutRequest
import ru.ushakov.authservice.auth.controller.dto.RegisterRequest
import ru.ushakov.authservice.auth.domain.AuthUser
import ru.ushakov.authservice.auth.repo.AuthUserRepository
import ru.ushakov.authservice.token.service.JwtTokenProvider
import ru.ushakov.authservice.token.service.TokenService

@Service
class AuthService(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val tokenService: TokenService
) {

    @Transactional
    fun registerUser(registerRequest: RegisterRequest): AuthUser {
        require(authUserRepository.findByUsername(registerRequest.username) == null) { "Username is already taken" }
        require(authUserRepository.findByEmail(registerRequest.email) == null) { "Email is already registered" }

        val hashedPassword = passwordEncoder.encode(registerRequest.password)
        val newUser = AuthUser(
            username = registerRequest.username,
            passwordHash = hashedPassword,
            email = registerRequest.email
        )

        return authUserRepository.save(newUser)
    }

    fun authenticate(loginRequest: LoginRequest): Map<String, String> {
        val user = authUserRepository.findByUsername(loginRequest.username)
            ?: throw IllegalArgumentException("Invalid username or password")
        require(passwordEncoder.matches(loginRequest.password, user.passwordHash)) { "Invalid username or password" }
        val refreshToken = tokenService.generateAndSaveRefreshToken(user.id)
        val jwtToken = jwtTokenProvider.createToken(user.username, user.id)
        return mapOf(
            "token" to jwtToken,
            "refreshToken" to refreshToken.token
        )
    }

    @Transactional
    fun logout(logoutRequest: LogoutRequest) {
        val storedToken = tokenService.findByToken(logoutRequest.refreshToken)
            ?: throw IllegalArgumentException("Invalid refresh token")
        tokenService.delete(storedToken)
    }
}