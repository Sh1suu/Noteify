package edu.cit.gaviola.noteify.repository

import edu.cit.gaviola.noteify.database.UserDao
import edu.cit.gaviola.noteify.database.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(fullName: String, email: String, password: String): Boolean {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                false // email already exists
            } else {
                userDao.insertUser(
                    UserEntity(
                        fullName = fullName,
                        email = email,
                        password = password
                    )
                )
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }
}