package ru.andersen.practicetest.services

import org.springframework.stereotype.Service
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.models.User
import ru.andersen.practicetest.models.sha256
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.repositories.UsersRepository
import java.time.Instant

@Service
class AccountService(
    private val usersRepository: UsersRepository,
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) {

    //todo: add logging
    //todo: scripts for filling in the db

    fun fetchAll(): List<Account> = accountsRepository.findAll()

    fun fetchTransactions(accountId: Long): List<Transaction> = transactionsRepository.findAllByAccount_Id(accountId)

    fun create(userName: String, pinCode: String): CreateAccountResponse {
        if (accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode)) {
            return CreateAccountResponse.AccountAlreadyExists
        }

        var user = usersRepository.findByName(userName)
        if (user == null) {
            user = User(name = userName)
            usersRepository.save(user)
        }

        val account = Account(
            user = user,
            pinCode = pinCode.sha256(),
            createdAt = Instant.now()
        )
        accountsRepository.save(account)
        return CreateAccountResponse.Ok
    }

}