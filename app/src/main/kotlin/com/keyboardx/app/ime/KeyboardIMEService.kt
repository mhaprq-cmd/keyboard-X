package com.keyboardx.app.ime

import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class KeyboardIMEService : InputMethodService() {

    private var keyboardInputView: KeyboardInputView? = null
    private var currentEditorInfo: EditorInfo? = null
    private var activeInputConnection: InputConnection? = null

    override fun onCreateInputView(): View {
        keyboardInputView = KeyboardInputView(this).apply {
            setKeyboardLayout(KeyboardLayoutProvider.getEnglishLayout())

            setOnKeyboardActionListener(object :
                KeyboardInputView.OnKeyboardActionListener {

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

        return keyboardInputView!!
    }

    override fun onStartInputView(
        editorInfo: EditorInfo?,
        restarting: Boolean
    ) {
        super.onStartInputView(editorInfo, restarting)

        currentEditorInfo = editorInfo
        activeInputConnection = getCurrentInputConnection()

        if (!restarting) {
            keyboardInputView?.resetState()
        }
    }

    override fun onStartInput(
        attribute: EditorInfo?,
        restarting: Boolean
    ) {
        super.onStartInput(attribute, restarting)

        currentEditorInfo = attribute
        activeInputConnection = getCurrentInputConnection()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)

        activeInputConnection = null
        currentEditorInfo = null
        keyboardInputView?.clearFocusAndRefresh()
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onEvaluateInputViewShown(): Boolean {
        return true
    }

    private fun handleKeyPress(keyCode: Int) {
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> handleDelete()
            KeyEvent.KEYCODE_SPACE -> handleSpace()
            KeyEvent.KEYCODE_ENTER -> handleEnter()
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
                inputConnection.performEditorAction(
                    EditorInfo.IME_ACTION_SEARCH
                )
                hideKeyboard()
            }

            EditorInfo.IME_ACTION_SEND -> {
                inputConnection.performEditorAction(
                    EditorInfo.IME_ACTION_SEND
                )
                hideKeyboard()
            }

            EditorInfo.IME_ACTION_NEXT -> {
                inputConnection.performEditorAction(
                    EditorInfo.IME_ACTION_NEXT
                )
            }

            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_GO -> {
                inputConnection.performEditorAction(
                    editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                )
                hideKeyboard()
            }

            else -> {
                if (
                    (editorInfo.inputType and
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
                ) {
                    inputConnection.commitText("\n", 1)
                } else {
                    hideKeyboard()
                }
            }
        }
    }

    private fun hideKeyboard() {
        requestHideSelf(0)
    }
}
