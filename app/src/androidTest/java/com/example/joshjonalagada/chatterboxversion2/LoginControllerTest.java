package com.example.joshjonalagada.chatterboxversion2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

// automatic login check
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginControllerTest {

    @Rule
    public ActivityTestRule<LoginController> mActivityTestRule = new ActivityTestRule<>(LoginController.class);

    @Test
    public void loginControllerTest() {
        onView(withId(R.id.usernameField)).perform(replaceText("Josh"));
        onView(withId(R.id.passwordField)).perform(replaceText("JJ151767"));
        onView(withId(R.id.submitButton)).perform(click());
    }
}
