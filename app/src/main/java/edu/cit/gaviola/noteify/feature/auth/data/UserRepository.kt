package edu.cit.gaviola.noteify.feature.auth.data

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(fullName: String, email: String, password: String): Boolean {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                false
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

    suspend fun loginUser(email: String, password: String): UserEntity? =
        userDao.login(email, password)

    suspend fun getUserByEmail(email: String): UserEntity? =
        userDao.getUserByEmail(email)
}