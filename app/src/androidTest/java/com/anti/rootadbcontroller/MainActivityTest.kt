package com.anti.rootadbcontroller

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for MainActivity that runs on an Android device or emulator.
 * These tests verify the UI elements and interactions of the main activity.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMainActivityLaunches() {
        // Verify that the activity launches successfully
        println("[DEBUG_LOG] MainActivity launched successfully")
    }

    @Test
    fun testAppTitleDisplayed() {
        // Check if the app title is displayed in the toolbar
        onView(withText("Root ADB Controller"))
            .check(matches(isDisplayed()))

        println("[DEBUG_LOG] App title is displayed")
    }

    @Test
    fun testRootStatusIndicatorDisplayed() {
        // Check if the root status indicator is displayed
        onView(withText(containsString("Root")))
            .check(matches(isDisplayed()))

        // Check if the Shizuku status indicator is displayed
        onView(withText(containsString("Shizuku")))
            .check(matches(isDisplayed()))

        println("[DEBUG_LOG] Root and Shizuku status indicators are displayed")
    }

    @Test
    fun testFeatureListDisplayed() {
        // Check if the feature list is displayed by verifying some of the feature titles
        onView(withText(containsString("Keylogging")))
            .check(matches(isDisplayed()))

        // Scroll down to see more features
        onView(withId(R.id.recyclerView))
            .perform(swipeUp())

        // Check for a feature that should be visible after scrolling
        onView(withText(containsString("Screenshot")))
            .check(matches(isDisplayed()))

        println("[DEBUG_LOG] Feature list is displayed and scrollable")
    }

    @Test
    fun testFeatureInfoButtonOpensDialog() {
        // Find a feature card and click its info button
        onView(
            allOf(
                withContentDescription("More information"),
                withParent(withParent(withParent(withText(containsString("Keylogging")))))
            )
        ).perform(click())

        // Verify that the explanation dialog is displayed
        onView(withText("Close"))
            .check(matches(isDisplayed()))

        // Close the dialog
        onView(withText("Close")).perform(click())

        println("[DEBUG_LOG] Feature info dialog opens and closes correctly")
    }
}

