package ru.andersen.practicetest.repositories

import org.springframework.data.jpa.repository.JpaRepository
import ru.andersen.practicetest.models.User

interface UsersRepository: JpaRepository<User, Long> {

    fun findByName(name: String): User?
}