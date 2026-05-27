package edu.cit.gaviola.noteify.feature.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.applyNavTabStyle
import edu.cit.gaviola.noteify.core.extensions.getUserEmail
import edu.cit.gaviola.noteify.core.extensions.getUserName
import edu.cit.gaviola.noteify.core.extensions.navigateAndClearStack
import edu.cit.gaviola.noteify.core.extensions.navigateTo
import edu.cit.gaviola.noteify.core.extensions.showToast
import edu.cit.gaviola.noteify.core.preferences.AppPreferences
import edu.cit.gaviola.noteify.feature.auth.login.MainActivity
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.feature.notes.list.NotesActivity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var prefs: AppPreferences
    private lateinit var noteViewModel: NoteViewModel

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileCourse: TextView
    private lateinit var tvProfileYear: TextView
    private lateinit var etCourse: EditText
    private lateinit var etYear: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnSaveProfile: Button
    private lateinit var viewModeGroup: LinearLayout
    private lateinit var editModeGroup: LinearLayout
    private lateinit var tvStorageUsed: TextView
    private lateinit var ivProfilePicture: ImageView

    private lateinit var userEmail: String

    /** Temp URI for the camera capture — we give this to the camera app. */
    private var cameraImageUri: Uri? = null

    // ── ActivityResult launchers ────────────────────────────────────────────

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCamera() else showToast(getString(R.string.error_camera_permission))
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri -> processAndSaveImage(uri) }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { processAndSaveImage(it) }
    }

    // ───────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userName = getUserName()
        userEmail    = getUserEmail()

        prefs         = AppPreferences(this)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvProfileName   = findViewById(R.id.tvProfileName)
        tvProfileCourse = findViewById(R.id.tvProfileCourse)
        tvProfileYear   = findViewById(R.id.tvProfileYear)
        etCourse        = findViewById(R.id.etCourse)
        etYear          = findViewById(R.id.etYear)
        btnEditProfile  = findViewById(R.id.btnEditProfile)
        btnSaveProfile  = findViewById(R.id.btnSaveProfile)
        viewModeGroup   = findViewById(R.id.viewModeGroup)
        editModeGroup   = findViewById(R.id.editModeGroup)
        tvStorageUsed   = findViewById(R.id.tvStorageUsed)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)

        tvProfileName.text = userName

        val savedCourse = prefs.getCourse(userEmail)
        val savedYear   = prefs.getYear(userEmail)
        tvProfileCourse.text = savedCourse.ifEmpty { getString(R.string.text_placeholder_course_default) }
        tvProfileYear.text   = savedYear.ifEmpty { getString(R.string.text_placeholder_year_default) }

        if (userEmail.isNotEmpty()) {
            noteViewModel.getNoteCount(userEmail).observe(this) { count ->
                findViewById<TextView>(R.id.tvNotesCount).text = count.toString()
            }
        }

        loadProfilePicture(ivProfilePicture)
        updateStorageDisplay()

        // Tapping the avatar container triggers the picker dialog
        findViewById<FrameLayout>(R.id.avatarContainer).setOnClickListener {
            showImageSourceDialog()
        }

        btnEditProfile.setOnClickListener {
            etCourse.setText(prefs.getCourse(userEmail).ifEmpty {
                getString(R.string.text_placeholder_course_default)
            })
            etYear.setText(prefs.getYear(userEmail).ifEmpty {
                getString(R.string.text_placeholder_year_default)
            })
            viewModeGroup.visibility = View.GONE
            editModeGroup.visibility = View.VISIBLE
        }

        btnSaveProfile.setOnClickListener {
            val newCourse = etCourse.text.toString().trim()
            val newYear   = etYear.text.toString().trim()

            if (newCourse.isEmpty() || newYear.isEmpty()) {
                showToast(getString(R.string.error_fill_profile_fields))
                return@setOnClickListener
            }

            prefs.setCourse(userEmail, newCourse)
            prefs.setYear(userEmail, newYear)

            tvProfileCourse.text = newCourse
            tvProfileYear.text   = newYear

            editModeGroup.visibility = View.GONE
            viewModeGroup.visibility = View.VISIBLE
            showToast(getString(R.string.text_profile_saved))
        }

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            navigateAndClearStack<MainActivity>()
        }

        setupBottomNav(userName, userEmail)
    }

    // ── Profile picture helpers ─────────────────────────────────────────────

    /**
     * Loads the saved profile picture (or the default avatar) into [target]
     * with a circular crop applied via Coil.
     */
    private fun loadProfilePicture(target: ImageView) {
        val savedPath = prefs.getProfileImagePath(userEmail)
        val file = if (savedPath.isNotEmpty()) File(savedPath) else null

        if (file != null && file.exists()) {
            target.load(file) {
                crossfade(true)
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_default_avatar)
                error(R.drawable.ic_default_avatar)
            }
        } else {
            target.load(R.drawable.ic_default_avatar) {
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title_change_photo))
            .setItems(
                arrayOf(
                    getString(R.string.option_take_photo),
                    getString(R.string.option_choose_gallery)
                )
            ) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndLaunch()
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> launchCamera()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        // Create a temp file in internal storage for the camera to write into
        val dir = File(filesDir, "profile_images").apply { mkdirs() }
        val tempFile = File(dir, "camera_temp.jpg")
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            tempFile
        )
        cameraImageUri = uri
        cameraLauncher.launch(uri)
    }

    /**
     * Scales the source [uri] to 512×512, crops to square, saves as JPEG
     * to internal storage, stores the path, and reloads the UI.
     */
    private fun processAndSaveImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val original    = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (original == null) {
                showToast(getString(R.string.error_image_load))
                return
            }

            val processed = cropAndScale(original, 512)
            original.recycle()

            // Save to internal storage
            val dir  = File(filesDir, "profile_images").apply { mkdirs() }
            val file = File(dir, "profile_${userEmail.replace("@", "_").replace(".", "_")}.jpg")
            FileOutputStream(file).use { out ->
                processed.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            processed.recycle()

            prefs.setProfileImagePath(userEmail, file.absolutePath)
            loadProfilePicture(ivProfilePicture)
            showToast(getString(R.string.text_photo_saved))

        } catch (e: Exception) {
            showToast(getString(R.string.error_image_load))
        }
    }

    /**
     * Crops [source] to a center square then scales it to [size]×[size].
     */
    private fun cropAndScale(source: Bitmap, size: Int): Bitmap {
        val dim    = minOf(source.width, source.height)
        val startX = (source.width  - dim) / 2
        val startY = (source.height - dim) / 2
        val cropped = Bitmap.createBitmap(source, startX, startY, dim, dim)
        val scaled  = Bitmap.createScaledBitmap(cropped, size, size, true)
        if (cropped !== source) cropped.recycle()
        return scaled
    }

    // ── Storage display ─────────────────────────────────────────────────────

    private fun updateStorageDisplay() {
        try {
            val cacheBytes = (cacheDir.listFiles()?.sumOf { it.length() } ?: 0L) +
                    (externalCacheDir?.listFiles()?.sumOf { it.length() } ?: 0L)
            val filesBytes = (filesDir.listFiles()?.sumOf { it.length() } ?: 0L)
            tvStorageUsed.text = formatBytes(cacheBytes + filesBytes)
        } catch (e: Exception) {
            tvStorageUsed.text = getString(R.string.label_storage_unavailable)
        }
    }

    private fun formatBytes(bytes: Long): String = when {
        bytes >= 1_073_741_824L -> String.format("%.2f GB", bytes / 1_073_741_824.0)
        bytes >= 1_048_576L     -> String.format("%.2f MB", bytes / 1_048_576.0)
        bytes >= 1_024L         -> String.format("%.2f KB", bytes / 1_024.0)
        else                    -> "$bytes B"
    }

    // ── Bottom nav ──────────────────────────────────────────────────────────

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab   = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = true)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            navigateTo<DashboardActivity>(userName, userEmail)
        }
        profileTab.setOnClickListener { /* already here */ }
    }
}