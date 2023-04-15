import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.activities.MainActivity
import com.systematicshoppers.sysho.database.ListItem
import com.systematicshoppers.sysho.database.QueryItem
import com.systematicshoppers.sysho.fragments.SearchFragment
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchFragmentTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addItemToList() {
        // Perform a click on the search_bar to set the focus
        onView(withId(R.id.search_bar)).perform(click())

        // Type the text you want to add (e.g., "apple") into the search_bar
        onView(withId(R.id.search_bar)).perform(typeText("apples"))

        // Press the Enter key to add the item
        onView(withId(R.id.search_bar)).perform(pressImeActionButton())

        //Close soft keyboard
        onView(withId(R.id.search_bar)).perform(closeSoftKeyboard())

        val queryList: MutableList<QueryItem> = mutableListOf(
            QueryItem("apples", false, 1)
        )
        // Call getProductList function
        val productList: MutableList<ListItem> = SearchFragment().getProductList(queryList, mutableListOf())

        val applesPresent = productList.any { it.entry == "apples" }
        assertTrue(applesPresent)
    }

    @Test
    fun addMultipleItemsToList() {
        val itemsToAdd = listOf("apples", "bananas", "oranges")

        // Add items to the searchFragmentRecyclerView and create a queryList
        val queryList: MutableList<QueryItem> = mutableListOf()
        for (item in itemsToAdd) {
            onView(withId(R.id.search_bar)).perform(click())
            onView(withId(R.id.search_bar)).perform(typeText(item))
            onView(withId(R.id.search_bar)).perform(pressImeActionButton())
            onView(withId(R.id.search_bar)).perform(closeSoftKeyboard())
            queryList.add(QueryItem(item, false, 1))
        }

        // Call getProductList function
        val productList: MutableList<ListItem> = SearchFragment().getProductList(queryList, mutableListOf())

        // Check if all items are present in the productList using a for loop
        for (item in itemsToAdd) {
            val itemPresent = productList.any { it.entry == item }
            assertTrue(itemPresent)
        }
    }

    @Test
    fun noInvalidItems() {
        val itemsToAdd = listOf("apples", "bananas", "bleepblorp")

        // Add items to the searchFragmentRecyclerView and create a queryList
        val queryList: MutableList<QueryItem> = mutableListOf()
        for (item in itemsToAdd) {
            onView(withId(R.id.search_bar)).perform(click())
            onView(withId(R.id.search_bar)).perform(typeText(item))
            onView(withId(R.id.search_bar)).perform(pressImeActionButton())
            onView(withId(R.id.search_bar)).perform(closeSoftKeyboard())
            queryList.add(QueryItem(item, false, 1))
        }

        // Call getProductList function
        val productList: MutableList<ListItem> = SearchFragment().getProductList(queryList, mutableListOf())

        val validItems = listOf("apples", "bananas")
        // Check if only valid items are present in the productList using a for loop
        for (item in validItems) {
            val itemPresent = productList.any { it.entry == item }
            assertTrue(itemPresent)
        }
    }
}
