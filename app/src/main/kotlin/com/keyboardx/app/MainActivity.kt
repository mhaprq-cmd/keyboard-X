package com.keyboardx.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var mainRoot: LinearLayout
    private lateinit var mainScrollView: ScrollView
    private lateinit var bottomNavigation: LinearLayout

    private lateinit var homeScreen: LinearLayout
    private lateinit var languagesScreen: LinearLayout
    private lateinit var appearanceScreen: LinearLayout
    private lateinit var settingsScreen: LinearLayout
    private lateinit var preferencesScreen: LinearLayout
    private lateinit var appLanguagesScreen: LinearLayout

    private lateinit var settingsPreferencesCard: LinearLayout
    private lateinit var settingsAppLanguagesCard: LinearLayout

    private lateinit var appLanguageArabicCard: LinearLayout
    private lateinit var appLanguageEnglishCard: LinearLayout
    private lateinit var appLanguageArabicSelected: TextView
    private lateinit var appLanguageEnglishSelected: TextView

    private lateinit var navHome: TextView
    private lateinit var navLanguages: TextView
    private lateinit var navAppearance: TextView
    private lateinit var navSettings: TextView
    private lateinit var keyboardEnabledStatus: TextView

    private lateinit var languageArabicCard: LinearLayout
    private lateinit var languageEnglishCard: LinearLayout
    private lateinit var languageArabicSelected: TextView
    private lateinit var languageEnglishSelected: TextView

    private var activePageView: View? = null
    private var currentScreen = SCREEN_HOME
    private var soundEnabled = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initializeDefaultLanguage()
        setupNavigation()
        setupKeyboardActions()
        setupShortcuts()
        setupLanguageSelection()
        setupAppLanguageSelection()
        setupSettingsActions()
        addInformationAndSupportLinks()
        refreshSoundStatus()

        showScreen(
            savedInstanceState?.getString(KEY_CURRENT_SCREEN)
                ?: SCREEN_HOME
        )

        refreshLanguageSelection()
        refreshAppLanguageSelection()

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

        if (::appLanguageArabicCard.isInitialized) {
            refreshAppLanguageSelection()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (activePageView != null) {
            closeInformationPage()
            return
        }

        when (currentScreen) {
            SCREEN_PREFERENCES,
            SCREEN_APP_LANGUAGES -> {
                showScreen(SCREEN_SETTINGS)
                return
            }

            SCREEN_SETTINGS,
            SCREEN_LANGUAGES,
            SCREEN_APPEARANCE -> {
                showScreen(SCREEN_HOME)
                return
            }
        }

        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_CURRENT_SCREEN, currentScreen)
        super.onSaveInstanceState(outState)
    }

    private fun bindViews() {
        mainRoot = findViewById(R.id.mainRoot)
        mainScrollView = mainRoot.getChildAt(0) as ScrollView
        bottomNavigation = findViewById(R.id.bottomNavigation)

        homeScreen = findViewById(R.id.homeScreen)
        languagesScreen = findViewById(R.id.languagesScreen)
        appearanceScreen = findViewById(R.id.appearanceScreen)
        settingsScreen = findViewById(R.id.settingsScreen)
        preferencesScreen = findViewById(R.id.preferencesScreen)
        appLanguagesScreen = findViewById(R.id.appLanguagesScreen)

        settingsPreferencesCard =
            findViewById(R.id.settingsPreferencesCard)

        settingsAppLanguagesCard =
            findViewById(R.id.settingsAppLanguagesCard)

        appLanguageArabicCard =
            findViewById(R.id.appLanguageArabicCard)

        appLanguageEnglishCard =
            findViewById(R.id.appLanguageEnglishCard)

        appLanguageArabicSelected =
            findViewById(R.id.appLanguageArabicSelected)

        appLanguageEnglishSelected =
            findViewById(R.id.appLanguageEnglishSelected)

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

    private fun initializeDefaultLanguage() {
        if (preferences.contains(KEY_ACTIVE_LANGUAGE)) {
            return
        }

        val deviceLanguage = resources.configuration.locales
            .get(0)
            .language

        val initialLanguage = if (deviceLanguage == "ar") {
            LANGUAGE_ARABIC
        } else {
            LANGUAGE_ENGLISH
        }

        preferences.edit()
            .putString(KEY_ACTIVE_LANGUAGE, initialLanguage)
            .apply()
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

        settingsPreferencesCard.setOnClickListener {
            showScreen(SCREEN_PREFERENCES)
        }

        settingsAppLanguagesCard.setOnClickListener {
            showScreen(SCREEN_APP_LANGUAGES)
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

    private fun setupAppLanguageSelection() {
        appLanguageArabicCard.setOnClickListener {
            setAppLanguage(LANGUAGE_ARABIC)
        }

        appLanguageEnglishCard.setOnClickListener {
            setAppLanguage(LANGUAGE_ENGLISH)
        }
    }

    private fun setAppLanguage(language: String) {
        val languageTag = when (language) {
            LANGUAGE_ARABIC -> "ar"
            else -> "en"
        }

        val currentLocales = AppCompatDelegate.getApplicationLocales()
        val currentTag = currentLocales.toLanguageTags()

        if (currentTag == languageTag) {
            refreshAppLanguageSelection()
            return
        }

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }

    private fun refreshAppLanguageSelection() {
        val configuredLocales =
            AppCompatDelegate.getApplicationLocales()

        val selectedLanguage = if (configuredLocales.isEmpty) {
            Locale.getDefault().language
        } else {
            configuredLocales[0]?.language ?: "en"
        }

        val isArabicSelected = selectedLanguage == "ar"

        appLanguageArabicSelected.visibility =
            if (isArabicSelected) View.VISIBLE else View.GONE

        appLanguageEnglishSelected.visibility =
            if (isArabicSelected) View.GONE else View.VISIBLE

        appLanguageArabicCard.isSelected = isArabicSelected
        appLanguageEnglishCard.isSelected = !isArabicSelected

        appLanguageArabicCard.alpha =
            if (isArabicSelected) 1.0f else 0.78f

        appLanguageEnglishCard.alpha =
            if (isArabicSelected) 0.78f else 1.0f

        appLanguageArabicCard.contentDescription =
            if (isArabicSelected) {
                "العربية، لغة واجهة التطبيق المحددة"
            } else {
                "العربية، اختيار لغة واجهة التطبيق"
            }

        appLanguageEnglishCard.contentDescription =
            if (!isArabicSelected) {
                "English, selected app interface language"
            } else {
                "English, choose app interface language"
            }
    }
    
    private fun setupSettingsActions() {
        val soundButton =
            findViewById<Button>(R.id.soundToggleButton)

        soundEnabled = preferences.getBoolean(
            KEY_SOUND_ENABLED,
            false
        )

        soundButton.setOnClickListener {
            soundEnabled = !soundEnabled

            preferences.edit()
                .putBoolean(KEY_SOUND_ENABLED, soundEnabled)
                .apply()

            refreshSoundStatus()
        }
    }

    private fun addInformationAndSupportLinks() {
        addSettingsLink(
            title = getString(R.string.about_title)
        ) {
            openInformationPage(
                R.layout.fragment_about,
                PAGE_ABOUT
            )
        }

        addSettingsLink(
            title = getString(R.string.support_page_title)
        ) {
            openInformationPage(
                R.layout.fragment_support,
                PAGE_SUPPORT
            )
        }

        addSettingsLink(
            title = getString(R.string.privacy_full_title)
        ) {
            openInformationPage(
                R.layout.fragment_privacy,
                PAGE_PRIVACY
            )
        }

        addSettingsLink(
            title = getString(R.string.terms_full_title)
        ) {
            openInformationPage(
                R.layout.fragment_terms,
                PAGE_TERMS
            )
        }

            private fun addInformationAndSupportLinks() {
        addSettingsLink(
            title = getString(R.string.about_title)
        ) {
            openInformationPage(
                R.layout.fragment_about,
                PAGE_ABOUT
            )
        }

        addSettingsLink(
            title = getString(R.string.support_page_title)
        ) {
            openInformationPage(
                R.layout.fragment_support,
                PAGE_SUPPORT
            )
        }

        addSettingsLink(
            title = getString(R.string.privacy_full_title)
        ) {
            openInformationPage(
                R.layout.fragment_privacy,
                PAGE_PRIVACY
            )
        }

        addSettingsLink(
            title = getString(R.string.terms_full_title)
        ) {
            openInformationPage(
                R.layout.fragment_terms,
                PAGE_TERMS
            )
        }
            }
    }

    private fun addSettingsLink(
        title: String,
        action: () -> Unit
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL

            setPadding(
                dp(16),
                0,
                dp(16),
                0
            )

            background = ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.app_card_background
            )

            isClickable = true
            isFocusable = true
            contentDescription = title

            setOnClickListener {
                action()
            }
        }

        val titleView = TextView(this).apply {
            text = title

            setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.app_text_primary
                )
            )

            textSize = 15f

            setTypeface(
                typeface,
                android.graphics.Typeface.BOLD
            )

            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        card.addView(
            titleView,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
            )
        )

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(56)
        ).apply {
            bottomMargin = dp(12)
        }

        settingsScreen.addView(card, params)
    }

    private fun openInformationPage(
        layoutResource: Int,
        pageName: String
    ) {
        closeInformationPage()

        val page = LayoutInflater.from(this)
            .inflate(layoutResource, mainRoot, false)

        activePageView = page
        currentScreen = pageName

        mainScrollView.visibility = View.GONE
        bottomNavigation.visibility = View.GONE

        val pageParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        mainRoot.addView(page, 0, pageParams)

        when (pageName) {
            PAGE_SUPPORT -> setupSupportPage(page)
            PAGE_ABOUT -> setupBackButton(page, R.id.aboutBackText)
            PAGE_PRIVACY -> setupBackButton(page, R.id.privacyBackText)
            PAGE_TERMS -> setupBackButton(page, R.id.termsBackText)
            PAGE_INTELLECTUAL_PROPERTY -> setupBackButton(
                page,
                R.id.intellectualPropertyBackText
            )
        }
    }

    private fun setupSupportPage(page: View) {
        setupBackButton(page, R.id.supportBackText)

        page.findViewById<TextView>(R.id.supportEmailAddress)
            ?.setOnClickListener {
                openSupportEmail()
            }

        page.findViewById<TextView>(R.id.supportReportIssueText)
            ?.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            this,
                            ReportIssueActivity::class.java
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                    showMessage(R.string.report_error_email_app_unavailable)
                }
            }
    }

    private fun setupBackButton(page: View, viewId: Int) {
        page.findViewById<TextView>(viewId)
            ?.setOnClickListener {
                closeInformationPage()
            }
    }

    private fun closeInformationPage() {
        val page = activePageView ?: return

        mainRoot.removeView(page)
        activePageView = null

        mainScrollView.visibility = View.VISIBLE
        bottomNavigation.visibility = View.VISIBLE

        showScreen(SCREEN_SETTINGS)
    }

    private fun openSupportEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:$SUPPORT_EMAIL")
        ).apply {
            putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.app_name) + " - Support"
            )
        }

        try {
            startActivity(emailIntent)
        } catch (_: ActivityNotFoundException) {
            showMessage(R.string.report_error_email_app_unavailable)
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

    private fun refreshSoundStatus() {
        val status = findViewById<TextView>(R.id.soundStatus)
        val button = findViewById<Button>(R.id.soundToggleButton)

        status.setText(
            if (soundEnabled) {
                R.string.sound_enabled
            } else {
                R.string.sound_disabled
            }
        )

        button.setText(
            if (soundEnabled) {
                R.string.sound_disabled
            } else {
                R.string.sound_enabled
            }
        )
    }

    private fun showFirstLaunchWelcomeDialogIfNeeded() {
        if (preferences.getBoolean(KEY_WELCOME_SHOWN, false)) {
            return
        }

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
            SCREEN_SETTINGS,
            SCREEN_PREFERENCES,
            SCREEN_APP_LANGUAGES -> screen

            else -> SCREEN_HOME
        }

        currentScreen = targetScreen

        homeScreen.visibility =
            if (targetScreen == SCREEN_HOME) View.VISIBLE else View.GONE

        languagesScreen.visibility =
            if (targetScreen == SCREEN_LANGUAGES) View.VISIBLE else View.GONE

        appearanceScreen.visibility =
            if (targetScreen == SCREEN_APPEARANCE) View.VISIBLE else View.GONE

        settingsScreen.visibility =
            if (targetScreen == SCREEN_SETTINGS) View.VISIBLE else View.GONE

        preferencesScreen.visibility =
            if (targetScreen == SCREEN_PREFERENCES) View.VISIBLE else View.GONE

        appLanguagesScreen.visibility =
            if (targetScreen == SCREEN_APP_LANGUAGES) View.VISIBLE else View.GONE

        val isMainTab = targetScreen in setOf(
            SCREEN_HOME,
            SCREEN_LANGUAGES,
            SCREEN_APPEARANCE,
            SCREEN_SETTINGS
        )

        bottomNavigation.visibility =
            if (isMainTab) View.VISIBLE else View.GONE

        navHome.setTextColor(
            if (targetScreen == SCREEN_HOME) activeColor else inactiveColor
        )

        navLanguages.setTextColor(
            if (targetScreen == SCREEN_LANGUAGES) activeColor else inactiveColor
        )

        navAppearance.setTextColor(
            if (targetScreen == SCREEN_APPEARANCE) activeColor else inactiveColor
        )

        navSettings.setTextColor(
            if (
                targetScreen == SCREEN_SETTINGS ||
                targetScreen == SCREEN_PREFERENCES ||
                targetScreen == SCREEN_APP_LANGUAGES
            ) {
                activeColor
            } else {
                inactiveColor
            }
        )

        navHome.isSelected = targetScreen == SCREEN_HOME
        navLanguages.isSelected = targetScreen == SCREEN_LANGUAGES
        navAppearance.isSelected = targetScreen == SCREEN_APPEARANCE
        navSettings.isSelected =
            targetScreen == SCREEN_SETTINGS ||
                targetScreen == SCREEN_PREFERENCES ||
                targetScreen == SCREEN_APP_LANGUAGES

        if (targetScreen == SCREEN_LANGUAGES) {
            refreshLanguageSelection()
        }

        if (targetScreen == SCREEN_APP_LANGUAGES) {
            refreshAppLanguageSelection()
        }

        mainScrollView.post {
            mainScrollView.scrollTo(0, 0)
        }
    }

    private fun refreshKeyboardStatus() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        val serviceName = "$packageName.ime.KeyboardIMEService"

        val isEnabled = inputMethodManager
            .enabledInputMethodList
            .any { inputMethod ->
                inputMethod.packageName == packageName &&
                    inputMethod.serviceName == serviceName
            }

        val selectedMethod = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )

        val isSelected =
            isEnabled && selectedMethod?.contains(serviceName) == true

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
        } catch (_: ActivityNotFoundException) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun showKeyboardPicker() {
        try {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager

            inputMethodManager.showInputMethodPicker()
        } catch (_: Exception) {
            showMessage(R.string.keyboard_not_enabled)
        }
    }

    private fun showMessage(messageResource: Int) {
        Toast.makeText(
            this,
            messageResource,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    companion object {
        private const val LANGUAGE_PREFERENCES =
            "keyboard_x_preferences"

        private const val KEY_ACTIVE_LANGUAGE = "active_language"
        private const val KEY_WELCOME_SHOWN = "welcome_dialog_shown"
        private const val KEY_SOUND_ENABLED = "sound_enabled"

        private const val LANGUAGE_ARABIC = "ARABIC"
        private const val LANGUAGE_ENGLISH = "ENGLISH"

        private const val KEY_CURRENT_SCREEN = "current_screen"

        private const val SCREEN_HOME = "home"
        private const val SCREEN_LANGUAGES = "languages"
        private const val SCREEN_APPEARANCE = "appearance"
        private const val SCREEN_SETTINGS = "settings"
        private const val SCREEN_PREFERENCES = "preferences"
        private const val SCREEN_APP_LANGUAGES = "app_languages"

        private const val PAGE_ABOUT = "page_about"
        private const val PAGE_SUPPORT = "page_support"
        private const val PAGE_PRIVACY = "page_privacy"
        private const val PAGE_TERMS = "page_terms"
        private const val PAGE_INTELLECTUAL_PROPERTY =
            "page_intellectual_property"

        private const val SUPPORT_EMAIL = "dmolgax@gmail.com"
    }
}
