package ru.andersen.practicetest.dto.transaction

sealed class DepositMoneyResponse {
    object Ok : DepositMoneyResponse()
    object AccessDenied : DepositMoneyResponse()
}