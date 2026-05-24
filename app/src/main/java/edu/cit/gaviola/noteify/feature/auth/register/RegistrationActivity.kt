package edu.cit.gaviola.noteify.feature.auth.register

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.isValidEmail
import edu.cit.gaviola.noteify.core.extensions.isValidPassword
import edu.cit.gaviola.noteify.core.extensions.showToast
import edu.cit.gaviola.noteify.core.preferences.AppPreferences
import edu.cit.gaviola.noteify.feature.auth.viewmodel.UserViewModel

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var prefs: AppPreferences

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etCourse: EditText
    private lateinit var spinnerYear: Spinner
    private lateinit var cbTerms: CheckBox
    private lateinit var btnCreateAccount: Button
    private lateinit var tvSignIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        prefs = AppPreferences(this)

        etFullName        = findViewById(R.id.etFullName)
        etEmail           = findViewById(R.id.etEmail)
        etPassword        = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etCourse          = findViewById(R.id.etCourse)
        spinnerYear       = findViewById(R.id.spinnerYear)
        cbTerms           = findViewById(R.id.cbTerms)
        btnCreateAccount  = findViewById(R.id.btnCreateAccount)
        tvSignIn          = findViewById(R.id.tvSignIn)

        // Populate year spinner
        val yearOptions = resources.getStringArray(R.array.year_levels)
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearOptions)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter

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
            val course          = etCourse.text.toString().trim()
            val year            = spinnerYear.selectedItem.toString()

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
                !password.isValidPassword() -> {
                    showToast(getString(R.string.error_password_too_short))
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    showToast(getString(R.string.error_passwords_no_match))
                    return@setOnClickListener
                }
                course.isEmpty() -> {
                    showToast(getString(R.string.error_fill_course))
                    return@setOnClickListener
                }
                year == yearOptions[0] -> {
                    showToast(getString(R.string.error_select_year))
                    return@setOnClickListener
                }
                !cbTerms.isChecked -> {
                    showToast(getString(R.string.error_accept_terms))
                    return@setOnClickListener
                }
            }

            // Observe register result, then save course/year once email is known
            userViewModel.registerResult.removeObservers(this)
            userViewModel.registerResult.observe(this) { success ->
                if (success) {
                    prefs.setCourse(email, course)
                    prefs.setYear(email, year)
                    showToast(getString(R.string.text_account_created))
                    finish()
                }
            }

            userViewModel.register(fullName, email, password)
        }

        tvSignIn.setOnClickListener { finish() }
    }
}