package com.systematicshoppers.sysho.activities

import android.content.ContentValues
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.systematicshoppers.sysho.LocationViewModel
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.SearchList
import com.systematicshoppers.sysho.databinding.ActivityMainBinding
import com.systematicshoppers.sysho.fragments.ResultsFragment
import com.systematicshoppers.sysho.fragments.SavedListsFragment
import com.systematicshoppers.sysho.fragments.SearchFragment
import com.systematicshoppers.sysho.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var geocoder: Geocoder
    private lateinit var currentFragmentName: String
    private val viewModel: SyshoViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    /**
      This init reads the metadata from Firebase for the product database.
      It returns a list of possible item names for the search autocomplete feature.
     **/
    init {
        getAutoCompleteList()
    }
    /**
        Location updates will begin here once the app is started.
        The user should be prompted for permission.
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        val toolbar = binding.toolbar
        setContentView(viewRoot)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        locationViewModel.startLocationUpdates(this)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        navController = navHostFragment.navController
        currentFragmentName = navController.currentDestination.toString()
        loadSettings()
    }

    /**Gives the drop down menu the style of nav_menu.xml which includes all the big app destinations.**/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    /**Sets what happens when a destination is selected from the drop down. There's a bit of a special case here
     * because the app also employs other forms of navigation. Any time a navigation event happens, the viewModel
     * updates with the name of the fragment at the top of the stack (this is not the same as FragmentManager tags!!)
     * In the drop down menu, the name becomes the value of currentFragmentName which then acts as an error check to determine
     * if a fragment being view is also being selected, and prevent a reload on user error.**/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frame = R.id.content
        currentFragmentName = viewModel.currentFragment.value.toString()
        val fragmentManager = supportFragmentManager
        return when (item.itemId) {
            R.id.searchFragment -> {
                if (currentFragmentName != "SearchFragment") {
                    val newFragment = SearchFragment()
                    val transaction = fragmentManager.beginTransaction()
                    if (fragmentManager.findFragmentByTag("search_fragment") == null) {
                        transaction.addToBackStack("search_fragment")
                    }
                    transaction.replace(frame, newFragment)
                        .commit()

                    currentFragmentName = viewModel.setCurrentFragment("SearchFragment").toString()
                    true
                } else true
            }
            R.id.resultsFragment -> {
                if (currentFragmentName != "ResultsFragment") {
                    val newFragment = ResultsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    if (fragmentManager.findFragmentByTag("results_fragment") == null) {
                        transaction.addToBackStack("results_fragment")
                    }
                    transaction.replace(frame, newFragment)
                        .commit()
                    currentFragmentName = viewModel.setCurrentFragment("ResultsFragment").toString()
                    true
                } else true
            }
            R.id.settingsFragment -> {
                    val newFragment = SettingsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    transaction.replace(frame, newFragment).commit()
                    currentFragmentName = viewModel.setCurrentFragment("SettingsFragment").toString()
                    true
            }
            R.id.logInActivity -> {
                val intent = Intent(this, LogInActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.savedListsFragment -> {
                if (currentFragmentName != "SavedListsFragment") {
                    val newFragment = SavedListsFragment()
                    val transaction = fragmentManager.beginTransaction()
                    if (fragmentManager.findFragmentByTag("saved_lists_fragment") == null) {
                        transaction.addToBackStack("saved_lists_fragment")
                    }
                    transaction.replace(frame, newFragment)
                        .commit()
                    currentFragmentName = viewModel.setCurrentFragment("SavedListsFragment").toString()
                    true
                } else true
            }
            // add more cases for each menu item
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**This is an override method for the NavController that Android recommends adding to AppCompat
     * activities that are acting as a home page for the navigation graph/ **/
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    /**This is the Firebase call to get the search list results from metadata (see init block at top)**/
    private fun getAutoCompleteList() {
            FirebaseUtils().fireStoreDatabase.collection("metadata").document("searchList")
                .get()
                .addOnSuccessListener { list ->
                    try {
                        Log.d(ContentValues.TAG, "Data retrieved: ${list.data}")
                        val listObject: SearchList? = list.toObject(SearchList::class.java)
                        viewModel.setAutoComplete(listObject?.searchList!!)

                    } catch (e: Exception) {
                        Log.d(
                            ContentValues.TAG,
                            "Could not retrieve metadata searchList ${list.id}"
                        )
                    }
                }
    }

    /**This will load default settings to match the displayed default distance in the settings.
     * This code can be modified to cater to individual user data and settings in the future!
     * **/
    private fun loadSettings() {
        viewModel.setDistanceFilter(20.0)
    }

}