package edu.cit.gaviola.noteify.auth.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.auth.data.UserEntity
import edu.cit.gaviola.noteify.auth.data.UserRepository
import edu.cit.gaviola.noteify.auth.register.RegistrationActivity
import edu.cit.gaviola.noteify.core.extensions.navigateTo
import edu.cit.gaviola.noteify.core.extensions.showToast
import edu.cit.gaviola.noteify.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.database.AppDatabase

/**
 * Login screen — MVP View implementation.
 *
 * This Activity is intentionally "dumb":
 *  - It only renders state that the Presenter pushes to it.
 *  - It delegates ALL decisions (validation, navigation logic) to [LoginPresenter].
 *  - It does NOT contain any if/else business logic.
 */
class MainActivity : AppCompatActivity(), LoginContract.View {

    private lateinit var presenter: LoginContract.Presenter

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var tvCreateAccount: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Wire up views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)

        // ProgressBar is added programmatically so we don't need to touch the XML layout.
        // If you add one to the XML, reference it here instead.
        progressBar = ProgressBar(this).also { it.visibility = View.GONE }

        // Build the Presenter — inject the repository (acting as our Model layer)
        val db = AppDatabase.getDatabase(application)
        val repository = UserRepository(db.userDao())
        presenter = LoginPresenter(this, repository)

        btnSignIn.setOnClickListener {
            presenter.onLoginClicked(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }

        tvCreateAccount.setOnClickListener {
            presenter.onCreateAccountClicked()
        }
    }

    // ── LoginContract.View implementation ─────────────────────────────────────

    override fun showLoading() {
        btnSignIn.isEnabled = false
        btnSignIn.text = "Signing in…"
    }

    override fun hideLoading() {
        btnSignIn.isEnabled = true
        btnSignIn.text = "Sign In"
    }

    override fun showError(message: String) {
        showToast(message)
    }

    override fun navigateToDashboard(user: UserEntity) {
        navigateTo<DashboardActivity>(user.fullName, user.email)
    }

    override fun navigateToRegistration() {
        navigateTo<RegistrationActivity>()
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onDestroy() {
        super.onDestroy()
        // Critical: detach View so the Presenter doesn't leak the Activity.
        presenter.detachView()
    }
}