package ru.andersen.practicetest.models

import java.math.BigDecimal
import java.security.MessageDigest
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    val user: User,
    val pinCode: String,
    val balance: BigDecimal = BigDecimal(0),
    val createdAt: Instant,
    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: List<Transaction>? = null
)

fun String.sha256() = MessageDigest.getInstance("SHA-256").digest(this.toByteArray(Charsets.UTF_8))
    .joinToString("") { "%02x".format(it) }