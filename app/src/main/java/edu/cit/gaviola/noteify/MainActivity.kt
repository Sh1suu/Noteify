package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        btnSignIn.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass email to Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", email.substringBefore("@")) // use email prefix as name
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}