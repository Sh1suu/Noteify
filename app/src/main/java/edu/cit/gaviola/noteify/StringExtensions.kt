package edu.cit.gaviola.noteify.core.extensions

/**
 * Extension functions on String.
 *
 * Small validators that were previously inlined as if-checks inside Activities.
 * Extracting them here keeps Activities lean and makes validation reusable.
 */

/** Returns true if the string is a plausible email address. */
fun String.isValidEmail(): Boolean =
    isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

/** Returns true if the string meets the minimum password length requirement (6 chars). */
fun String.isValidPassword(): Boolean = length >= 6

/**
 * Truncates the string to [maxLength] characters and appends "..." if it was longer.
 * Used for note content previews in the notes list.
 */
fun String.truncate(maxLength: Int = 80): String =
    if (length > maxLength) substring(0, maxLength) + "…" else this