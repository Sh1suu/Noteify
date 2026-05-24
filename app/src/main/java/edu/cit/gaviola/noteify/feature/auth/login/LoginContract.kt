package edu.cit.gaviola.noteify.feature.auth.login

import edu.cit.gaviola.noteify.core.model.UserEntity

/**
 * MVP Contract for the Login screen.
 */
interface LoginContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToDashboard(user: UserEntity)
        fun navigateToRegistration()
    }

    interface Presenter {
        fun onLoginClicked(email: String, password: String)
        fun onCreateAccountClicked()
        fun detachView()
    }
}