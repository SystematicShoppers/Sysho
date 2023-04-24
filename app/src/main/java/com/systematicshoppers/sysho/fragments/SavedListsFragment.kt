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
    // Initialize a ListItemAdapter with the savedLists data and a deleteList callback
    val adapter = ListItemAdapter(savedLists) { position ->
        deleteList(position)
    }
    private val viewModel: SyshoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_saved_lists layout
        val view = inflater.inflate(R.layout.fragment_saved_lists, container, false)
        return view
    }

    private lateinit var noSavedListsText: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView and set its layout manager
        recyclerView = view.findViewById<RecyclerView>(R.id.saved_lists_recycler_view)
        noSavedListsText = view.findViewById<TextView>(R.id.no_saved_lists_text)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load the user's saved lists and update the RecyclerView's adapter
        loadUserLists { userLists ->
            savedLists.clear()
            savedLists.addAll(userLists)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            // Update the visibility of the "No Saved Lists" message
            if (savedLists.isEmpty()) {
                noSavedListsText.visibility = View.VISIBLE
            } else {
                noSavedListsText.visibility = View.GONE
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val transaction = parentFragmentManager.beginTransaction()
                viewModel.setCurrentFragment("SearchFragment").toString()
                transaction.replace(R.id.content, SearchFragment()).commit()
            }
        }
        // Add the callback to the onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private val userLoggedOutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            clearLists()
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(userLoggedOutReceiver, IntentFilter("USER_LOGGED_OUT"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(userLoggedOutReceiver)
    }

    private fun clearLists() {
        savedLists.clear()
        adapter.notifyDataSetChanged()
        checkEmptyList()
    }

    private fun checkEmptyList() {
        if (savedLists.isEmpty()) {
            noSavedListsText.visibility = View.VISIBLE
        } else {
            noSavedListsText.visibility = View.GONE
        }
    }



    // Function to load the user's saved lists from Firestore
    private fun loadUserLists(callback: (List<UserList>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseFirestore.getInstance()
            val userListsRef = database.collection("Users").document(userId).collection("lists")

            // Get the user's saved lists from Firestore and call the callback with the results
            userListsRef.get().addOnSuccessListener { querySnapshot ->
                val userLists = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    val userList = documentSnapshot.toObject(UserList::class.java)
                    userList?.id = documentSnapshot.id
                    userList
                }
                callback(userLists)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load lists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Execute the callback with an empty list for not signed-in users
            callback(emptyList())
            noSavedListsText.visibility = View.VISIBLE
        }
    }

    // Function to delete a list at the specified position
    private fun deleteList(position: Int) {
        val userList = savedLists[position]
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseFirestore.getInstance()
            val userListsRef = database.collection("Users").document(userId).collection("lists")

            // Delete the list from Firestore and update the RecyclerView's adapter
            userListsRef.document(userList.id).delete().addOnSuccessListener {
                savedLists.removeAt(position)
                recyclerView.adapter?.notifyDataSetChanged()

                // Update the visibility of the "No Saved Lists" message
                if (savedLists.isEmpty()) {
                    noSavedListsText.visibility = View.VISIBLE
                } else {
                    noSavedListsText.visibility = View.GONE
                }

                Toast.makeText(requireContext(), "List deleted successfully", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete list: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
