package ru.andersen.practicetest.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.andersen.practicetest.models.Account

interface AccountsRepository : JpaRepository<Account, Long> {

    fun existsAccountByCustomerNameAndPinCode(name: String, pinCode: String): Boolean

    fun findAccountByIdAndPinCode(id: Long, pinCode: String): Account?
}