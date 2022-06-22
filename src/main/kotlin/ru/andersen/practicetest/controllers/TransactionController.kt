package ru.andersen.practicetest.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.andersen.practicetest.dto.transaction.DepositMoneyRequest
import ru.andersen.practicetest.dto.transaction.DepositMoneyResponse
import ru.andersen.practicetest.dto.transaction.TransferMoneyRequest
import ru.andersen.practicetest.dto.transaction.TransferMoneyResponse
import ru.andersen.practicetest.dto.transaction.WithdrawMoneyRequest
import ru.andersen.practicetest.dto.transaction.WithdrawMoneyResponse
import ru.andersen.practicetest.services.TransactionService
import javax.validation.Valid

@RestController
@RequestMapping("/api/transaction")
class TransactionController(private val transactionService: TransactionService) {

    @PostMapping("/deposit", consumes = ["application/json"])
    @Operation(
        summary = "Deposit money into an account",
        method = "POST",
        responses = [
            ApiResponse(responseCode = "200", description = "Deposited successfully"),
            ApiResponse(responseCode = "400", description = "Request payload has incorrect format"),
            ApiResponse(responseCode = "403", description = "Incorrect pin code for provided account"),
        ]
    )
    fun deposit(@Valid @RequestBody request: DepositMoneyRequest): ResponseEntity<Unit> {
        return when (transactionService.deposit(request.accountId!!, request.pinCode!!, request.amount!!)) {
            DepositMoneyResponse.Ok -> ResponseEntity.ok().build()
            DepositMoneyResponse.AccessDenied -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @PostMapping("/withdraw", consumes = ["application/json"])
    @Operation(
        summary = "Withdraw money",
        method = "POST",
        responses = [
            ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            ApiResponse(responseCode = "400", description = "Request payload has incorrect format"),
            ApiResponse(responseCode = "402", description = "Insufficient funds"),
            ApiResponse(responseCode = "403", description = "Incorrect pin code for provided account"),
        ]
    )
    fun withdraw(@Valid @RequestBody request: WithdrawMoneyRequest): ResponseEntity<Unit> {
        return when (transactionService.withdraw(request.accountId!!, request.pinCode!!, request.amount!!)) {
            WithdrawMoneyResponse.Ok -> ResponseEntity.ok().build()
            WithdrawMoneyResponse.AccessDenied -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            WithdrawMoneyResponse.InsufficientFunds -> ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build()
        }
    }

    @PostMapping("/transfer", consumes = ["application/json"])
    @Operation(
        summary = "Transfer money from one account to another",
        method = "POST",
        responses = [
            ApiResponse(responseCode = "200", description = "Money transferred successfully"),
            ApiResponse(responseCode = "400", description = "Request payload has incorrect format"),
            ApiResponse(responseCode = "402", description = "Insufficient funds"),
            ApiResponse(responseCode = "403", description = "Incorrect pin code for provided account"),
            ApiResponse(responseCode = "404", description = "Recipient not found"),
        ]
    )
    fun transfer(@Valid @RequestBody request: TransferMoneyRequest): ResponseEntity<Unit> {
        return when (transactionService.transfer(
            request.senderAccountId!!,
            request.recipientAccountId!!,
            request.pinCode!!,
            request.amount!!
        )) {
            TransferMoneyResponse.Ok -> ResponseEntity.ok().build()
            TransferMoneyResponse.AccessDenied -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            TransferMoneyResponse.InsufficientFunds -> ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build()
            TransferMoneyResponse.RecipientNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}