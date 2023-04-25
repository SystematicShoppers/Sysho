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


/**
 * The LogInActivity is an Android Activity that handles the user authentication process using FirebaseUI Authentication.
 * It enables users to sign in with their Google accounts and manages the sign-in and sign-out flow within the app.
 * It is responsible for updating the user interface to reflect the current authentication state,
 * handling the result of the sign-in process, and sending a local broadcast to inform the SavedListFragment when a user has logged out.
 */
class LogInActivity: AppCompatActivity() {

    // Declare a binding variable
    private lateinit var binding: ActivityLoginBinding
    // Initialize the SyshoViewModel
    private val viewModel: SyshoViewModel by viewModels()
    // Declare a variable to hold a reference to the user's email
    private lateinit var userEmailTextView: TextView
    // Create a callback for the sign-in intent
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    /**
     * The entry point of the LogInActivity where the layout is inflated,
     * the view bindings are set, the onClickListeners for the login and sign out buttons are configured,
     * and the onBackPressedDispatcher is overridden to return to the MainActivity (SearchFragment) when the back button is pressed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the onClickListener for the loginButton
        binding.loginButton.setOnClickListener {
            createSignInIntent()
        }

        // Set the onClickListener for the signOutButton
        binding.signOutButton.setOnClickListener {
            signOut()
        }

        // Bind the userEmailTextView and update the display
        userEmailTextView = binding.userEmailTextView
        updateUserEmailDisplay()

        // Override the back button to return to the MainActivity(SearchFragment)
        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@LogInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Updates the email display for the logged-in user or shows "Not signed in" if the user is not logged in.
     * Also toggles the visibility of the login and sign out buttons.
     */
    private fun updateUserEmailDisplay() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmailTextView = binding.userEmailTextView
        val loginButton = binding.loginButton
        val signOutButton = binding.signOutButton
        // If the user is signed in, display the user's email and hide the login button
        if (user != null) {
            userEmailTextView.text = getString(R.string.user_display, user.email)
            loginButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
        }
        // If the user is not signed in, display "Not signed in" and hide the sign out button
        else {
            userEmailTextView.text = getString(R.string.not_signed_in)
            loginButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

    /**
     * Creates and launches a sign-in intent using FirebaseUI with Google.
     * The signInLauncher then handles the result of the sign-in attempt.
    */
    private fun createSignInIntent() {
        // Choose authentication providers(Google)
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

    /**
     * Handles the sign-in result. If the sign-in is successful, it updates the email display.
     * If the sign-in is not successful, it checks for the error code in the IDP response and displays
     * an error message for the given response.
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        // Get the IDP response from the result
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            updateUserEmailDisplay() //update the TextView email display
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            if (response == null) {
                // User canceled the sign-in flow using the back button
                Toast.makeText(this, "You did not sign in", Toast.LENGTH_SHORT).show()
            } else {
                // Get the error code from the IDP response
                val errorCode = response.getError()?.errorCode
                when (errorCode) {
                    ErrorCodes.NO_NETWORK -> {      // No network connection error
                        Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {   // Unknown error occurred

                        Toast.makeText(this, "Unknown error occurred", Toast.LENGTH_SHORT).show()
                    }
                    ErrorCodes.PROVIDER_ERROR -> {  // Error with the selected provider
                        Toast.makeText(this, "Error with the selected provider", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ErrorCodes.ANONYMOUS_UPGRADE_MERGE_CONFLICT -> {    // Anonymous account upgrade failed due to merge conflict
                        Toast.makeText(
                            this,
                            "Anonymous account upgrade failed due to merge conflict",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.DEVELOPER_ERROR -> {     // Developer error occurred
                        Toast.makeText(
                            this,
                            "A sign-in operation couldn't be completed due to a developer error.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.EMAIL_MISMATCH_ERROR -> {// Email mismatch error
                        Toast.makeText(
                            this,
                            "The email address of the account being linked already exists in the user's account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.ERROR_USER_DISABLED -> { // User's account is disabled
                        Toast.makeText(
                            this,
                            "The user's account has been disabled.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED -> {  // User cancelled the update of Play Services
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
                            // An API Exception occurred
                            Toast.makeText(
                                this,
                                "An API Exception occurred: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // An unknown error occurred
                            Toast.makeText(this, "Unhandled error occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Signs out the currently authenticated user from Firebase Authentication, updates the email display,
     * and sends a local broadcast to inform the SavedListsFragment that the user has logged out.
     */
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
}

//Sources: https://firebase.google.com/docs/auth/android/firebaseui