package ru.andersen.practicetest.dto.transaction

sealed class WithdrawMoneyResponse {
    object Ok : WithdrawMoneyResponse()
    object AccessDenied : WithdrawMoneyResponse()
    object InsufficientFunds : WithdrawMoneyResponse()
}