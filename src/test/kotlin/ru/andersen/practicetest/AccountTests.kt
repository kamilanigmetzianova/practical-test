package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.OperationType
import ru.andersen.practicetest.models.Transaction
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.services.AccountService
import java.math.BigDecimal
import java.time.Instant

class AccountTests {

    private val account1 = Account(id = 1L, customerName = "Test1", pinCode = "0000", createdAt = Instant.now())
    private val account2 = Account(id = 2L, customerName = "Test2", pinCode = "1111", createdAt = Instant.now())
    private val transaction1 = Transaction(
        account = account1,
        type = OperationType.DEPOSIT,
        balanceBefore = BigDecimal(0),
        balanceAfter = BigDecimal(100),
        createdAt = Instant.now()
    )
    private val transaction2 = Transaction(
        account = account1,
        type = OperationType.WITHDRAW,
        balanceBefore = BigDecimal(100),
        balanceAfter = BigDecimal(10),
        createdAt = Instant.now()
    )

    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val accountService = AccountService(accountsRepository, transactionsRepository)

    @Test
    fun `accounts fetched successfully`() {
        //given
        val list = listOf(account1, account2)
        every { accountsRepository.findAll() } returns list

        //when
        val result = accountService.fetchAll()

        //then
        verify(exactly = 1) { accountsRepository.findAll() }

        assert(result == list)
    }

    @Test
    fun `transactions for specified account fetched successfully`() {
        //given
        val list = listOf(transaction1, transaction2)
        every { transactionsRepository.findAllByAccount_Id(account1.id) } returns list

        //when
        val result = accountService.fetchTransactions(account1.id)

        //then
        verify(exactly = 1) { transactionsRepository.findAllByAccount_Id(account1.id) }

        assert(result == list)
    }

}
