package com.keyboardx.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReportIssueActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var descriptionInput: EditText

    private val supportEmail = "dmolgax@gmail.com"

    private val categoryResourceIds = intArrayOf(
        R.string.report_category_hint,
        R.string.report_category_activation,
        R.string.report_category_typing,
        R.string.report_category_language,
        R.string.report_category_appearance,
        R.string.report_category_crash,
        R.string.report_category_privacy,
        R.string.report_category_other
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_report_issue)

        nameInput = findViewById(R.id.reportNameInput)
        emailInput = findViewById(R.id.reportEmailInput)
        categorySpinner = findViewById(R.id.reportCategorySpinner)
        descriptionInput = findViewById(R.id.reportDescriptionInput)

        setupCategorySpinner()

        findViewById<TextView>(R.id.reportIssueBackText)
            .setOnClickListener {
                finish()
            }

        findViewById<Button>(R.id.reportSendButton)
            .setOnClickListener {
                prepareEmailReport()
            }
    }

    private fun setupCategorySpinner() {
        val categories = categoryResourceIds.map { resourceId ->
            getString(resourceId)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        categorySpinner.adapter = adapter
        categorySpinner.setSelection(0)
    }

    private fun prepareEmailReport() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val categoryPosition = categorySpinner.selectedItemPosition

        nameInput.error = null
        emailInput.error = null
        descriptionInput.error = null

        if (email.isEmpty()) {
            emailInput.error = getString(
                R.string.report_error_email_required
            )
            emailInput.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = getString(
                R.string.report_error_email_invalid
            )
            emailInput.requestFocus()
            return
        }

        if (categoryPosition <= 0) {
            Toast.makeText(
                this,
                R.string.report_error_category_required,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (description.isEmpty()) {
            descriptionInput.error = getString(
                R.string.report_error_description_required
            )
            descriptionInput.requestFocus()
            return
        }

        val category = getString(
            categoryResourceIds[categoryPosition]
        )

        val subject = getString(
            R.string.report_email_subject,
            category
        )

        val body = getString(
            R.string.report_email_body,
            name.ifBlank { getString(R.string.report_name_label) },
            email,
            category,
            description
        )

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$supportEmail")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(
                Intent.createChooser(
                    emailIntent,
                    getString(R.string.report_send_button)
                )
            )
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                this,
                R.string.report_error_email_app_unavailable,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
