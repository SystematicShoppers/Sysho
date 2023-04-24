package com.systematicshoppers.sysho.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.systematicshoppers.sysho.R
import com.systematicshoppers.sysho.SyshoViewModel
import com.systematicshoppers.sysho.databinding.ActivityLoginBinding
import androidx.activity.addCallback



class LogInActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SyshoViewModel by viewModels()
    private lateinit var userEmailTextView: TextView //variable to hold a reference to the email text view

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            createSignInIntent()
        }

        binding.signOutButton.setOnClickListener {
            signOut()
        }

        userEmailTextView = binding.userEmailTextView
        updateUserEmailDisplay()

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@LogInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUserEmailDisplay() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmailTextView = binding.userEmailTextView
        val loginButton = binding.loginButton
        val signOutButton = binding.signOutButton
        if (user != null) {
            userEmailTextView.text = getString(R.string.user_display, user.email)
            loginButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
        } else {
            userEmailTextView.text = getString(R.string.not_signed_in)
            loginButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            updateUserEmailDisplay() //update the textview email display
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            if (response == null) {
                Toast.makeText(this, "You did not sign in", Toast.LENGTH_SHORT).show()
            } else {
                val errorCode = response.getError()?.errorCode
                when (errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                    }
                    ErrorCodes.PROVIDER_ERROR -> {
                        Toast.makeText(this, "Error with the selected provider", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT -> {
                        Toast.makeText(
                            this,
                            "Anonymous account upgrade failed due to merge conflict",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.DEVELOPER_ERROR -> {
                        Toast.makeText(
                            this,
                            "A sign-in operation couldn't be completed due to a developer error.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.EMAIL_MISMATCH_ERROR -> {
                        Toast.makeText(
                            this,
                            "The email address of the account being linked already exists in the user's account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.ERROR_USER_DISABLED -> {
                        Toast.makeText(
                            this,
                            "The user's account has been disabled.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> {
                        Toast.makeText(
                            this,
                            "A required update to Play Services was cancelled by the user.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        // Handle other errors, including ApiException, here
                        val error = response.getError()
                        if (error is ApiException) {
                            Toast.makeText(
                                this,
                                "An API Exception occurred: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(this, "Unhandled error occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                updateUserEmailDisplay()
                // Send a local broadcast to inform the SavedListsFragment that the user has logged out
                val intent = Intent("USER_LOGGED_OUT")
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
    }


    // Used to delete the currently authenticated user's account from Firebase Authentication.
    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // Handle the completion of the account deletion process
            }
    }
}