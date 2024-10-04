package ru.ushakov.authservice.auth.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.ushakov.authservice.auth.domain.AuthUser

@Repository
interface AuthUserRepository : JpaRepository<AuthUser, Long> {
    fun findByUsername(username: String): AuthUser?
    fun findByEmail(email: String): AuthUser?
}