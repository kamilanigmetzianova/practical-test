package ru.andersen.practicetest.dto.account

import java.math.BigDecimal

data class AccountInfo(
    val userName: String,
    val balance: BigDecimal,
)