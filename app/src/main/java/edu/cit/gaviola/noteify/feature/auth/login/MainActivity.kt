package edu.cit.gaviola.noteify.feature.auth.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.feature.auth.data.UserRepository
import edu.cit.gaviola.noteify.feature.auth.register.RegistrationActivity
import edu.cit.gaviola.noteify.core.extensions.navigateTo
import edu.cit.gaviola.noteify.core.extensions.showToast
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.core.data.AppDatabase

/**
 * Login screen — MVP View implementation.
 * Dumb view: renders state only, delegates all logic to LoginPresenter.
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

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignIn = findViewById(R.id.btnSignIn)
        tvCreateAccount = findViewById(R.id.tvCreateAccount)
        progressBar = ProgressBar(this).also { it.visibility = View.GONE }

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

    override fun showLoading() {
        btnSignIn.isEnabled = false
        btnSignIn.text = getString(R.string.btn_signing_in)
    }

    override fun hideLoading() {
        btnSignIn.isEnabled = true
        btnSignIn.text = getString(R.string.btn_sign_in)
    }

    override fun showError(message: String) = showToast(message)

    override fun navigateToDashboard(user: UserEntity) {
        navigateTo<DashboardActivity>(user.fullName, user.email)
    }

    override fun navigateToRegistration() {
        navigateTo<RegistrationActivity>()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}