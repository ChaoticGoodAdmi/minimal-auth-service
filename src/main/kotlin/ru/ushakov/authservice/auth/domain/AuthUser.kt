package ru.ushakov.authservice.auth.domain
import jakarta.persistence.*

@Entity
@Table(name = "auth_user")
data class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val passwordHash: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),

    @Column(nullable = true)
    var updatedAt: java.time.LocalDateTime? = null
)