package ru.andersen.practicetest.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.andersen.practicetest.models.Transaction

interface TransactionsRepository : JpaRepository<Transaction, Long> {
    fun findAllByAccount_Id(id: Long): List<Transaction>
}