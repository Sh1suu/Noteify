package edu.cit.gaviola.noteify.core.extensions

/**
 * Single source of truth for all string validation.
 * Both Login and Registration use these — do NOT duplicate inline.
 */

/** Returns true if the string is a plausible email address. */
fun String.isValidEmail(): Boolean =
    isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**
 * Returns true if the string meets the minimum password length requirement (6 chars).
 * Used by LoginPresenter AND RegistrationActivity — same rule, one definition.
 */
fun String.isValidPassword(): Boolean = length >= 6

/**
 * Truncates to [maxLength] characters and appends "…" if longer.
 * Used for note content previews.
 */
fun String.truncate(maxLength: Int = 80): String =
    if (length > maxLength) substring(0, maxLength) + "…" else this