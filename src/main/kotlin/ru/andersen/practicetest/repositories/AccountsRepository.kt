package ru.andersen.practicetest.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import ru.andersen.practicetest.models.Account
import javax.persistence.LockModeType

interface AccountsRepository : JpaRepository<Account, Long> {

    fun existsAccountByUser_NameAndPinCode(name: String, pinCode: String): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAccountByIdAndPinCode(id: Long, pinCode: String): Account?
}