package com.example.praktikum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.SetOptions

class StateTestViewModel : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _name2 = MutableLiveData<String>()
    val name2: LiveData<String> = _name2

    //Firebase Firestore instance
    private val db = FirebaseFirestore.getInstance()

    init {
        loadUserData()
    }

    fun onNameUpdate(newName: String) {
        _name.value = newName
        saveUserData("name", newName)
    }

    fun onName2Update(newName2: String) {
        _name2.value = newName2
        saveUserData("name2", newName2)
    }

    fun saveUserData(name: String, name2: String) {
        val userData = mapOf(
            "name" to name,
            "name2" to name2
        )
        db.collection("users").document("user1")
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving data", e)
            }
    }

    private fun loadUserData() {
        db.collection("users").document("user1")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _name.value = document.getString("name") ?: ""
                    _name2.value = document.getString("name2") ?: ""
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user dara", e)
            }
    }
}