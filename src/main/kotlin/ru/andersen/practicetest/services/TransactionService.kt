package ru.andersen.practicetest.services

import org.springframework.stereotype.Service
import ru.andersen.practicetest.dto.transaction.DepositMoneyResponse
import ru.andersen.practicetest.dto.transaction.TransferMoneyResponse
import ru.andersen.practicetest.dto.transaction.WithdrawMoneyResponse
import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
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

    @Transactional
    fun deposit(accountId: Long, pinCode: String, amount: BigDecimal): DepositMoneyResponse {
        val account = accountsRepository.findAccountByIdAndPinCode(accountId, pinCode)
            ?: return DepositMoneyResponse.AccessDenied

        val updatedBalance = account.balance.add(amount)

        val transaction = Transaction(
            account = account,
            type = OperationType.DEPOSIT,
            balanceBefore = account.balance,
            balanceAfter = updatedBalance,
            createdAt = Instant.now()
        )

        accountsRepository.save(account.copy(balance = updatedBalance))
        transactionsRepository.save(transaction)
        return DepositMoneyResponse.Ok
    }

    @Transactional
    fun withdraw(accountId: Long, pinCode: String, amount: BigDecimal): WithdrawMoneyResponse {
        val account = accountsRepository.findAccountByIdAndPinCode(accountId, pinCode)
            ?: return WithdrawMoneyResponse.AccessDenied

        if (account.balance < amount)
            return WithdrawMoneyResponse.InsufficientFunds

        val updatedBalance = account.balance.subtract(amount)

        val transaction = Transaction(
            account = account,
            type = OperationType.WITHDRAW,
            balanceBefore = account.balance,
            balanceAfter = updatedBalance,
            createdAt = Instant.now()
        )

        accountsRepository.save(account.copy(balance = updatedBalance))
        transactionsRepository.save(transaction)
        return WithdrawMoneyResponse.Ok
    }

    @Transactional
    fun transfer(
        senderAccountId: Long,
        receiverAccountId: Long,
        pinCode: String,
        amount: BigDecimal
    ): TransferMoneyResponse {
        val senderAccount = accountsRepository.findAccountByIdAndPinCode(senderAccountId, pinCode)
            ?: return TransferMoneyResponse.AccessDenied

        val recipientAccount = accountsRepository.findById(receiverAccountId).orElse(null)
            ?: return TransferMoneyResponse.RecipientNotFound

        if (senderAccount.balance < amount)
            return TransferMoneyResponse.InsufficientFunds

        val senderBalance = recipientAccount.balance.subtract(amount)
        val recipientBalance = recipientAccount.balance.add(amount)

        val senderTransaction = Transaction(
            account = senderAccount,
            type = OperationType.TRANSFER,
            balanceBefore = senderAccount.balance,
            balanceAfter = senderBalance,
            createdAt = Instant.now()
        )
        accountsRepository.save(senderAccount.copy(balance = senderBalance))
        transactionsRepository.save(senderTransaction)

        val recipientTransaction = Transaction(
            account = recipientAccount,
            type = OperationType.DEPOSIT,
            balanceBefore = recipientAccount.balance,
            balanceAfter = recipientBalance,
            createdAt = Instant.now()
        )
        accountsRepository.save(recipientAccount.copy(balance = recipientBalance))
        transactionsRepository.save(recipientTransaction)

        return TransferMoneyResponse.Ok
    }
}