package ru.andersen.practicetest.services

import org.springframework.stereotype.Service
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import java.time.Instant

@Service
class AccountService(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) {

    //todo: add logging
    //todo: scripts for filling in the db

    fun fetchAll(): List<Account> = accountsRepository.findAll()

    fun fetchTransactions(accountId: Long): List<Transaction> = transactionsRepository.findAllByAccount_Id(accountId)

    fun create(customerName: String, pinCode: String): CreateAccountResponse {
        if (accountsRepository.existsAccountByCustomerNameAndPinCode(customerName, pinCode)) {
            return CreateAccountResponse.AccountAlreadyExists
        }

        val account = Account(
            customerName = customerName,
            pinCode = pinCode,
            createdAt = Instant.now()
        )
        accountsRepository.save(account)
        return CreateAccountResponse.Ok
    }

}