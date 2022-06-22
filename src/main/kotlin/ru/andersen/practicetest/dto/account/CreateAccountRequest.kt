package ru.andersen.practicetest.dto.account

import ru.andersen.practicetest.validation.PinCode
import javax.validation.constraints.NotNull

data class CreateAccountRequest(
    @get:NotNull
    val userName: String?,

    @get:NotNull
    @get:PinCode
    val pinCode: String?,
)