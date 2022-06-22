package ru.andersen.practicetest

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import ru.andersen.practicetest.dto.account.CreateAccountResponse
import ru.andersen.practicetest.models.Account
import ru.andersen.practicetest.models.Beneficiary
import ru.andersen.practicetest.repositories.AccountsRepository
import ru.andersen.practicetest.repositories.TransactionsRepository
import ru.andersen.practicetest.repositories.BeneficiariesRepository
import ru.andersen.practicetest.services.AccountService
import java.time.Instant

class CreateAccountTests {

    private val beneficiaryName = "Test"
    private val pinCode = "0000"
    private val account = Account(beneficiary = Beneficiary(name = beneficiaryName), pinCode = pinCode, createdAt = Instant.now())

    private val beneficiariesRepository: BeneficiariesRepository = mockk()
    private val accountsRepository: AccountsRepository = mockk()
    private val transactionsRepository: TransactionsRepository = mockk()
    private val accountService = AccountService(beneficiariesRepository, accountsRepository, transactionsRepository)

    @Test
    fun `account was created successfully`() {
        //given
        every { beneficiariesRepository.findByName(beneficiaryName) } returns Beneficiary(name = beneficiaryName)
        every { accountsRepository.save(any()) } returns account

        //when
        val result: CreateAccountResponse = accountService.create(beneficiaryName, pinCode)

        //then
        verify(exactly = 1) { beneficiariesRepository.findByName(beneficiaryName) }
        verify(exactly = 1) { accountsRepository.save(any()) }

        assert(result is CreateAccountResponse.Ok)
    }

}
