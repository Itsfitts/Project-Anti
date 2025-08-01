package com.anti.rootadbcontroller

import android.content.Context
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Example test for Android components using Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AndroidComponentTest {

    @Test
    fun testContextNotNull() {
        val context: Context = RuntimeEnvironment.getApplication()
        assertNotNull("Application context should not be null", context)
    }

    @Test
    fun testTextViewCreation() {
        val context: Context = RuntimeEnvironment.getApplication()
        val textView = TextView(context)
        textView.text = "Hello Robolectric"

        assertEquals(
            "TextView text should be set correctly",
            "Hello Robolectric",
            textView.text.toString(),
        )
    }
}
