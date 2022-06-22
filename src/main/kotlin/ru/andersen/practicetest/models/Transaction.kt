package ru.andersen.practicetest.models

import java.math.BigDecimal
import java.time.Instant
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    val account: Account,
    @Enumerated(EnumType.STRING)
    val type: OperationType,
    val balanceBefore: BigDecimal,
    val balanceAfter: BigDecimal,
    val createdAt: Instant,
)

enum class OperationType {
    DEPOSIT, WITHDRAW, TRANSFER
}