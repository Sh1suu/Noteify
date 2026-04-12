package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)

        btnCreateAccount.setOnClickListener {
            val fullName = findViewById<android.widget.EditText>(R.id.etFullName).text.toString()
            val email = findViewById<android.widget.EditText>(R.id.etEmail).text.toString()

            if (fullName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass data to DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", fullName)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
            finish()
        }

        tvSignIn.setOnClickListener {
            finish() // Go back to login
        }
    }
}