package ru.andersen.practicetest.services

import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(AccountService::class.java)

    fun fetchAll(): List<Account> = accountsRepository.findAll()

    fun fetchTransactions(accountId: Long): List<Transaction> = transactionsRepository.findAllByAccount_Id(accountId)

    fun create(userName: String, pinCode: String): CreateAccountResponse {
        if (accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode)) {
            log.info("Error in account creation: account already exists")
            return CreateAccountResponse.AccountAlreadyExists
        }

        var user = usersRepository.findByName(userName)
        if (user == null) {
            user = User(name = userName)
            usersRepository.save(user)
            log.info("New user created $user")
        }

        val account = Account(
            user = user,
            pinCode = pinCode.sha256(),
            createdAt = Instant.now()
        )
        accountsRepository.save(account)
        log.info("New account created $account")
        return CreateAccountResponse.Ok
    }

}