package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        // Observe login result
        userViewModel.loginResult.observe(this) { user ->
            if (user != null) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USER_NAME", user.fullName)
                intent.putExtra("USER_EMAIL", user.email)
                startActivity(intent)
            }
        }

        // Observe error messages
        userViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        btnSignIn.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.login(email, password)
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}