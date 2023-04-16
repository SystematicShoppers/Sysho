// ResultsFragmentTest.kt
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.systematicshoppers.sysho.activities.MainActivity
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.database.QueryItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultsFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkViewsDisplayedInResultsFragment() {
        val itemsToAdd = listOf("apples", "bananas", "oranges")

        // Add items to the searchFragmentRecyclerView and create a queryList
        val queryList: MutableList<QueryItem> = mutableListOf()
        for (item in itemsToAdd) {
            onView(withId(R.id.search_bar)).perform(ViewActions.click())
            onView(withId(R.id.search_bar)).perform(ViewActions.typeText(item))
            onView(withId(R.id.search_bar)).perform(ViewActions.pressImeActionButton())
            onView(withId(R.id.search_bar)).perform(ViewActions.closeSoftKeyboard())
            queryList.add(QueryItem(item, false, 1))
        }
        // Go to Results Page
        onView(withId(R.id.shopBtn)).perform(ViewActions.click())

        onView(withId(R.id.resultsRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.filterDistance)).check(matches(isDisplayed()))
        onView(withId(R.id.filterPrice)).check(matches(isDisplayed()))
        onView(withId(R.id.filterTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun displayDistance() {
        val itemsToAdd = listOf("apples", "bananas")

        // Add items to the searchFragmentRecyclerView and create a queryList
        val queryList: MutableList<QueryItem> = mutableListOf()
        for (item in itemsToAdd) {
            onView(withId(R.id.search_bar)).perform(ViewActions.click())
            onView(withId(R.id.search_bar)).perform(ViewActions.typeText(item))
            onView(withId(R.id.search_bar)).perform(ViewActions.pressImeActionButton())
            onView(withId(R.id.search_bar)).perform(ViewActions.closeSoftKeyboard())
            queryList.add(QueryItem(item, false, 1))
        }
        onView(withId(R.id.shopBtn)).perform((ViewActions.click()))
        Thread.sleep(2000)

        // Click the distance button when the Results page is displayed
        onView(withId(R.id.filterDistance)).perform(ViewActions.click())

        onView(withId(R.id.resultsRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.filterDistance)).check(matches(isDisplayed()))
        onView(withId(R.id.filterPrice)).check(matches(isDisplayed()))
        onView(withId(R.id.filterTextView)).check(matches(isDisplayed()))
    }
}
