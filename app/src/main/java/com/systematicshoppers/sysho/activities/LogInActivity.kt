package com.systematicshoppers.sysho.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.databinding.ActivityLoginBinding

class LogInActivity: FirebaseUIActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SyshoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        binding.loginBtn.setOnClickListener {

        }

    }
}