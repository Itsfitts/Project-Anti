package com.anti.rootadbcontroller;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test for MainActivity that runs on an Android device or emulator.
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
    }

    @Test
    public void testRecyclerViewDisplayed() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAppTitleDisplayed() {
        // Check if the app title is displayed in the toolbar
        Espresso.onView(ViewMatchers.withText(R.string.app_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
