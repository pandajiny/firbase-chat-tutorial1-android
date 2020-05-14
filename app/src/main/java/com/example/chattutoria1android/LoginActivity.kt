package com.example.chattutoria1android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    private var RC_GOOGLE_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

// firebase auth init
        auth = Firebase.auth

//        Define Google SignIn Option
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestId().build()

//        Google SignIn Client Initial with gso
        googleSignInClient = GoogleSignIn.getClient(this, gso)

//        set Onclick Listener to GoogleSingInButton
        findViewById<SignInButton>(R.id.google_sign_in_button).setOnClickListener {
            doSignInWithGoogle()
        }
    }

    override fun onStart() {
        super.onStart()
//        Check user already Logged in
        if (auth.currentUser != null) {
            toast("User Already Logged In with " + auth.currentUser?.email)
//            if firebase auth already exist, force the user will be returned to the main page
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun doSignInWithGoogle() {
//        Make Intent for Google SignIn Activity with sign in client assigned with custom gso
        val googleSignInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
//            get Signed In Account
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
//                try to get account
                val account = task.getResult(ApiException::class.java)!!
//                after getting information of google account, auth with firebase
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
//               If Error occurred, errorText will display Error message
                findViewById<TextView>(R.id.errorText).text = e.message
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
//            if successfully firebase logged in,
            if (task.isSuccessful) {
//                get User Information
                val user = auth.currentUser
                toast("Firebase Login!" + user!!.email)
//                Go back to the main activity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
//                if firebase login failed,
                toast("firebase Login Failed")
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }


}
