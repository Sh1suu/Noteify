package edu.cit.gaviola.noteify.core.extensions

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Extension functions on Activity / AppCompatActivity.
 *
 * These eliminate the boilerplate of repeated Intent construction,
 * flag setting, and Toast calls spread across every Activity in the project.
 */

/** Show a short Toast from any Activity without needing a context reference. */
fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Navigate to [destination] Activity, optionally forwarding user session extras
 * (USER_NAME and USER_EMAIL).  The caller can also pass extra configuration via [block].
 *
 * Example:
 *   navigateTo<DashboardActivity>(userName, userEmail)
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
 * Navigate to [destination] and clear the entire back-stack.
 * Used for logout flows where the user must not be able to press Back to return.
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

/** Safely retrieve USER_NAME from the launching intent, with a sensible default. */
fun AppCompatActivity.getUserName(): String =
    intent.getStringExtra("USER_NAME") ?: "Student"

/** Safely retrieve USER_EMAIL from the launching intent. */
fun AppCompatActivity.getUserEmail(): String =
    intent.getStringExtra("USER_EMAIL") ?: ""