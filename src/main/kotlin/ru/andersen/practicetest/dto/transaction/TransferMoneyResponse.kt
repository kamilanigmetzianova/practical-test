package ru.andersen.practicetest.dto.transaction

sealed class TransferMoneyResponse {
    object Ok : TransferMoneyResponse()
    object AccessDenied : TransferMoneyResponse()
    object InsufficientFunds : TransferMoneyResponse()
    object RecipientNotFound : TransferMoneyResponse()
}