package edu.cit.gaviola.noteify.core.extensions

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** Show a short Toast from any Activity. */
fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Navigate to [T] Activity, optionally forwarding USER_NAME and USER_EMAIL extras.
 */
inline fun <reified T : Activity> Activity.navigateTo(
    userName: String = "",
    userEmail: String = "",
    block: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java).apply {
        if (userName.isNotEmpty()) putExtra("USER_NAME", userName)
        if (userEmail.isNotEmpty()) putExtra("USER_EMAIL", userEmail)
        block()
    }
    startActivity(intent)
}

/**
 * Navigate to [T] and clear the entire back-stack.
 * Used for logout flows.
 */
inline fun <reified T : Activity> Activity.navigateAndClearStack(
    userName: String = "",
    userEmail: String = ""
) {
    navigateTo<T>(userName, userEmail) {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    finish()
}

/** Safely retrieve USER_NAME from the launching intent. */
fun AppCompatActivity.getUserName(): String =
    intent.getStringExtra("USER_NAME") ?: "Student"

/** Safely retrieve USER_EMAIL from the launching intent. */
fun AppCompatActivity.getUserEmail(): String =
    intent.getStringExtra("USER_EMAIL") ?: ""