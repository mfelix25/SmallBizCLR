package com.chatapp.data.registration

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.chatapp.data.Database
import com.chatapp.ui.registration.RegistrationView

class RegistrationPresenter constructor(private val registrationView: RegistrationView) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createAccount(email: String, password: String, displayName: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid.toString()
                        val databaseReference = FirebaseDatabase.getInstance().reference.child(Database.usersNode).child(userId)
                        updateDatabase(displayName, databaseReference)
                    }
                }
                .addOnFailureListener {
                    registrationView.registrationError(it.message.toString())
                }
    }

    private fun updateDatabase(displayName: String, databaseReference: DatabaseReference) {
        val userObject = HashMap<String, String>()
        userObject[Database.displayNameNode] = displayName
        userObject[Database.statusNode] = "Hello"
        userObject[Database.imageNode] = "default"
        userObject[Database.thumbImageNode] = "default"
        databaseReference.setValue(userObject).addOnCompleteListener { task: Task<Void> ->
            updateDatabaseResult(task.isSuccessful, displayName)
        }
    }

    private fun updateDatabaseResult(success: Boolean, displayName: String) {
        if (success) {
            registrationView.updateDatabaseSuccess(displayName)
        } else {
            registrationView.updateDatabaseFail()
        }
    }
}