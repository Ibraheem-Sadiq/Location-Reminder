package com.udacity.project4.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    lateinit var signInLauncher: ActivityResultLauncher<Intent?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser != null) {
            continyeAppFlow()
            return


        } else {
             signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { res ->
                this.onSignInResult(res)
            }
            setContentView(R.layout.activity_authentication)
            findViewById<Button>(R.id.btn_login).setOnClickListener {
                signIn()
            }
        }

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            continyeAppFlow()
        } else
            Log.d("TAG", "onSignInResult: ${result.idpResponse?.error?.localizedMessage}")
    }

    fun signIn() {


        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
            providers
        ).build()
        signInLauncher.launch(signInIntent)
    }

    fun continyeAppFlow() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()

    }

}