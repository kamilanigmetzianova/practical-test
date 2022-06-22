package ru.andersen.practicetest.dto.account

sealed class CreateAccountResponse {
    object Ok : CreateAccountResponse()
}