package com.example.kotlinmessenger2.ui.auth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.kotlinmessenger2.R
import org.junit.Assert.*
import org.junit.Test

class LoginActivityTest {

    @Test
    fun testLoginToRegister() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)

        //onView(withId(R.id.email_edittext_login)).check(matches(isDisplayed()))
        //onView(withId(R.id.password_edittext_login)).check(matches(isDisplayed()))
        onView(withId(R.id.back_to_register_textview)).check(matches(isDisplayed()))
        onView(withId(R.id.back_to_register_textview)).perform(click())
        //onView(withId(R.id.activity_register_parent)).check(matches(isDisplayed()))
    }
}