package ru.andersen.practicetest.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.andersen.practicetest.models.Beneficiary

interface BeneficiariesRepository: JpaRepository<Beneficiary, Long> {

    fun findByName(name: String): Beneficiary?
}