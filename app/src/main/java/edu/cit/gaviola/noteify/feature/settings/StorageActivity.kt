package edu.cit.gaviola.noteify.feature.settings

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import edu.cit.gaviola.noteify.R

class StorageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        displayStorageInfo()
    }

    private fun displayStorageInfo() {
        // Internal storage via StatFs (available on all API levels >= 24)
        val internalPath = Environment.getDataDirectory()
        val statFs = StatFs(internalPath.path)

        val totalBytes     = statFs.totalBytes
        val availableBytes = statFs.availableBytes
        val usedBytes      = totalBytes - availableBytes

        // App-specific storage: cache + files directories
        val cacheBytes = (cacheDir.listFiles()?.sumOf { it.length() } ?: 0L) +
                (externalCacheDir?.listFiles()?.sumOf { it.length() } ?: 0L)
        val filesBytes = (filesDir.listFiles()?.sumOf { it.length() } ?: 0L)
        val appBytes   = cacheBytes + filesBytes

        val usagePercent = if (totalBytes > 0)
            ((usedBytes.toFloat() / totalBytes.toFloat()) * 100).toInt()
        else 0

        findViewById<TextView>(R.id.tvTotalStorage).text =
            getString(R.string.text_storage_total, formatBytes(totalBytes))
        findViewById<TextView>(R.id.tvUsedStorage).text =
            getString(R.string.text_storage_used_detail, formatBytes(usedBytes))
        findViewById<TextView>(R.id.tvAvailableStorage).text =
            getString(R.string.text_storage_available, formatBytes(availableBytes))
        findViewById<TextView>(R.id.tvAppStorage).text =
            getString(R.string.text_storage_app, formatBytes(appBytes))
        findViewById<TextView>(R.id.tvUsagePercent).text =
            getString(R.string.text_storage_percent, usagePercent)

        val progressBar = findViewById<ProgressBar>(R.id.progressStorageUsage)
        progressBar.progress = usagePercent
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824L -> String.format("%.2f GB", bytes / 1_073_741_824.0)
            bytes >= 1_048_576L     -> String.format("%.2f MB", bytes / 1_048_576.0)
            bytes >= 1_024L         -> String.format("%.2f KB", bytes / 1_024.0)
            else                    -> "$bytes B"
        }
    }
}