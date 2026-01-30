package com.example.mediqorog

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mediqorog.view.LoginActivity
import com.example.mediqorog.view.DashboardActivity
import com.example.mediqorog.view.RegistrationActivity
import com.example.mediqorog.view.ForgotPasswordActivity
import com.example.mediqorog.view.AdminDashboardActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testEmailField_acceptsInput() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")
    }

    @Test
    fun testPasswordField_acceptsInput() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
    }

    @Test
    fun testLoginButton_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("loginButton")
            .performClick()
    }

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")
        composeRule.onNodeWithTag("passwordField")
            .performTextInput("password123")
        composeRule.onNodeWithTag("loginButton")
            .performClick()

        // Wait for navigation
        Thread.sleep(2000)

        intended(hasComponent(DashboardActivity::class.java.name))
    }

    @Test
    fun testGoogleSignInButton_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("googleSignInButton")
            .performClick()
    }

    @Test
    fun testRegisterLink_navigatesToSignup() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("registerLink")
            .performClick()

        intended(hasComponent(RegistrationActivity::class.java.name))
    }

    @Test
    fun testForgotPasswordLink_navigatesToForgotPassword() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("forgotPasswordLink")
            .performClick()

        intended(hasComponent(ForgotPasswordActivity::class.java.name))
    }

    @Test
    fun testRememberMeCheckbox_toggles() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("rememberMeCheckbox")
            .performClick()
    }

    @Test
    fun testPasswordVisibilityToggle_clickable() {
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("passwordVisibilityToggle")
            .performClick()
    }
}