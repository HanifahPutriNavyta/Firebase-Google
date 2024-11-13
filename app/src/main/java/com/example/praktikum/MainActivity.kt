package com.example.praktikum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.praktikum.ui.theme.PraktikumTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        val viewModel = ViewModelProvider(this)[StateTestViewModel::class.java]
        val username = intent.getStringExtra("username") ?: "Guest"

        setContent {
            StateTestScreen(viewModel, username)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StateTestScreenPreview() {
    val viewModel = StateTestViewModel()
    StateTestScreen(viewModel = viewModel, username = "PreviewUser")

}