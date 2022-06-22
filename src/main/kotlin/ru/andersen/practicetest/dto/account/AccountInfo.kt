package ru.andersen.practicetest.dto.account

import java.math.BigDecimal

data class AccountInfo(
    val customerName: String,
    val balance: BigDecimal,
)