package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.dto.transaction.DepositMoneyResponse
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

class DepositTests {

    private val accountId = 1L
    private val beneficiaryName = "Test"
    private val pinCode = "0000"
    private val pinCodeHash = pinCode.sha256()
    private val balance = BigDecimal(10)
    private val account = Account(
        id = accountId,
        beneficiary = Beneficiary(name = beneficiaryName),
        pinCode = pinCodeHash,
        balance = balance,
        createdAt = Instant.now()
    )
    private val depositAmount = BigDecimal(100)
    private val transaction = Transaction(
        account = account,
        type = OperationType.DEPOSIT,
        balanceBefore = balance,
        balanceAfter = balance + depositAmount,
        createdAt = Instant.now()
    )

    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val transactionService = TransactionService(accountsRepository, transactionsRepository)

    @Test
    fun `deposited money successfully`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(accountId, pinCodeHash) } returns account
        every { accountsRepository.save(any()) } returns account.copy(balance = balance + depositAmount)
        every { transactionsRepository.save(any()) } returns transaction

        //when
        val result: DepositMoneyResponse = transactionService.deposit(accountId, pinCode, depositAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(accountId, pinCodeHash) }
        verify(exactly = 1) { accountsRepository.save(any()) }
        verify(exactly = 1) { transactionsRepository.save(any()) }

        assert(result is DepositMoneyResponse.Ok)
    }

    @Test
    fun `pin code invalid`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(accountId, pinCodeHash) } returns null

        //when
        val result: DepositMoneyResponse = transactionService.deposit(accountId, pinCode, depositAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(accountId, pinCodeHash) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is DepositMoneyResponse.AccessDenied)
    }
}