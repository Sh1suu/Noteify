package edu.cit.gaviola.noteify.feature.auth.login

import edu.cit.gaviola.noteify.feature.auth.data.UserRepository
import edu.cit.gaviola.noteify.core.extensions.isValidEmail
import edu.cit.gaviola.noteify.core.extensions.isValidPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter for the Login feature.
 * Zero Android framework imports — fully unit-testable with a plain JVM test.
 * Uses the same isValidPassword() as RegistrationActivity (≥ 6 chars).
 */
class LoginPresenter(
    private var view: LoginContract.View?,
    private val repository: UserRepository
) : LoginContract.Presenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onLoginClicked(email: String, password: String) {
        when {
            email.isBlank() || password.isBlank() -> {
                view?.showError("Please enter email and password")
                return
            }
            !email.isValidEmail() -> {
                view?.showError("Please enter a valid email address")
                return
            }
            !password.isValidPassword() -> {
                view?.showError("Password must be at least 6 characters")
                return
            }
        }

        view?.showLoading()

        presenterScope.launch {
            val user = withContext(Dispatchers.IO) {
                repository.loginUser(email, password)
            }
            if (user != null) {
                view?.hideLoading()
                view?.navigateToDashboard(user)
            } else {
                view?.hideLoading()
                view?.showError("Invalid email or password")
            }
        }
    }

    override fun onCreateAccountClicked() {
        view?.navigateToRegistration()
    }

    override fun detachView() {
        view = null
        presenterScope.coroutineContext[Job]?.cancel()
    }
}