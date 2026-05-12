package com.example.tvoya_zhdulya;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import com.example.tvoya_zhdulya.ui.PersonsActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainUiTest {
    @Rule
    public ActivityScenarioRule<PersonsActivity> activityRule =
            new ActivityScenarioRule<>(PersonsActivity.class);

    @Test
    public void testWelcomeMessageVisible() {
        Espresso.onView(ViewMatchers.withText("Кого ты ждешь?"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.btnAddPerson))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}