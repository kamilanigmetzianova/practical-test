package ru.andersen.practicetest.dto.account

import java.math.BigDecimal

data class AccountInfo(
    val beneficiaryName: String,
    val balance: BigDecimal,
)