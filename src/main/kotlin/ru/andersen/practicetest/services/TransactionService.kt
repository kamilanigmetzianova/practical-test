package ru.andersen.practicetest.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.andersen.practicetest.dto.transaction.DepositMoneyResponse
import ru.andersen.practicetest.dto.transaction.TransferMoneyResponse
import ru.andersen.practicetest.dto.transaction.WithdrawMoneyResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.models.sha256
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import java.math.BigDecimal
import java.time.Instant
import javax.transaction.Transactional

@Service
class TransactionService(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
) {

    private val log = LoggerFactory.getLogger(TransactionService::class.java)

    @Transactional
    fun deposit(accountId: Long, pinCode: String, amount: BigDecimal): DepositMoneyResponse {
        val account = accountsRepository.findAccountByIdAndPinCode(accountId, pinCode.sha256())
            ?: run {
                log.info("Pin code is invalid")
                return DepositMoneyResponse.AccessDenied
            }

        val updatedBalance = account.balance + amount
        val transaction = createTransaction(account, OperationType.DEPOSIT, updatedBalance)

        accountsRepository.save(account.copy(balance = updatedBalance))
        transactionsRepository.save(transaction)

        return DepositMoneyResponse.Ok
    }

    @Transactional
    fun withdraw(accountId: Long, pinCode: String, amount: BigDecimal): WithdrawMoneyResponse {
        val account = accountsRepository.findAccountByIdAndPinCode(accountId, pinCode.sha256())
            ?: run {
                log.info("Pin code is invalid")
                return WithdrawMoneyResponse.AccessDenied
            }

        if (account.balance < amount) {
            log.info("Insufficient funds in the account")
            return WithdrawMoneyResponse.InsufficientFunds
        }

        val updatedBalance = account.balance - amount
        val transaction = createTransaction(account, OperationType.DEPOSIT, updatedBalance)

        accountsRepository.save(account.copy(balance = updatedBalance))
        transactionsRepository.save(transaction)

        return WithdrawMoneyResponse.Ok
    }

    @Transactional
    fun transfer(
        senderAccountId: Long,
        recipientAccountId: Long,
        pinCode: String,
        amount: BigDecimal
    ): TransferMoneyResponse {
        val senderAccount = accountsRepository.findAccountByIdAndPinCode(senderAccountId, pinCode.sha256())
            ?: run {
                log.info("Pin code is invalid")
                return TransferMoneyResponse.AccessDenied
            }

        val recipientAccount = accountsRepository.findById(recipientAccountId).orElse(null)
            ?: run {
                log.info("Recipient with id=$recipientAccountId is not found")
                return TransferMoneyResponse.RecipientNotFound
            }

        if (senderAccount.balance < amount) {
            log.info("Insufficient funds in the account")
            return TransferMoneyResponse.InsufficientFunds
        }

        val senderBalance = senderAccount.balance - amount
        val senderTransaction = createTransaction(senderAccount, OperationType.TRANSFER, senderBalance)
        accountsRepository.save(senderAccount.copy(balance = senderBalance))
        transactionsRepository.save(senderTransaction)

        val recipientBalance = recipientAccount.balance + amount
        val recipientTransaction = createTransaction(recipientAccount, OperationType.DEPOSIT, recipientBalance)
        accountsRepository.save(recipientAccount.copy(balance = recipientBalance))
        transactionsRepository.save(recipientTransaction)

        return TransferMoneyResponse.Ok
    }

    private fun createTransaction(account: Account, type: OperationType, updatedBalance: BigDecimal): Transaction {
        return Transaction(
            account = account,
            type = type,
            balanceBefore = account.balance,
            balanceAfter = updatedBalance,
            createdAt = Instant.now()
        )
    }
}