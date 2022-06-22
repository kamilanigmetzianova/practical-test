package ru.andersen.practicetest.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.andersen.practicetest.dto.account.AccountInfo
import ru.andersen.practicetest.dto.account.CreateAccountRequest
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.dto.transaction.TransactionInfo
import ru.andersen.practicetest.dto.transaction.toDto
import ru.andersen.practicetest.services.AccountService
import javax.validation.Valid

@RestController
@RequestMapping("/api/account")
class AccountController(private val accountService: AccountService) {

    @GetMapping("/fetch-all")
    @Operation(
        summary = "Get list of all existing accounts",
        method = "GET",
        responses = [
            ApiResponse(responseCode = "200", description = "Accounts listed"),
        ]
    )
    fun fetchAll(): ResponseEntity<List<AccountInfo>> {
        val list = accountService.fetchAll().map { AccountInfo(it.user.name, it.balance) }
        return ResponseEntity.ok(list)
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(
        summary = "Get list of all transactions for specific account",
        method = "GET",
        responses = [
            ApiResponse(responseCode = "200", description = "Transactions listed"),
        ]
    )
    fun fetchAccountTransactions(@PathVariable accountId: Long): ResponseEntity<List<TransactionInfo>> {
        val list = accountService.fetchTransactions(accountId).map { it.toDto() }
        return ResponseEntity.ok(list)
    }

    @PostMapping("/create", consumes = ["application/json"])
    @Operation(
        summary = "Create new account",
        method = "POST",
        responses = [
            ApiResponse(responseCode = "200", description = "Account created"),
            ApiResponse(responseCode = "400", description = "Request payload has incorrect format"),
            ApiResponse(responseCode = "409", description = "Such account already exists"),
        ]
    )
    fun create(@Valid @RequestBody request: CreateAccountRequest): ResponseEntity<Unit> {
        return when (accountService.create(request.userName!!, request.pinCode!!)) {
            CreateAccountResponse.Ok -> ResponseEntity.ok().build()
            CreateAccountResponse.AccountAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

}