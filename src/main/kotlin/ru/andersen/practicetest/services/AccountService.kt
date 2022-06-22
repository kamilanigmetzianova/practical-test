package ru.andersen.practicetest.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.models.Beneficiary
import ru.andersen.practicetest.models.sha256
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.repositories.BeneficiariesRepository
import java.time.Instant

@Service
class AccountService(
    private val beneficiariesRepository: BeneficiariesRepository,
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository
) {

    private val log = LoggerFactory.getLogger(AccountService::class.java)

    fun fetchAll(): List<Account> = accountsRepository.findAll()

    fun fetchTransactions(accountId: Long): List<Transaction> = transactionsRepository.findAllByAccount_Id(accountId)

    fun create(beneficiaryName: String, pinCode: String): CreateAccountResponse {
        var beneficiary = beneficiariesRepository.findByName(beneficiaryName)
        if (beneficiary == null) {
            beneficiary = Beneficiary(name = beneficiaryName)
            beneficiariesRepository.save(beneficiary)
            log.info("New beneficiary created, id=${beneficiary.id}")
        }

        val account = Account(
            beneficiary = beneficiary,
            pinCode = pinCode.sha256(),
            createdAt = Instant.now()
        )
        accountsRepository.save(account)
        log.info("New account created, id=${account.id}")
        return CreateAccountResponse.Ok
    }

}