package edu.cit.gaviola.noteify.auth.register

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.auth.viewmodel.UserViewModel
import edu.cit.gaviola.noteify.core.extensions.showToast

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        userViewModel.registerResult.observe(this) { success ->
            if (success) {
                showToast("Account created successfully!")
                finish()
            }
        }

        userViewModel.errorMessage.observe(this) { message ->
            showToast(message)
        }

        btnCreateAccount.setOnClickListener {
            val fullName = findViewById<EditText>(R.id.etFullName).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            userViewModel.register(fullName, email, password)
        }

        tvSignIn.setOnClickListener { finish() }
    }
}