package ru.andersen.practicetest.dto.transaction

import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
import java.math.BigDecimal
import java.time.Instant

data class TransactionInfo(
    val id: Long,
    val accountId: Long,
    val type: OperationType,
    val balanceBefore: BigDecimal,
    val balanceAfter: BigDecimal,
    val createdAt: Instant,
)

fun Transaction.toDto() = TransactionInfo(id, account.id, type, balanceBefore, balanceAfter, createdAt)