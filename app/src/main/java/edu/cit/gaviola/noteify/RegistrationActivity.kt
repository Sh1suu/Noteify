package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.viewmodel.UserViewModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        // Observe register result
        userViewModel.registerResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                finish() // Go back to login
            }
        }

        // Observe error messages
        userViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        btnCreateAccount.setOnClickListener {
            val fullName = findViewById<EditText>(R.id.etFullName).text.toString().trim()
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.register(fullName, email, password)
        }

        tvSignIn.setOnClickListener {
            finish()
        }
    }
}