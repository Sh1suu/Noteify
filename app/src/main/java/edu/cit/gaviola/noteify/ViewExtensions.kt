package edu.cit.gaviola.noteify.core.extensions

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Extension functions on View and its subclasses.
 *
 * Centralises visibility helpers and bottom-nav tab coloring logic
 * that was previously copy-pasted across DashboardActivity, NotesActivity,
 * ProfileActivity and CreateNoteActivity.
 */

/** Make a View visible. */
fun View.show() {
    visibility = View.VISIBLE
}

/** Make a View gone (does not occupy space). */
fun View.hide() {
    visibility = View.GONE
}

/** Make a View invisible (still occupies space). */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Apply the active/inactive tint to a bottom-nav icon + label pair.
 *
 * @param isActive Whether this tab is the currently selected one.
 */
fun LinearLayout.applyNavTabStyle(
    iconId: Int,
    labelId: Int,
    isActive: Boolean
) {
    val activeColor = Color.parseColor("#9b51e0")
    val inactiveColor = Color.parseColor("#888888")
    val color = if (isActive) activeColor else inactiveColor

    findViewById<ImageView>(iconId)?.setColorFilter(color)
    findViewById<TextView>(labelId)?.setTextColor(color)
}