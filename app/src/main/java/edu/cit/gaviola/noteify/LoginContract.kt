package edu.cit.gaviola.noteify.auth.login

import edu.cit.gaviola.noteify.auth.data.UserEntity

/**
 * MVP Contract for the Login screen.
 *
 * Defines the obligations of the View and the Presenter as nested interfaces
 * so that the relationship between them is explicit and co-located.
 *
 * View  — passive UI layer; only renders state and delegates user events.
 * Presenter — contains ALL presentation logic; has no Android framework imports.
 */
interface LoginContract {

    /**
     * The View is implemented by [MainActivity].
     * It exposes only UI-level operations; it never makes decisions.
     */
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToDashboard(user: UserEntity)
        fun navigateToRegistration()
    }

    /**
     * The Presenter is implemented by [LoginPresenter].
     * It is constructed with a View reference and a Model (repository).
     */
    interface Presenter {
        /** Called when the user taps "Sign In". */
        fun onLoginClicked(email: String, password: String)

        /** Called when the user taps "Create one". */
        fun onCreateAccountClicked()

        /**
         * Must be called in the Activity's onDestroy() to prevent memory leaks.
         * After this call the presenter must not post any updates to the View.
         */
        fun detachView()
    }
}