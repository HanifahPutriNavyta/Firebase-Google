package com.example.praktikum

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.common.api.ApiException


class LoginActivity2 : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "LoginActivity"
    private val RC_SIGN_IN = 123 // Kode untuk request sign-in

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Inisialisasi Firebase Auth

        setContent {
            LoginScreen(
                onLoginSuccess = { username ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                },
                onGoogleSignInClick = { startGoogleSignIn() } // Tangani klik sign-in Google
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Periksa apakah pengguna sudah masuk (non-null) dan perbarui UI sesuai.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun startGoogleSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(R.string.your_web_client_id.toString()) // Ganti dengan Client ID Anda
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        Identity.getSignInClient(this).beginSignIn(signInRequest)
            .addOnSuccessListener { result ->

                Toast.makeText(this, "Google sign-in success", Toast.LENGTH_SHORT).show()
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0,
                        null
                    )

            }

            .addOnFailureListener {
                Toast.makeText(this, "Google sign-in failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
//            val idToken = credential.googleIdToken
//            when {
//                idToken != null -> {
//                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                    auth.signInWithCredential(firebaseCredential)
//                        .addOnCompleteListener(this) { task ->
//                            if (task.isSuccessful) {
//                                Log.d(TAG, "signInWithCredential:success")
//                                val user = auth.currentUser
//                                updateUI(user)
//                            } else {
//                                Log.w(TAG, "signInWithCredential:failure", task.exception)
//                                updateUI(null)
//                            }
//                        }
//                }
//                else -> {
//                    Log.d(TAG, "No ID token!")
//                }
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = auth.currentUser
                                        updateUI(user)
                                    } else {
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                        updateUI(null)
                                    }
                                }
                        }
                        else -> {
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Sign-in failed with exception: ${e.message}")
                    updateUI(null)
                }
            } else {
                Log.w(TAG, "Sign-in failed: result code is not OK")
            }
        }
    }



    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Welcome, ${user.email}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(onClick = {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val username = task.result?.user?.email ?: ""
                        onLoginSuccess(username)
                    } else {
                        errorMessage = "Login failed: ${task.exception?.message}"
                    }
                }
        }) {
            Text("Login")
        }

        Button(onClick = onGoogleSignInClick) {
            Text("Sign in with Google")
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = {}, onGoogleSignInClick = {})
}
