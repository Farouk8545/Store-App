package com.example.sportsstore.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sportsstore.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val _user = MutableLiveData<FirebaseUser?>()

    // LiveData to observe the user data
    val user: LiveData<FirebaseUser?>
        get() = _user

    init {
        // Check if user is already signed in
        _user.value = auth.currentUser
    }

    // Initialize Google Sign-In options
    fun initGoogleSignInClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update LiveData with signed-in user information
                    _user.value = auth.currentUser
                } else {
                    Toast.makeText(getApplication(), "Sign in failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            // Update LiveData with null to indicate sign out
            _user.value = null
        }
    }

    fun createAccount(email: String, password: String, phoneNumber: String, address: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val firestore = FirebaseFirestore.getInstance()
                    val userRef = auth.currentUser?.let {
                        firestore.collection("users").document(it.uid)
                    }
                    val hashmap = hashMapOf(
                        "email" to email,
                        "displayName" to user.value?.displayName,
                        "phoneNumber" to phoneNumber,
                        "address" to address
                    )
                    userRef?.set(hashmap)
                    _user.value = auth.currentUser
                }else Toast.makeText(getApplication(), "Failed to create account!", Toast.LENGTH_SHORT).show()
            }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    _user.value = auth.currentUser
                }else Toast.makeText(getApplication(), "Wrong email or password!", Toast.LENGTH_SHORT).show()
            }
    }

    fun addPurchase(product: String, price: Double, date: Timestamp, state: String, paymentMethod: String, imageUrl: String?, id: String){
        val firestore = FirebaseFirestore.getInstance()
        val purchaseRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("purchases").document(id)
        }

        val purchaseData = hashMapOf(
            "product" to product,
            "price" to price,
            "date" to date,
            "state" to state,
            "paymentMethod" to paymentMethod,
            "imageUrl" to imageUrl
        )

        purchaseRef?.set(purchaseData)?.addOnSuccessListener {
            val query = firestore.collection("sports_shirts").whereEqualTo("id", id)
            query.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    for(document in task.result){
                        document.reference.update("salesCount", FieldValue.increment(1))
                    }
                }
            }
        }?.addOnFailureListener{e ->
            Log.d(TAG, e.toString())
        }
    }

    fun addFavorite(product: String, price: Double, imageUrl: String?, description: String?, id: String){
        val firestore = FirebaseFirestore.getInstance()
        val favoriteRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("favorites").document(id)
        }

        val favoriteData = hashMapOf(
            "product" to product,
            "price" to price,
            "imageUrl" to imageUrl,
            "description" to description
        )

        favoriteRef?.set(favoriteData)?.addOnSuccessListener {
            val query = firestore.collection("sports_shirts").whereEqualTo("id", id)
            query.get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    for(document in task.result){
                        document.reference.update("inFavourite", true)
                    }
                }
            }
        }?.addOnFailureListener{e ->
            Log.d(TAG, e.toString())
        }
    }

    fun deleteFavorite(id: String){
        val firestore = FirebaseFirestore.getInstance()
        val favoriteRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("favorites").document(id)
        }
        favoriteRef?.delete()?.addOnSuccessListener {
            val query = firestore.collection("sports_shirts").whereEqualTo("id", id)
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        document.reference.update("inFavourite", false)
                    }
                }
            }
        }?.addOnFailureListener{ e ->
            Log.d(TAG, e.toString())
        }
    }

    suspend fun inFavorite(id: String): Boolean{
        val firestore = FirebaseFirestore.getInstance()
        val favoriteRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("favorites").document(id)
        }

        return try {
            val documentSnapshot = favoriteRef?.get()?.await()
            documentSnapshot?.exists() ?: false
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            false
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}