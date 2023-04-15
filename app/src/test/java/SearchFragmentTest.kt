import com.systematicshoppers.sysho.database.ListItem
import com.systematicshoppers.sysho.database.QueryItem
import com.systematicshoppers.sysho.fragments.SearchFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchFragmentTest {

    private lateinit var searchFragment: SearchFragment

    @Before
    fun setUp() {
        searchFragment = SearchFragment()
    }

    @Test
    fun queryIsNotListed_queryNotInList_returnsTrue() {
        val queryList = mutableListOf(
            QueryItem("Item 1", false, 1),
            QueryItem("Item 2", false, 1),
            QueryItem("Item 3", false, 1)
        )

        searchFragment.queryList.addAll(queryList)
        val result = searchFragment.queryIsNotListed("Item 4")
        assertEquals(true, result)
    }

    @Test
    fun queryIsNotListed_queryInList_returnsFalse() {
        val queryList = mutableListOf(
            QueryItem("Item 1", false, 1),
            QueryItem("Item 2", false, 1),
            QueryItem("Item 3", false, 1)
        )

        searchFragment.queryList.addAll(queryList)
        val result = searchFragment.queryIsNotListed("Item 2")
        assertEquals(false, result)
    }


    @Test
    fun getProductList_returnsCorrectList() {
        val searchFragment = SearchFragment()
        val queryItems = mutableListOf(
            QueryItem("Product 1", false, 1),
            QueryItem("Product 2", false, 1),
            QueryItem("Product 3", false, 1)
        )
        val expectedList = mutableListOf("Product 1", "Product 2", "Product 3")


        val actualList = mutableListOf<ListItem>()
        val resultList = searchFragment.getProductList(queryItems, actualList)

        assertEquals(expectedList, resultList)
    }

}
