package com.anti.rootadbcontroller;

import android.content.Context;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example test for Android components using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class AndroidComponentTest {

    @Test
    public void testContextNotNull() {
        Context context = RuntimeEnvironment.getApplication();
        assertNotNull("Application context should not be null", context);
    }

    @Test
    public void testTextViewCreation() {
        Context context = RuntimeEnvironment.getApplication();
        TextView textView = new TextView(context);
        textView.setText("Hello Robolectric");

        assertEquals("TextView text should be set correctly",
                "Hello Robolectric", textView.getText().toString());
    }
}
