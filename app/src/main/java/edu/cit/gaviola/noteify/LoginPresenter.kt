package edu.cit.gaviola.noteify.auth.login

import edu.cit.gaviola.noteify.auth.data.UserRepository
import edu.cit.gaviola.noteify.core.extensions.isValidEmail
import edu.cit.gaviola.noteify.core.extensions.isValidPassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Presenter for the Login feature.
 *
 * - Contains ALL presentation logic (validation, async calls, navigation decisions).
 * - Has zero Android framework imports (no Context, no View, no Toast).
 * - Communicates with the View exclusively through [LoginContract.View].
 * - Uses coroutines internally; cancels all pending work in [detachView].
 *
 * Because it holds no Android types, this class can be unit-tested with
 * a plain JVM test and a fake/mock [LoginContract.View].
 */
class LoginPresenter(
    private var view: LoginContract.View?,
    private val repository: UserRepository
) : LoginContract.Presenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onLoginClicked(email: String, password: String) {
        // --- Input Validation (presentation logic lives here, not in the Activity) ---
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

            // Always dispatch back to Main before touching the View
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
        // Cancel all coroutines so we never call back into a destroyed Activity.
        presenterScope.coroutineContext[Job]?.cancel()
    }
}