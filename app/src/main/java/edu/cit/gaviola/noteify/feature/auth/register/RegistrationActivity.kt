package edu.cit.gaviola.noteify.feature.auth.register

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.feature.auth.viewmodel.UserViewModel
import edu.cit.gaviola.noteify.core.extensions.isValidEmail
import edu.cit.gaviola.noteify.core.extensions.isValidPassword
import edu.cit.gaviola.noteify.core.extensions.showToast

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnCreateAccount: Button
    private lateinit var tvSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        etFullName        = findViewById(R.id.etFullName)
        etEmail           = findViewById(R.id.etEmail)
        etPassword        = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        cbTerms           = findViewById(R.id.cbTerms)
        btnCreateAccount  = findViewById(R.id.btnCreateAccount)
        tvSignIn          = findViewById(R.id.tvSignIn)

        userViewModel.registerResult.observe(this) { success ->
            if (success) {
                showToast(getString(R.string.text_account_created))
                finish()
            }
        }

        userViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        btnCreateAccount.setOnClickListener {
            val fullName        = etFullName.text.toString().trim()
            val email           = etEmail.text.toString().trim()
            val password        = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // All client-side validation fires BEFORE any DB call
            when {
                fullName.isEmpty() || email.isEmpty() ||
                        password.isEmpty() || confirmPassword.isEmpty() -> {
                    showToast(getString(R.string.error_fill_all_fields))
                    return@setOnClickListener
                }
                !email.isValidEmail() -> {
                    showToast(getString(R.string.error_invalid_email))
                    return@setOnClickListener
                }
                // Same isValidPassword() used by LoginPresenter — single source of truth (≥ 6 chars)
                !password.isValidPassword() -> {
                    showToast(getString(R.string.error_password_too_short))
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    showToast(getString(R.string.error_passwords_no_match))
                    return@setOnClickListener
                }
                !cbTerms.isChecked -> {
                    showToast(getString(R.string.error_accept_terms))
                    return@setOnClickListener
                }
            }

            userViewModel.register(fullName, email, password)
        }

        tvSignIn.setOnClickListener { finish() }
    }
}