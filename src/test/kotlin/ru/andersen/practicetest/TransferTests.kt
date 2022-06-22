package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.dto.transaction.TransferMoneyResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.models.Beneficiary
import ru.andersen.practicetest.models.sha256
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.services.TransactionService
import java.math.BigDecimal
import java.time.Instant
import java.util.*

class TransferTests {

    private val senderPinCode = "0000"
    private val senderPinCodeHash = senderPinCode.sha256()
    private val receiverPinCode = "1111"
    private val receiverPinCodeHash = receiverPinCode.sha256()

    private val senderAccount = Account(
        id = 1L,
        beneficiary = Beneficiary(name = "Sender"),
        pinCode = senderPinCodeHash,
        balance = BigDecimal(100),
        createdAt = Instant.now()
    )
    private val recipientAccount = Account(
        id = 2L,
        beneficiary = Beneficiary(name = "Recipient"),
        pinCode = receiverPinCodeHash,
        balance = BigDecimal(100),
        createdAt = Instant.now()
    )
    private val transferAmount = BigDecimal(10)
    private val senderTransaction = Transaction(
        account = senderAccount,
        type = OperationType.TRANSFER,
        balanceBefore = senderAccount.balance,
        balanceAfter = senderAccount.balance - transferAmount,
        createdAt = Instant.now()
    )
    private val recipientTransaction = Transaction(
        account = recipientAccount,
        type = OperationType.DEPOSIT,
        balanceBefore = recipientAccount.balance,
        balanceAfter = recipientAccount.balance + transferAmount,
        createdAt = Instant.now()
    )

    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val transactionService = TransactionService(accountsRepository, transactionsRepository)

    @Test
    fun `transferred money successfully`() {
        //given
        val updatedSender = senderAccount.copy(balance = senderAccount.balance - transferAmount)
        val updatedRecipient = recipientAccount.copy(balance = recipientAccount.balance + transferAmount)

        every { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) } returns senderAccount
        every { accountsRepository.findById(recipientAccount.id) } returns Optional.of(recipientAccount)
        every { accountsRepository.save(updatedSender) } returns updatedSender
        every { accountsRepository.save(updatedRecipient) } returns updatedRecipient
        every { transactionsRepository.save(any()) } returns senderTransaction //todo

        //when
        val result: TransferMoneyResponse =
            transactionService.transfer(senderAccount.id, recipientAccount.id, senderPinCode, transferAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) }
        verify(exactly = 1) { accountsRepository.findById(recipientAccount.id) }
        verify(exactly = 2) { accountsRepository.save(any()) }
        verify(exactly = 2) { transactionsRepository.save(any()) }

        assert(result is TransferMoneyResponse.Ok)
    }

    @Test
    fun `pin code invalid`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) } returns null

        //when
        val result: TransferMoneyResponse =
            transactionService.transfer(senderAccount.id, recipientAccount.id, senderPinCode, transferAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is TransferMoneyResponse.AccessDenied)
    }

    @Test
    fun `recipient not found`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) } returns senderAccount
        every { accountsRepository.findById(recipientAccount.id) } returns Optional.ofNullable(null)

        //when
        val result: TransferMoneyResponse =
            transactionService.transfer(senderAccount.id, recipientAccount.id, senderPinCode, transferAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) }
        verify(exactly = 1) { accountsRepository.findById(recipientAccount.id) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is TransferMoneyResponse.RecipientNotFound)
    }

    @Test
    fun `insufficient funds`() {
        //given
        val bigAmount = BigDecimal(1000)
        every { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) } returns senderAccount
        every { accountsRepository.findById(recipientAccount.id) } returns Optional.of(recipientAccount)

        //when
        val result: TransferMoneyResponse =
            transactionService.transfer(senderAccount.id, recipientAccount.id, senderPinCode, bigAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(senderAccount.id, senderPinCodeHash) }
        verify(exactly = 1) { accountsRepository.findById(recipientAccount.id) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is TransferMoneyResponse.InsufficientFunds)
    }
}