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
import androidx.appcompat.app.AlertDialog
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

    private lateinit var languageArabicCard: LinearLayout
    private lateinit var languageEnglishCard: LinearLayout
    private lateinit var languageArabicSelected: TextView
    private lateinit var languageEnglishSelected: TextView

    private val preferences by lazy {
        getSharedPreferences(
            LANGUAGE_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    private val activeColor: Int
        get() = ContextCompat.getColor(this, R.color.app_accent)

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
        setupLanguageSelection()

        showScreen(
            savedInstanceState?.getString(KEY_CURRENT_SCREEN)
                ?: SCREEN_HOME
        )

        refreshLanguageSelection()

        if (savedInstanceState == null) {
            showFirstLaunchWelcomeDialogIfNeeded()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::keyboardEnabledStatus.isInitialized) {
            refreshKeyboardStatus()
        }

        if (::languageArabicCard.isInitialized) {
            refreshLanguageSelection()
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

        languageArabicCard =
            findViewById(R.id.languageArabicCard)

        languageEnglishCard =
            findViewById(R.id.languageEnglishCard)

        languageArabicSelected =
            findViewById(R.id.languageArabicSelected)

        languageEnglishSelected =
            findViewById(R.id.languageEnglishSelected)
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

    private fun setupLanguageSelection() {
        languageArabicCard.setOnClickListener {
            savePreferredLanguage(LANGUAGE_ARABIC)
            refreshLanguageSelection()
        }

        languageEnglishCard.setOnClickListener {
            savePreferredLanguage(LANGUAGE_ENGLISH)
            refreshLanguageSelection()
        }
    }

    private fun savePreferredLanguage(language: String) {
        preferences.edit()
            .putString(KEY_ACTIVE_LANGUAGE, language)
            .apply()
    }

    private fun refreshLanguageSelection() {
        val selectedLanguage = preferences.getString(
            KEY_ACTIVE_LANGUAGE,
            LANGUAGE_ENGLISH
        ) ?: LANGUAGE_ENGLISH

        val isArabicSelected =
            selectedLanguage == LANGUAGE_ARABIC

        languageArabicSelected.visibility =
            if (isArabicSelected) View.VISIBLE else View.GONE

        languageEnglishSelected.visibility =
            if (isArabicSelected) View.GONE else View.VISIBLE

        languageArabicCard.isSelected = isArabicSelected
        languageEnglishCard.isSelected = !isArabicSelected

        languageArabicCard.alpha =
            if (isArabicSelected) 1.0f else 0.78f

        languageEnglishCard.alpha =
            if (isArabicSelected) 0.78f else 1.0f

        languageArabicCard.contentDescription =
            if (isArabicSelected) {
                "العربية، اللغة المحددة"
            } else {
                "العربية"
            }

        languageEnglishCard.contentDescription =
            if (!isArabicSelected) {
                "الإنجليزية، اللغة المحددة"
            } else {
                "الإنجليزية"
            }
    }

    private fun showFirstLaunchWelcomeDialogIfNeeded() {
        if (preferences.getBoolean(KEY_WELCOME_SHOWN, false)) {
            return
        }

        // Save the flag before showing the dialog to prevent repeats.
        preferences.edit()
            .putBoolean(KEY_WELCOME_SHOWN, true)
            .apply()

        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(R.string.activation_instructions)
            .setPositiveButton(R.string.enable_keyboard) { _, _ ->
                openKeyboardSettings()
            }
            .setNeutralButton(R.string.select_keyboard) { _, _ ->
                showKeyboardPicker()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .setCancelable(true)
            .show()
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
            if (targetScreen == SCREEN_HOME) activeColor
            else inactiveColor
        )

        navLanguages.setTextColor(
            if (targetScreen == SCREEN_LANGUAGES) activeColor
            else inactiveColor
        )

        navAppearance.setTextColor(
            if (targetScreen == SCREEN_APPEARANCE) activeColor
            else inactiveColor
        )

        navSettings.setTextColor(
            if (targetScreen == SCREEN_SETTINGS) activeColor
            else inactiveColor
        )

        navHome.isSelected = targetScreen == SCREEN_HOME
        navLanguages.isSelected = targetScreen == SCREEN_LANGUAGES
        navAppearance.isSelected = targetScreen == SCREEN_APPEARANCE
        navSettings.isSelected = targetScreen == SCREEN_SETTINGS

        if (targetScreen == SCREEN_LANGUAGES) {
            refreshLanguageSelection()
        }
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
        private const val LANGUAGE_PREFERENCES =
            "keyboard_x_preferences"

        private const val KEY_ACTIVE_LANGUAGE =
            "active_language"

        private const val KEY_WELCOME_SHOWN =
            "welcome_dialog_shown"

        private const val LANGUAGE_ARABIC = "ARABIC"
        private const val LANGUAGE_ENGLISH = "ENGLISH"

        private const val KEY_CURRENT_SCREEN = "current_screen"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_LANGUAGES = "languages"
        private const val SCREEN_APPEARANCE = "appearance"
        private const val SCREEN_SETTINGS = "settings"
    }
}
