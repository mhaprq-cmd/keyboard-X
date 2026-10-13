package com.keyboardx.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var homeScreen: LinearLayout
    private lateinit var languagesScreen: LinearLayout
    private lateinit var appearanceScreen: LinearLayout
    private lateinit var settingsScreen: LinearLayout

    private lateinit var navHome: TextView
    private lateinit var navLanguages: TextView
    private lateinit var navAppearance: TextView
    private lateinit var navSettings: TextView
    private lateinit var keyboardEnabledStatus: TextView

    private val activeColor: Int
        get() = ContextCompat.getColor(
            this,
            R.color.app_accent
        )

    private val inactiveColor: Int
        get() = ContextCompat.getColor(
            this,
            R.color.app_text_secondary
        )

    private var currentScreen = SCREEN_HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupNavigation()
        setupKeyboardActions()
        setupShortcuts()

        showScreen(
            savedInstanceState?.getString(KEY_CURRENT_SCREEN)
                ?: SCREEN_HOME
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

    private fun bindViews() {
        homeScreen = findViewById(R.id.homeScreen)
        languagesScreen = findViewById(R.id.languagesScreen)
        appearanceScreen = findViewById(R.id.appearanceScreen)
        settingsScreen = findViewById(R.id.settingsScreen)

        navHome = findViewById(R.id.navHome)
        navLanguages = findViewById(R.id.navLanguages)
        navAppearance = findViewById(R.id.navAppearance)
        navSettings = findViewById(R.id.navSettings)

        keyboardEnabledStatus =
            findViewById(R.id.keyboardEnabledStatus)
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            showScreen(SCREEN_HOME)
        }

        navLanguages.setOnClickListener {
            showScreen(SCREEN_LANGUAGES)
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
        val targetScreen = when (screen) {
            SCREEN_HOME,
            SCREEN_LANGUAGES,
            SCREEN_APPEARANCE,
            SCREEN_SETTINGS -> screen

            else -> SCREEN_HOME
        }

        currentScreen = targetScreen

        homeScreen.visibility =
            if (targetScreen == SCREEN_HOME) {
                View.VISIBLE
            } else {
                View.GONE
            }

        languagesScreen.visibility =
            if (targetScreen == SCREEN_LANGUAGES) {
                View.VISIBLE
            } else {
                View.GONE
            }

        appearanceScreen.visibility =
            if (targetScreen == SCREEN_APPEARANCE) {
                View.VISIBLE
            } else {
                View.GONE
            }

        settingsScreen.visibility =
            if (targetScreen == SCREEN_SETTINGS) {
                View.VISIBLE
            } else {
                View.GONE
            }

        navHome.setTextColor(
            if (targetScreen == SCREEN_HOME) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navLanguages.setTextColor(
            if (targetScreen == SCREEN_LANGUAGES) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navAppearance.setTextColor(
            if (targetScreen == SCREEN_APPEARANCE) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navSettings.setTextColor(
            if (targetScreen == SCREEN_SETTINGS) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navHome.isSelected = targetScreen == SCREEN_HOME
        navLanguages.isSelected = targetScreen == SCREEN_LANGUAGES
        navAppearance.isSelected = targetScreen == SCREEN_APPEARANCE
        navSettings.isSelected = targetScreen == SCREEN_SETTINGS
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
            if (isSelected) {
                activeColor
            } else {
                inactiveColor
            }
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
        private const val SCREEN_LANGUAGES = "languages"
        private const val SCREEN_APPEARANCE = "appearance"
        private const val SCREEN_SETTINGS = "settings"
    }
}
