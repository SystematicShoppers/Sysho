package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.systematicshoppers.sysho.R
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.adapters.ListItemAdapter
import com.systematicshoppers.sysho.database.UserList

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment_saved_lists layout
        val view = inflater.inflate(R.layout.fragment_saved_lists, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView and set its layout manager
        recyclerView = view.findViewById<RecyclerView>(R.id.saved_lists_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load the user's saved lists and update the RecyclerView's adapter
        loadUserLists { userLists ->
            savedLists.clear()
            savedLists.addAll(userLists)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
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
