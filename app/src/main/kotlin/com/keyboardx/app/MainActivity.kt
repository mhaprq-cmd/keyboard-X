package com.keyboardx.app

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var homeScreen: LinearLayout
    private lateinit var appearanceScreen: LinearLayout
    private lateinit var settingsScreen: LinearLayout

    private lateinit var navHome: TextView
    private lateinit var navAppearance: TextView
    private lateinit var navSettings: TextView
    private lateinit var keyboardEnabledStatus: TextView

    private val activeColor = 0xFF5546D8.toInt()
    private val inactiveColor = 0xFF77798B.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupNavigation()
        setupKeyboardActions()
        setupShortcuts()

        showScreen(
            savedInstanceState?.getString(KEY_CURRENT_SCREEN) ?: SCREEN_HOME
        )
    }

    override fun onResume() {
        super.onResume()

        if (::keyboardEnabledStatus.isInitialized) {
            refreshKeyboardStatus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_CURRENT_SCREEN, currentScreen)
        super.onSaveInstanceState(outState)
    }

    private var currentScreen = SCREEN_HOME

    private fun bindViews() {
        homeScreen = findViewById(R.id.homeScreen)
        appearanceScreen = findViewById(R.id.appearanceScreen)
        settingsScreen = findViewById(R.id.settingsScreen)

        navHome = findViewById(R.id.navHome)
        navAppearance = findViewById(R.id.navAppearance)
        navSettings = findViewById(R.id.navSettings)

        keyboardEnabledStatus =
            findViewById(R.id.keyboardEnabledStatus)
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            showScreen(SCREEN_HOME)
        }

        navAppearance.setOnClickListener {
            showScreen(SCREEN_APPEARANCE)
        }

        navSettings.setOnClickListener {
            showScreen(SCREEN_SETTINGS)
        }
    }

    private fun setupKeyboardActions() {
        findViewById<Button>(R.id.enableKeyboardButton)
            .setOnClickListener {
                openKeyboardSettings()
            }

        findViewById<Button>(R.id.selectKeyboardButton)
            .setOnClickListener {
                showKeyboardPicker()
            }
    }

    private fun setupShortcuts() {
        findViewById<LinearLayout>(R.id.homeAppearanceShortcut)
            .setOnClickListener {
                showScreen(SCREEN_APPEARANCE)
            }

        findViewById<LinearLayout>(R.id.homeSettingsShortcut)
            .setOnClickListener {
                showScreen(SCREEN_SETTINGS)
            }
    }

    private fun showScreen(screen: String) {
        currentScreen = screen

        homeScreen.visibility =
            if (screen == SCREEN_HOME) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        appearanceScreen.visibility =
            if (screen == SCREEN_APPEARANCE) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        settingsScreen.visibility =
            if (screen == SCREEN_SETTINGS) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        navHome.setTextColor(
            if (screen == SCREEN_HOME) activeColor else inactiveColor
        )

        navAppearance.setTextColor(
            if (screen == SCREEN_APPEARANCE) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navSettings.setTextColor(
            if (screen == SCREEN_SETTINGS) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navHome.isSelected = screen == SCREEN_HOME
        navAppearance.isSelected = screen == SCREEN_APPEARANCE
        navSettings.isSelected = screen == SCREEN_SETTINGS
    }

    private fun refreshKeyboardStatus() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        val isEnabled = inputMethodManager
            .enabledInputMethodList
            .any { inputMethod ->
                inputMethod.packageName == packageName &&
                    inputMethod.serviceName ==
                    "$packageName.ime.KeyboardIMEService"
            }

        val selectedMethod = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )

        val isSelected =
            isEnabled &&
                selectedMethod?.contains(
                    "$packageName.ime.KeyboardIMEService"
                ) == true

        keyboardEnabledStatus.setText(
            when {
                isSelected -> R.string.keyboard_selected
                isEnabled -> R.string.keyboard_not_selected
                else -> R.string.keyboard_not_enabled
            }
        )

        keyboardEnabledStatus.setTextColor(
            if (isSelected) activeColor else inactiveColor
        )
    }

    private fun openKeyboardSettings() {
        try {
            startActivity(
                Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            )
        } catch (_: Exception) {
            startActivity(
                Intent(Settings.ACTION_SETTINGS)
            )
        }
    }

    private fun showKeyboardPicker() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        inputMethodManager.showInputMethodPicker()
    }

    companion object {
        private const val KEY_CURRENT_SCREEN = "current_screen"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_APPEARANCE = "appearance"
        private const val SCREEN_SETTINGS = "settings"
    }
}
