package com.example.sportsstore.adminFragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsstore.R
import com.example.sportsstore.adapters.AdminColorChoiceAdapter
import com.example.sportsstore.databinding.FragmentAdminAddRemoveProductBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AdminAddRemoveProductFragment : Fragment() {
    private lateinit var binding: FragmentAdminAddRemoveProductBinding
    private lateinit var adapter: AdminColorChoiceAdapter
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminAddRemoveProductBinding.inflate(inflater, container, false)

        adapter = AdminColorChoiceAdapter()
        binding.colorsRv.adapter = adapter
        binding.colorsRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the URI of the selected image
                selectedImageUri= result.data?.data
                selectedImageUri?.let {
                    // Use the URI, for example, set it in an ImageView
                    binding.imageView.setImageURI(it)
                }
            }
        }

        binding.addColorButton.setOnClickListener {
            val color = binding.colorEt.text.toString()
            if (color.isNotEmpty()) {
                adapter.setData(color)
                binding.colorEt.text.clear()
            }
        }

        binding.imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.addProductButton.setOnClickListener {
            if (selectedImageUri != null && getSelectedSizes().isNotEmpty() && adapter.getData().isNotEmpty()) {
                uploadImageToFirebaseStorage(selectedImageUri!!)
            } else {
                Toast.makeText(context, "Please select an image, sizes and colors", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backbut.setOnClickListener{
            findNavController().navigate(R.id.action_adminAddRemoveProductFragment_to_adminMainPageFragment)
        }

        return binding.root
    }

    private fun getSelectedSizes(): List<String>{
        val list = mutableListOf<String>()
        if(binding.smallCheckbox.isChecked) list.add(binding.smallCheckbox.text.toString())
        if(binding.mediumCheckbox.isChecked) list.add(binding.mediumCheckbox.text.toString())
        if(binding.largeCheckbox.isChecked) list.add(binding.largeCheckbox.text.toString())
        if(binding.xlargeCheckbox.isChecked) list.add(binding.xlargeCheckbox.text.toString())
        if(binding.xxlargeCheckbox.isChecked) list.add(binding.xxlargeCheckbox.text.toString())
        if(binding.xxxlargeCheckbox.isChecked) list.add(binding.xxxlargeCheckbox.text.toString())
        return list
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        // Create a unique reference for the image in Firebase Storage
        val imageRef = storageRef.child("products/${System.currentTimeMillis()}.jpg")

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Image uploaded successfully
                // Now get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Now save the image URL to Firestore
                    saveProductToFirestore(imageUrl)
                }
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(context, "Upload Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProductToFirestore(imageUrl: String) {
        val selectedId = binding.radioGroup.checkedRadioButtonId

        if(selectedId == -1){
            Toast.makeText(context, "Please, select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedId)

        // Create a product object
        val product = hashMapOf(
            "productName" to binding.nameEt.text.toString(),
            "price" to binding.priceEt.text.toString().toDouble(),
            "description" to binding.descriptionEt.text.toString(),
            "imageUrl" to imageUrl, // Store the image URL
            "year" to binding.yearEt.text.toString(),
            "id" to binding.idEt.text.toString(),
            "colors" to adapter.getData(),
            "sizes" to getSelectedSizes(),
            "category" to selectedRadioButton.text.toString(),
            "createdAt" to Timestamp.now(),
            "salesCount" to 0,
            // Add other product details if necessary
        )

        // Save the product to Firestore
        firestore.collection("sports_shirts")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_adminAddRemoveProductFragment_to_adminMainPageFragment)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add product: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}