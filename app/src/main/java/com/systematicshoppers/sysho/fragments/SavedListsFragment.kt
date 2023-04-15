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
import com.google.firebase.database.FirebaseDatabase
import com.systematicshoppers.sysho.adapters.ListItemAdapter
import com.systematicshoppers.sysho.database.ListItem


class SavedListsFragment : Fragment() {

    private var savedLists = mutableListOf<ListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved_lists, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.saved_lists_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ListItemAdapter(savedLists)
        recyclerView.adapter = adapter

        loadUserLists { userLists ->
            savedLists.clear()
            savedLists.addAll(userLists)
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadUserLists(callback: (List<ListItem>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseDatabase.getInstance()
            val userListsRef = database.getReference("users").child(userId).child("lists")

            userListsRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val userLists = mutableListOf<ListItem>()
                    dataSnapshot.children.forEach { childSnapshot ->
                        childSnapshot.children.forEach { listItemSnapshot ->
                            val listItem = listItemSnapshot.getValue(ListItem::class.java)
                            listItem?.let { userLists.add(it) }
                        }
                    }
                    callback(userLists)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load lists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}