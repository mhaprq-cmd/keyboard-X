package com.keyboardx.app.ime

import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * Main IME service for Keyboard X - Phase 3.
 *
 * Provides native Android IME functionality with proper InputConnection handling.
 * Connects to KeyboardInputView for input UI and handles text editing operations.
 *
 * Phase 3 scope:
 * - Fixed InputConnection shadowing issue
 * - Real, native InputMethodService integration
 * - Callback-based communication with input view
 * - No Phase 4 keyboard layout logic
 */
class KeyboardIMEService : InputMethodService() {

    private var keyboardInputView: KeyboardInputView? = null
    private var currentEditorInfo: EditorInfo? = null
    private var activeInputConnection: InputConnection? = null

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

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        currentEditorInfo = editorInfo

        // Get the real InputConnection from InputMethodService without shadowing
        activeInputConnection = getCurrentInputConnection()

        if (!restarting) {
            resetKeyboardState()
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        activeInputConnection = null
        currentEditorInfo = null
        keyboardInputView?.clearFocusAndRefresh()
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfo = attribute

        // Get the real InputConnection from InputMethodService without shadowing
        activeInputConnection = getCurrentInputConnection()
    }

    override fun onExtractedTextClicked() {
        super.onExtractedTextClicked()
    }

    override fun onEvaluateFullscreenMode(): Boolean = false

    override fun onEvaluateInputViewShown(): Boolean = true

    private fun handleKeyPress(keyCode: Int) {
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> handleDelete()
            KeyEvent.KEYCODE_SPACE -> handleSpace()
            KeyEvent.KEYCODE_ENTER -> handleEnter()
            else -> {
                keyCodeToText(keyCode)?.let { text ->
                    handleTextInput(text)
                }
            }
        }
    }

    private fun handleTextInput(text: String) {
        val inputConnection = activeInputConnection ?: return
        inputConnection.commitText(text, 1)
    }

    private fun handleDelete() {
        val inputConnection = activeInputConnection ?: return
        inputConnection.deleteSurroundingText(1, 0)
    }

    private fun handleSpace() {
        val inputConnection = activeInputConnection ?: return
        inputConnection.commitText(" ", 1)
    }

    private fun handleEnter() {
        val inputConnection = activeInputConnection ?: return
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

            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_GO -> {
                inputConnection.performEditorAction(
                    editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                )
                hideSoftInput()
            }

            else -> {
                if ((editorInfo.inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0) {
                    inputConnection.commitText("\n", 1)
                } else {
                    hideSoftInput()
                }
            }
        }
    }

    private fun resetKeyboardState() {
        keyboardInputView?.resetState()
    }

    private fun hideSoftInput() {
        requestHideSelf(0)
    }

    private fun keyCodeToText(keyCode: Int): String? {
        return when (keyCode) {
            KeyEvent.KEYCODE_0 -> "0"
            KeyEvent.KEYCODE_1 -> "1"
            KeyEvent.KEYCODE_2 -> "2"
            KeyEvent.KEYCODE_3 -> "3"
            KeyEvent.KEYCODE_4 -> "4"
            KeyEvent.KEYCODE_5 -> "5"
            KeyEvent.KEYCODE_6 -> "6"
            KeyEvent.KEYCODE_7 -> "7"
            KeyEvent.KEYCODE_8 -> "8"
            KeyEvent.KEYCODE_9 -> "9"
            KeyEvent.KEYCODE_A -> "a"
            KeyEvent.KEYCODE_B -> "b"
            KeyEvent.KEYCODE_C -> "c"
            KeyEvent.KEYCODE_D -> "d"
            KeyEvent.KEYCODE_E -> "e"
            KeyEvent.KEYCODE_F -> "f"
            KeyEvent.KEYCODE_G -> "g"
            KeyEvent.KEYCODE_H -> "h"
            KeyEvent.KEYCODE_I -> "i"
            KeyEvent.KEYCODE_J -> "j"
            KeyEvent.KEYCODE_K -> "k"
            KeyEvent.KEYCODE_L -> "l"
            KeyEvent.KEYCODE_M -> "m"
            KeyEvent.KEYCODE_N -> "n"
            KeyEvent.KEYCODE_O -> "o"
            KeyEvent.KEYCODE_P -> "p"
            KeyEvent.KEYCODE_Q -> "q"
            KeyEvent.KEYCODE_R -> "r"
            KeyEvent.KEYCODE_S -> "s"
            KeyEvent.KEYCODE_T -> "t"
            KeyEvent.KEYCODE_U -> "u"
            KeyEvent.KEYCODE_V -> "v"
            KeyEvent.KEYCODE_W -> "w"
            KeyEvent.KEYCODE_X -> "x"
            KeyEvent.KEYCODE_Y -> "y"
            KeyEvent.KEYCODE_Z -> "z"
            KeyEvent.KEYCODE_COMMA -> ","
            KeyEvent.KEYCODE_PERIOD -> "."
            KeyEvent.KEYCODE_SEMICOLON -> ";"
            KeyEvent.KEYCODE_APOSTROPHE -> "'"
            KeyEvent.KEYCODE_MINUS -> "-"
            KeyEvent.KEYCODE_EQUALS -> "="
            KeyEvent.KEYCODE_SLASH -> "/"
            KeyEvent.KEYCODE_BACKSLASH -> "\\"
            else -> null
        }
    }
}
