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
import com.example.sportsstore.models.CartModel
import com.example.sportsstore.models.FavoriteModel
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
    private val firestore = FirebaseFirestore.getInstance()
    private val _cartItems = MutableLiveData<List<CartModel>>()
    val cartItems: LiveData<List<CartModel>> get() = _cartItems
    private val _favItems = MutableLiveData<List<FavoriteModel>>()
    val favItems: LiveData<List<FavoriteModel>> get() = _favItems


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
                        "address" to address,
                        "role" to "customer"
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

    private fun addPurchase(product: String, price: Double, date: Timestamp, state: String, paymentMethod: String, imageUrl: String?, id: String, color: String, size: String){
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
            "imageUrl" to imageUrl,
            "color" to color,
            "size" to size
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

    fun fetchFavoriteItems(){
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("favorites")
            .get()
            .addOnSuccessListener { documents ->
                val items = documents.toObjects(FavoriteModel::class.java)
                _favItems.postValue(items)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error fetching cart items: ${e.message}")
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
            "description" to description,
            "id" to id
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

    fun fetchCartItems() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("purchases_cart")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.d(TAG, "Error listening to cart items: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val items = snapshots.toObjects(CartModel::class.java)
                    _cartItems.postValue(items)
                }
            }
    }

    fun addPurchaseCart(productName: String, price: Double, imageUrl: String?, description: String?, id: String, color: String, size: String) {
        val firestore = FirebaseFirestore.getInstance()
        val purchaseRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("purchases_cart").document(id)
        }

        val purchaseData = hashMapOf(
            "productName" to productName,
            "price" to price,
            "imageUrl" to imageUrl,
            "description" to description,
            "id" to id,
            "color" to color,
            "size" to size
        )

        purchaseRef?.set(purchaseData)?.addOnSuccessListener {
            Log.d(TAG, "Purchase added successfully.")
            fetchCartItems()
        }?.addOnFailureListener { e ->
            Log.d(TAG, "Error adding purchase: ${e.message}")
        }
    }

    fun deletePurchaseCart(id: String) {
        val firestore = FirebaseFirestore.getInstance()
        val purchaseRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("purchases_cart").document(id)
        }

        purchaseRef?.delete()?.addOnSuccessListener {
            Log.d(TAG, "Purchase deleted successfully.")
            fetchCartItems()
        }?.addOnFailureListener { e ->
            Log.d(TAG, "Error deleting purchase: ${e.message}")
        }
    }



    suspend fun purchaseCartExists(id: String): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val purchaseRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("purchases_cart").document(id)
        }

        return try {
            val documentSnapshot = purchaseRef?.get()?.await()
            documentSnapshot?.exists() ?: false
        } catch (e: Exception) {
            Log.d(TAG, "Error checking purchase existence: ${e.message}")
            false
        }
    }

    fun addOrder(productName: String, year:String?, price: Double, imageUrl: String?, description: String?, id: String, selectedColor: String, selectedSize: String, amount: Int, address: String, email: String, phoneNumber: String, paymentMethod: String){
        val firestore = FirebaseFirestore.getInstance()
        val orderRef = firestore.collection("orders")

        val orderData = hashMapOf(
            "productName" to productName,
            "year" to year,
            "price" to price,
            "imageUrl" to imageUrl,
            "description" to description,
            "id" to id,
            "selectedColor" to selectedColor,
            "selectedSize" to selectedSize,
            "amount" to amount,
            "address" to address,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "paymentMethod" to paymentMethod,
            "state" to "undelivered",
            "date" to Timestamp.now()
        )

        orderRef.add(orderData).addOnSuccessListener {
            addPurchase(productName, price, Timestamp.now(), "undelivered", paymentMethod, imageUrl, id, selectedColor, selectedSize)
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        val firestore = FirebaseFirestore.getInstance()
        val purchaseRef = auth.currentUser?.let {
            firestore.collection("users").document(it.uid).collection("purchases_cart").document(itemId)
        }

        purchaseRef?.update("quantity", newQuantity)?.addOnSuccessListener {
            Log.d(TAG, "Quantity updated successfully for itemId: $itemId")
        }?.addOnFailureListener { e ->
            Log.d(TAG, "Error updating quantity for itemId: $itemId, error: ${e.message}")
        }
    }


    fun deleteAllCartItems() {
        val userId = auth.currentUser?.uid ?: return

        // Reference to the user's cart collection
        val cartCollectionRef = firestore.collection("users").document(userId).collection("purchases_cart")

        cartCollectionRef.get().addOnSuccessListener { snapshot ->
            // Iterate through all cart items and delete each one
            for (document in snapshot.documents) {
                document.reference.delete().addOnSuccessListener {
                    Log.d(TAG, "Cart item ${document.id} deleted successfully.")
                }.addOnFailureListener { e ->
                    Log.d(TAG, "Error deleting cart item: ${e.message}")
                }
            }
            // After all deletions, refresh the cart items list
            fetchCartItems()
        }.addOnFailureListener { e ->
            Log.d(TAG, "Error fetching cart items for deletion: ${e.message}")
        }
    }
}