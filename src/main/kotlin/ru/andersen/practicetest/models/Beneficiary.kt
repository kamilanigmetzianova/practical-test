package ru.andersen.practicetest.models

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Beneficiary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // or uuid
    val name: String,
    @OneToMany(mappedBy = "beneficiary", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val accounts: List<Account>? = null,
)