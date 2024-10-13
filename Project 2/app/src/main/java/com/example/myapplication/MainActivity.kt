package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var reportDescription: EditText
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    // Keywords to detect fraud reports
    private val suspiciousKeywords = listOf("test", "spam", "fake", "nonsense")
    private var lastSubmissionTime: Long = 0 // For rate limiting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge layout
        setContentView(R.layout.activity_main)

        // Handle window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views and SharedPreferences
        reportDescription = findViewById(R.id.etReportDescription)
        submitButton = findViewById(R.id.btnSubmit)
        sharedPreferences = getSharedPreferences("Reports", MODE_PRIVATE)

        // Set up the submit button click listener
        submitButton.setOnClickListener {
            val description = reportDescription.text.toString().trim()

            when {
                isFraudulentReport(description) -> {
                    Toast.makeText(this, "Suspicious content detected. Please provide valid information.", Toast.LENGTH_SHORT).show()
                }
                isDuplicateReport(description) -> {
                    Toast.makeText(this, "Duplicate report detected. Please provide new information.", Toast.LENGTH_SHORT).show()
                }
                isRateLimited() -> {
                    Toast.makeText(this, "Please wait before submitting another report.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    saveReportHash(description)
                    openGoogleForm(description)
                }
            }
        }
    }

    // Check for suspicious content using keywords
    private fun isFraudulentReport(description: String): Boolean {
        return suspiciousKeywords.any { keyword -> description.contains(keyword, ignoreCase = true) }
    }

    // Check if the report is a duplicate
    private fun isDuplicateReport(description: String): Boolean {
        val reportHash = description.hashCode().toString()
        val previousHash = sharedPreferences.getString("lastReportHash", "")
        return reportHash == previousHash
    }

    // Rate limiting logic to prevent spamming
    private fun isRateLimited(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastSubmissionTime) < 60000 // 1-minute limit
    }

    // Save report hash to prevent future duplicates
    private fun saveReportHash(description: String) {
        val reportHash = description.hashCode().toString()
        sharedPreferences.edit().putString("lastReportHash", reportHash).apply()
        lastSubmissionTime = System.currentTimeMillis()
    }

    // Open Google Form using Intent
    private fun openGoogleForm(description: String) {
        val formUrl = "https://docs.google.com/forms/d/e/1FAIpQLSd2jwVj7gmmb4f9_YFSp3D9Fh6DJWhob2MF3kuc3v5aq0GhjA/viewform?usp=sf_link"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formUrl))
        startActivity(intent)
        Toast.makeText(this, "Report submitted successfully.", Toast.LENGTH_SHORT).show()
    }
}
