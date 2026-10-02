package com.keyboardx.app.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

/**
 * Main Input Method Editor (IME) Service for Keyboard X.
 * Extends Android's InputMethodService to provide a real keyboard implementation.
 */
class KeyboardIMEService : InputMethodService() {

    private var inputConnection: InputConnection? = null

    override fun onCreateInputView(): View? {
        return super.onCreateInputView()
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        inputConnection = currentInputConnection
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        inputConnection = null
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val inputConnection = inputConnection ?: return
        when (primaryCode) {
            KeyEvent.KEYCODE_DEL -> {
                inputConnection.deleteSurroundingText(1, 0)
            }
            else -> {
                val character = primaryCode.toChar().toString()
                inputConnection.commitText(character, 1)
            }
        }
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onEvaluateInputViewShown(): Boolean {
        return true
    }
}