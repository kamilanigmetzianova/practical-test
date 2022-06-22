package ru.andersen.practicetest.models

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, //or UUID
    val customerName: String,
    val pinCode: String,
    val balance: BigDecimal = BigDecimal(0),
    val createdAt: Instant,
    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: List<Transaction>? = null
)