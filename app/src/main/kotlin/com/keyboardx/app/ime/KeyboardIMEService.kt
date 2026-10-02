package com.keyboardx.app.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * Main Input Method Editor (IME) Service for Keyboard X.
 * Implements a real, functional IME using Android's InputMethodService.
 *
 * Responsibilities:
 * - Manage InputConnection with target application
 * - Create and display keyboard input view
 * - Handle basic text input operations: text commit, deletion, space, enter
 * - Handle input field changes and IME restart
 * - Separate IME core logic from UI implementation
 */
class KeyboardIMEService : InputMethodService() {

    private var keyboardInputView: KeyboardInputView? = null
    private var currentEditorInfo: EditorInfo? = null
    private var currentInputConnection: InputConnection? = null

    /**
     * Called when the IME creates its input view.
     * Returns a real keyboard input view instead of empty view.
     */
    override fun onCreateInputView(): View? {
        keyboardInputView = KeyboardInputView(this).apply {
            setOnKeyboardActionListener(object : KeyboardInputView.OnKeyboardActionListener {
                override fun onKeyPress(keyCode: Int) {
                    handleKeyPress(keyCode)
                }

                override fun onText(text: String) {
                    handleTextInput(text)
                }

                override fun onDelete() {
                    handleDelete()
                }

                override fun onSpace() {
                    handleSpace()
                }

                override fun onEnter() {
                    handleEnter()
                }
            })
        }
        return keyboardInputView
    }

    /**
     * Called when the input view is being shown and the input method should start its UI.
     * Updates editor info and establishes connection with target application.
     */
    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        currentEditorInfo = editorInfo
        currentInputConnection = currentInputConnection
        
        // Reset keyboard state when input starts or restarts
        if (!restarting) {
            resetKeyboardState()
        }
    }

    /**
     * Called when the input view is being hidden and the input method should hide its UI.
     * Clears connections and state.
     */
    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        currentInputConnection = null
        currentEditorInfo = null
        keyboardInputView?.clearFocus()
    }

    /**
     * Called when the user starts interacting with a new text input field.
     * Handles input field changes.
     */
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfo = attribute
        currentInputConnection = currentInputConnection
    }

    /**
     * Called when a key is long pressed.
     * Extended functionality can be added here in future phases.
     */
    override fun onExtractedTextClicked() {
        super.onExtractedTextClicked()
    }

    /**
     * Prevent fullscreen mode for this IME.
     */
    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    /**
     * Keep input view visible.
     */
    override fun onEvaluateInputViewShown(): Boolean {
        return true
    }

    // ============ Input Handling Methods ============

    /**
     * Handles a generic key press.
     * Delegates to specific handlers based on key code.
     */
    private fun handleKeyPress(keyCode: Int) {
        val inputConnection = currentInputConnection ?: return

        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> handleDelete()
            KeyEvent.KEYCODE_SPACE -> handleSpace()
            KeyEvent.KEYCODE_ENTER -> handleEnter()
            else -> {
                // Numeric or character key
                val character = keyCode.toChar().toString()
                inputConnection.commitText(character, 1)
            }
        }
    }

    /**
     * Handles text input from keyboard.
     * Commits the text to the target application via InputConnection.
     */
    private fun handleTextInput(text: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(text, text.length)
    }

    /**
     * Handles backspace/delete key.
     * Deletes one character before the cursor.
     */
    private fun handleDelete() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.deleteSurroundingText(1, 0)
    }

    /**
     * Handles space key.
     * Commits a space character.
     */
    private fun handleSpace() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(" ", 1)
    }

    /**
     * Handles Enter/Return key.
     * Behavior depends on EditorInfo input type and flags:
     * - For search actions: performs search if configured
     * - For send actions: performs send if configured
     * - For next actions: moves to next field
     * - Default: commits newline character
     */
    private fun handleEnter() {
        val inputConnection = currentInputConnection ?: return
        val editorInfo = currentEditorInfo ?: return

        when (editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_SEARCH -> {
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
                hideSoftInput()
            }
            EditorInfo.IME_ACTION_SEND -> {
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_SEND)
                hideSoftInput()
            }
            EditorInfo.IME_ACTION_NEXT -> {
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_NEXT)
            }
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO -> {
                inputConnection.performEditorAction(editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION)
                hideSoftInput()
            }
            else -> {
                // Default: commit newline for multi-line inputs
                if ((editorInfo.inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) != 0) {
                    inputConnection.commitText("\n", 1)
                } else {
                    // For single-line inputs, hide keyboard on enter
                    hideSoftInput()
                }
            }
        }
    }

    /**
     * Resets keyboard internal state when starting a new input session.
     */
    private fun resetKeyboardState() {
        keyboardInputView?.let {
            it.resetState()
        }
    }

    /**
     * Hides the soft input keyboard.
     */
    private fun hideSoftInput() {
        val windowToken = window?.window?.decorView?.windowToken
        if (windowToken != null) {
            requestHideSelf(0)
        }
    }
}
