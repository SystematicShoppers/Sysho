package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.systematicshoppers.sysho.R
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.adapters.ListItemAdapter
import com.systematicshoppers.sysho.database.UserList

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.systematicshoppers.sysho.SyshoViewModel

private const val TAG = "SavedListsFragment"

/**
 * The SavedListsFragment class is a Fragment that displays a list of saved user lists in a RecyclerView.
 * It retrieves the user's saved lists from Firestore, populates the RecyclerView with the ListItemAdapter,
 * and provides functionality to delete a list.
 */
class SavedListsFragment : Fragment() {

    // Initialize an empty mutable list of UserList objects
    private var savedLists = mutableListOf<UserList>()
    // Declare a RecyclerView variable
    private lateinit var recyclerView: RecyclerView
    // Declare a TextView variable for the "No Saved Lists" message
    private lateinit var noSavedListsText: TextView
    // Initialize a ListItemAdapter with the savedLists data and a deleteList callback
    val adapter = ListItemAdapter(savedLists) { position ->
        deleteList(position)
    }
    private val viewModel: SyshoViewModel by activityViewModels()

    /**
     * Inflates the fragment_saved_lists layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_saved_lists layout XML file using the LayoutInflater,
        // attach it to the container ViewGroup (if provided), and store the resulting view in a variable
        val view = inflater.inflate(R.layout.fragment_saved_lists, container, false)
        // Return the created view
        return view
    }

    /**
     * onViewCreated initializes the RecyclerView, its layout manager, and loads the user's saved lists.
     * It also updates the "No Saved Lists" message visibility and handles the back button press to reset to the search fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.saved_lists_recycler_view)  // Initialize the RecyclerView for displaying saved lists
        noSavedListsText = view.findViewById<TextView>(R.id.no_saved_lists_text)        // Initialize the TextView for displaying "No Saved Lists" message
        recyclerView.layoutManager = LinearLayoutManager(requireContext())              // Set a LinearLayoutManager for the RecyclerView, which will organize the items in a vertical list

        // Load the user's saved lists and update the RecyclerView's adapter
        loadUserLists { userLists ->
            savedLists.clear()              // Clear any existing data in the savedLists list
            savedLists.addAll(userLists)    // Add the loaded userLists to the savedLists list
            recyclerView.adapter = adapter  // Set the adapter for the RecyclerView with the updated savedLists data
            adapter.notifyDataSetChanged()  // Notify the adapter that the dataset has changed, so the RecyclerView can be updated

            // Update the visibility of the "No Saved Lists" message
            if (savedLists.isEmpty()) {
                noSavedListsText.visibility = View.VISIBLE
            } else {
                noSavedListsText.visibility = View.GONE
            }
        }

        // Overload the back button press to reset the view to the search fragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val transaction = parentFragmentManager.beginTransaction()  // Begin a fragment transaction
                viewModel.setCurrentFragment("SearchFragment").toString()   // Set the current fragment in the ViewModel to "SearchFragment"
                transaction.replace(R.id.content, SearchFragment()).commit()// Replace the current fragment with a new instance of SearchFragment
            }
        }
        // Add the callback to the onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    // Broadcast receiver to clear the saved lists when the user logs out
    private val userLoggedOutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            clearLists()
        }
    }

    /**
     * Registers the broadcast receiver (userLoggedOutReceiver) when the fragment is resumed
     */
    override fun onResume() {
        super.onResume()
        // Get a LocalBroadcastManager instance with the current context and register the userLoggedOutReceiver with the "USER_LOGGED_OUT" intent filter
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(userLoggedOutReceiver, IntentFilter("USER_LOGGED_OUT"))
    }

    /**
     * Unregisters the broadcast receiver (userLoggedOutReceiver) when the fragment is paused
     */
    override fun onPause() {
        super.onPause()
        // Get a LocalBroadcastManager instance with the current context and unregister the userLoggedOutReceiver
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(userLoggedOutReceiver)
    }

    /**
     * Clears the saved lists, notifies the adapter about the data set change,
     * and updates the visibility of the "No Saved Lists" message.
     */
    private fun clearLists() {
        savedLists.clear()
        adapter.notifyDataSetChanged()
        checkEmptyList()
    }

    /**
     * Function to check if the saved lists are empty and updates the visibility of the "No Saved Lists" message
     */
    private fun checkEmptyList() {
        if (savedLists.isEmpty()) {
            noSavedListsText.visibility = View.VISIBLE
        } else {
            noSavedListsText.visibility = View.GONE
        }
    }


    /**
     * This function retrieves the user's saved lists from Firestore
     * and calls the provided callback with the list of UserList objects.
     * It also handles the case when a user is not signed in by calling the callback with an empty list.
     */
    private fun loadUserLists(callback: (List<UserList>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser   // Get the current user
        // Check if the user is not null (signed-in)
        if (user != null) {
            val userId = user.uid                           // Get the user's ID
            val database = FirebaseFirestore.getInstance()  // Get a reference to the Firestore database
            // Get a reference to the user's lists collection (their search history) in Firestore
            val userListsRef = database.collection("Users").document(userId).collection("lists")

            // Get the user's saved lists from Firestore and call the callback with the results
            userListsRef.get().addOnSuccessListener { querySnapshot ->
                // Map the querySnapshot documents to UserList objects
                val userLists = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    // Convert the documentSnapshot to a UserList object
                    val userList = documentSnapshot.toObject(UserList::class.java)
                    // Set the UserList object's ID to the documentSnapshot's ID
                    userList?.id = documentSnapshot.id
                    userList
                }
                // Call the callback with the retrieved UserList objects
                callback(userLists)
            }.addOnFailureListener { exception ->
                // Show a Toast message to inform the user that loading the lists has failed
                Toast.makeText(requireContext(), "Failed to load lists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Execute the callback with an empty list for not signed-in users
            callback(emptyList())
            noSavedListsText.visibility = View.VISIBLE
        }
    }

    /**
     * This function deletes a list at the specified position from Firestore
     * and updates the RecyclerView's adapter accordingly.
     * It also updates the visibility of the "No Saved Lists" message.
     */
    private fun deleteList(position: Int) {
        val userList = savedLists[position]                 // Get the UserList object at the specified position
        val user = FirebaseAuth.getInstance().currentUser   // Get the current user

        // Check if the user is not null (signed-in)
        if (user != null) {
            val userId = user.uid                           // Get the user's ID
            val database = FirebaseFirestore.getInstance()  // Get a reference to the Firestore database
            // Get a reference to the user's lists collection (their search history) in Firestore
            val userListsRef = database.collection("Users").document(userId).collection("lists")

            // Delete the list from Firestore using the UserList's ID
            userListsRef.document(userList.id).delete().addOnSuccessListener {
                // Remove the deleted list from the savedLists data and notify the RecyclerView's adapter of the change
                savedLists.removeAt(position)
                recyclerView.adapter?.notifyDataSetChanged()

                // Update the visibility of the "No Saved Lists" message
                if (savedLists.isEmpty()) {
                    noSavedListsText.visibility = View.VISIBLE
                } else {
                    noSavedListsText.visibility = View.GONE
                }

                // Show a Toast message to inform the user that the list has been deleted successfully
                Toast.makeText(requireContext(), "List deleted successfully", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener { exception ->
                // Show a Toast message to inform the user that the list deletion has failed
                Toast.makeText(
                    requireContext(),
                    "Failed to delete list: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
