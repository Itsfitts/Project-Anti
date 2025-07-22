package com.anti.rootadbcontroller;

import android.view.View;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

/**
 * Instrumented test for MainActivity that runs on an Android device or emulator.
 * These tests verify the UI elements and interactions of the main activity.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMainActivityLaunches() {
        // Verify that the activity launches successfully
        // This is a basic test to ensure the activity can be started
        System.out.println("[DEBUG_LOG] MainActivity launched successfully");
    }

    @Test
    public void testAppTitleDisplayed() {
        // Check if the app title is displayed in the toolbar
        Espresso.onView(ViewMatchers.withText("Root ADB Controller"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        System.out.println("[DEBUG_LOG] App title is displayed");
    }

    @Test
    public void testRootStatusIndicatorDisplayed() {
        // Check if the root status indicator is displayed
        // This could be either "Rooted" or "Not Rooted" depending on the device
        Espresso.onView(ViewMatchers.withText(containsString("Rooted")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the Shizuku status indicator is displayed
        Espresso.onView(ViewMatchers.withText(containsString("Shizuku")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        System.out.println("[DEBUG_LOG] Root and Shizuku status indicators are displayed");
    }

    @Test
    public void testFeatureListDisplayed() {
        // Check if the feature list is displayed by verifying some of the feature titles
        Espresso.onView(ViewMatchers.withText(containsString("Keylogging")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Scroll down to see more features
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .perform(ViewActions.swipeUp());

        // Check for a feature that should be visible after scrolling
        Espresso.onView(ViewMatchers.withText(containsString("Screenshot")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        System.out.println("[DEBUG_LOG] Feature list is displayed and scrollable");
    }

    @Test
    public void testFeatureInfoButtonOpensDialog() {
        // Find a feature card and click its info button
        Espresso.onView(allOf(
                withContentDescription("More information"),
                withParent(withParent(withParent(withText(containsString("Keylogging")))))
            ))
            .perform(ViewActions.click());

        // Verify that the explanation dialog is displayed
        Espresso.onView(ViewMatchers.withText("Close"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Close the dialog
        Espresso.onView(ViewMatchers.withText("Close"))
            .perform(ViewActions.click());

        System.out.println("[DEBUG_LOG] Feature info button opens explanation dialog");
    }

    @Test
    public void testAutomationSettingsButtonOpensDialog() {
        // Click the automation settings button (More Vert icon)
        Espresso.onView(withContentDescription("Automation Settings"))
            .perform(ViewActions.click());

        // Verify that the automation settings dialog is displayed
        Espresso.onView(ViewMatchers.withText("Automation Settings"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Close the dialog
        Espresso.onView(ViewMatchers.withText("Close"))
            .perform(ViewActions.click());

        System.out.println("[DEBUG_LOG] Automation settings button opens settings dialog");
    }

    @Test
    public void testScrollThroughFeatureList() {
        // Scroll through the entire feature list to ensure all items can be displayed
        // First scroll down multiple times
        for (int i = 0; i < 5; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(ViewActions.swipeUp());
        }

        // Then scroll back up
        for (int i = 0; i < 5; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .perform(ViewActions.swipeDown());
        }

        // Verify we can still see the first feature
        Espresso.onView(ViewMatchers.withText(containsString("Keylogging")))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        System.out.println("[DEBUG_LOG] Successfully scrolled through the entire feature list");
    }
}
