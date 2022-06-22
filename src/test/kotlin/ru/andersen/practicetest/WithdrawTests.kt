package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.dto.transaction.WithdrawMoneyResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.models.User
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.services.TransactionService
import java.math.BigDecimal
import java.time.Instant

class WithdrawTests {

    private val accountId = 1L
    private val userName = "Test"
    private val pinCode = "0000"
    private val balance = BigDecimal(100)
    private val account = Account(
        id = accountId,
        user = User(name = userName),
        pinCode = pinCode,
        balance = balance,
        createdAt = Instant.now()
    )
    private val withdrawAmount = BigDecimal(10)
    private val transaction = Transaction(
        account = account,
        type = OperationType.WITHDRAW,
        balanceBefore = balance,
        balanceAfter = balance.subtract(withdrawAmount),
        createdAt = Instant.now()
    )

    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val transactionService = TransactionService(accountsRepository, transactionsRepository)

    @Test
    fun `withdrawn money successfully`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) } returns account
        every { accountsRepository.save(any()) } returns account.copy(balance = balance.subtract(withdrawAmount))
        every { transactionsRepository.save(any()) } returns transaction

        //when
        val result: WithdrawMoneyResponse = transactionService.withdraw(accountId, pinCode, withdrawAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) }
        verify(exactly = 1) { accountsRepository.save(any()) }
        verify(exactly = 1) { transactionsRepository.save(any()) }

        assert(result is WithdrawMoneyResponse.Ok)
    }

    @Test
    fun `pin code invalid`() {
        //given
        every { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) } returns null

        //when
        val result: WithdrawMoneyResponse = transactionService.withdraw(accountId, pinCode, withdrawAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is WithdrawMoneyResponse.AccessDenied)
    }

    @Test
    fun `insufficient funds`() {
        //given
        val bigAmount = BigDecimal(1000)
        every { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) } returns account

        //when
        val result: WithdrawMoneyResponse = transactionService.withdraw(accountId, pinCode, bigAmount)

        //then
        verify(exactly = 1) { accountsRepository.findAccountByIdAndPinCode(accountId, pinCode) }
        verify(exactly = 0) { accountsRepository.save(any()) }
        verify(exactly = 0) { transactionsRepository.save(any()) }

        assert(result is WithdrawMoneyResponse.InsufficientFunds)
    }
}