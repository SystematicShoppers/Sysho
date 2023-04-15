package com.systematicshoppers.sysho.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.systematicshoppers.sysho.R

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.systematicshoppers.sysho.adapters.ListItemAdapter
import com.systematicshoppers.sysho.database.FirebaseUtils
import com.systematicshoppers.sysho.database.ListItem
import com.systematicshoppers.sysho.database.UserList

private const val TAG = "SavedListsFragment"

class SavedListsFragment : Fragment() {

    private var savedLists = mutableListOf<UserList>()
    val adapter = ListItemAdapter(savedLists)

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

        loadUserLists { userLists ->
            savedLists.clear()
            savedLists.addAll(userLists)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadUserLists(callback: (List<UserList>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val database = FirebaseFirestore.getInstance()
            val userListsRef = database.collection("Users").document(userId).collection("lists")

            userListsRef.get().addOnSuccessListener { querySnapshot ->
                val userLists = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    val items = documentSnapshot.toObject(UserList::class.java)?.items ?: listOf()
                    UserList(documentSnapshot.id, items)
                }
                callback(userLists)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load lists: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


//    private fun loadUserLists(callback: (List<ListItem>) -> Unit) {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            val userId = user.uid
//            val database = FirebaseFirestore.getInstance()
//            val userListsRef = database.collection("Users").document(userId).collection("lists")
//
//            userListsRef.get().addOnSuccessListener { querySnapshot ->
//                val userLists = querySnapshot.documents.flatMap { documentSnapshot ->
//                    Log.d(TAG, "Document data: ${documentSnapshot.data}") // Log the document data
//                    val items = documentSnapshot.get("items") as? List<Map<String, Any>>
//                    items?.mapNotNull { itemMap ->
//                        Log.d(TAG, "Item map: $itemMap") // Log the item map
//                        val entry = itemMap["entry"] as? String ?: ""
//                        val quantity = (itemMap["quantity"] as? Long)?.toInt() ?: 0
//                        ListItem(entry, quantity)
//                    } ?: emptyList()
//                }
//                callback(userLists)
//            }.addOnFailureListener { exception ->
//                Toast.makeText(requireContext(), "Failed to load lists: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}
