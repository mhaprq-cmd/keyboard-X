package com.keyboardx.app

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.keyboardx.app.ime.KeyboardInputView
import com.keyboardx.app.ime.KeyboardStyle
import com.keyboardx.app.ime.KeyboardStyleCatalog
import com.keyboardx.app.ime.KeyboardStyleStore
import kotlin.math.max
import kotlin.math.min

/**
 * Preview page for one keyboard style.
 *
 * It hosts the REAL KeyboardInputView (the same view the system keyboard
 * uses), forced to show the previewed style without saving it.
 * The style is saved only when the user taps Apply.
 *
 * The system keyboard is kept hidden so only Keyboard X is visible.
 */
class KeyboardPreviewActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var keyboardView: KeyboardInputView
    private lateinit var applyButton: Button
    private lateinit var style: KeyboardStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyboard_preview)

        val styleId = intent.getStringExtra(EXTRA_STYLE_ID)
            ?: KeyboardStyleStore.DEFAULT_STYLE_ID

        style = KeyboardStyleCatalog.findById(styleId)

        input = findViewById(R.id.previewInput)
        keyboardView = findViewById(R.id.previewKeyboard)
        applyButton = findViewById(R.id.previewApplyButton)

        findViewById<TextView>(R.id.previewStyleName)
            .setText(style.nameRes)

        findViewById<TextView>(R.id.previewBackText)
            .setOnClickListener {
                finish()
            }

        setupInput()
        setupKeyboard()
        setupApplyButton()
    }

    private fun setupInput() {
        // Do not open the system keyboard on top of Keyboard X.
        input.setShowSoftInputOnFocus(false)
        input.requestFocus()
    }

    private fun setupKeyboard() {
        keyboardView.setPreviewStyle(style)

        keyboardView.setOnKeyboardActionListener(
            object : KeyboardInputView.OnKeyboardActionListener {

                override fun onKeyPress(keyCode: Int) {
                    // No action needed for the preview field.
                }

                override fun onText(text: String) {
                    insertText(text)
                }

                override fun onDelete() {
                    deleteBeforeCursor()
                }

                override fun onSpace() {
                    insertText(" ")
                }

                override fun onEnter() {
                    insertText("\n")
                }

                override fun onSearch() {
                    // Search has no target inside the preview page.
                }
            }
        )
    }

    private fun insertText(text: String) {
        val editable = input.text
        val start = min(input.selectionStart, input.selectionEnd)
        val end = max(input.selectionStart, input.selectionEnd)

        val from = max(start, 0)
        val to = max(end, 0)

        editable.replace(from, to, text)
        input.setSelection(from + text.length)
    }

    private fun deleteBeforeCursor() {
        val editable = input.text
        val start = max(
            min(input.selectionStart, input.selectionEnd),
            0
        )
        val end = max(
            max(input.selectionStart, input.selectionEnd),
            0
        )

        if (start != end) {
            editable.delete(start, end)
            input.setSelection(start)
            return
        }

        if (start == 0) {
            return
        }

        val removeCount =
            if (
                start >= 2 &&
                Character.isLowSurrogate(editable[start - 1]) &&
                Character.isHighSurrogate(editable[start - 2])
            ) {
                2
            } else {
                1
            }

        editable.delete(start - removeCount, start)
        input.setSelection(start - removeCount)
    }

    private fun setupApplyButton() {
        refreshApplyButton()

        applyButton.setOnClickListener {
            if (isStyleApplied()) {
                return@setOnClickListener
            }

            KeyboardStyleStore.setSelectedId(this, style.id)
            refreshApplyButton()

            Toast.makeText(
                this,
                R.string.selection_saved,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isStyleApplied(): Boolean {
        return KeyboardStyleCatalog.getSelected(this).id == style.id
    }

    private fun refreshApplyButton() {
        val applied = isStyleApplied()

        applyButton.setText(
            if (applied) {
                R.string.keyboard_style_applied
            } else {
                R.string.keyboard_style_apply
            }
        )

        applyButton.setTextColor(
            ContextCompat.getColor(
                this,
                if (applied) {
                    R.color.app_accent
                } else {
                    R.color.app_background
                }
            )
        )

        ViewCompat.setBackgroundTintList(
            applyButton,
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    if (applied) {
                        R.color.app_accent_soft
                    } else {
                        R.color.app_accent
                    }
                )
            )
        )
    }

    companion object {
        const val EXTRA_STYLE_ID = "keyboard_style_id"
    }
}
