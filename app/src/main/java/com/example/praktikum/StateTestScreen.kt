package com.example.praktikum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.praktikum.ui.theme.PraktikumTheme

@Composable
fun StateTestScreen(viewModel: StateTestViewModel, username: String){
    val name by viewModel.name.observeAsState(initial = "")
    val name2 by viewModel.name2.observeAsState(initial = "")


    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(text = "Logged in as: $username", style = TextStyle(fontSize = 24.sp))

        MyText(name, name2)
        MyTextField(name, onNameChange = {
            viewModel.onNameUpdate(it)
        })
        MyTextField2(name2, onNameChange = {
            viewModel.onName2Update(it)
        })

        Button(onClick = {
            viewModel.saveUserData(name, name2)
        }) {
            Text(text = "Save Data")
        }
    }
}

@Composable
fun MyText(name : String, name2 : String){
    Text(
        text = "Hello $name $name2",
        style = TextStyle(fontSize = 30.sp)
    )
}

@Composable
fun MyTextField(name : String, onNameChange : (String)->Unit){
    OutlinedTextField(
        value = name,
        onValueChange = {
            onNameChange(it)
        },
        label = { Text(text = "Enter name") })
}

@Composable
fun MyTextField2(name2 : String, onNameChange : (String)->Unit){
    OutlinedTextField(
        value = name2,
        onValueChange = {
            onNameChange(it)
        },
        label = { Text(text = "Enter name") })
}