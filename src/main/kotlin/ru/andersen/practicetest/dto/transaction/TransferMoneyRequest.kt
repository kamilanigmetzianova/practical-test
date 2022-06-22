package ru.andersen.practicetest.dto.transaction

import ru.andersen.practicetest.validation.PinCode
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class TransferMoneyRequest(
    @get:NotNull
    val senderAccountId: Long?,

    @get:NotNull
    val recipientAccountId: Long?,

    @get:NotNull
    @get:PinCode
    val pinCode: String?,

    @get:NotNull //todo: >0?
    val amount: BigDecimal?,
)