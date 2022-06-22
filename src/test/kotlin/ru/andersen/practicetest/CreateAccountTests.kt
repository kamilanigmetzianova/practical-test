package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.User
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.repositories.UsersRepository
import ru.andersen.practicetest.services.AccountService
import java.time.Instant

class CreateAccountTests {

    private val userName = "Test"
    private val pinCode = "0000"
    private val account = Account(user = User(name = userName), pinCode = pinCode, createdAt = Instant.now())

    private val usersRepository: UsersRepository = mockk()
    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val accountService = AccountService(usersRepository, accountsRepository, transactionsRepository)

    @Test
    fun `account was created successfully`() {
        //given
        every { usersRepository.findByName(userName) } returns User(name = userName)
        every { accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode) } returns false
        every { accountsRepository.save(any()) } returns account

        //when
        val result: CreateAccountResponse = accountService.create(userName, pinCode)

        //then
        verify(exactly = 1) { usersRepository.findByName(userName) }
        verify(exactly = 1) { accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode) }
        verify(exactly = 1) { accountsRepository.save(any()) }

        assert(result is CreateAccountResponse.Ok)
    }

    @Test
    fun `such account already exists`() {
        //given
        every { accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode) } returns true

        //when
        val result: CreateAccountResponse = accountService.create(userName, pinCode)

        //then
        verify(exactly = 1) { accountsRepository.existsAccountByUser_NameAndPinCode(userName, pinCode) }
        verify(exactly = 0) { accountsRepository.save(any()) }

        assert(result is CreateAccountResponse.AccountAlreadyExists)
    }

}
