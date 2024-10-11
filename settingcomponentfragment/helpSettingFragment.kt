package com.example.sportsstore.fragments.settingcomponentfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sportsstore.R

class HelpSupportFragment : Fragment(R.layout.fragment_help_setting) {

    private lateinit var viewFAQsButton: Button
    private lateinit var contactSupportButton: Button
    private lateinit var submitFeedbackButton: Button
    private lateinit var termsButton: Button
    private lateinit var privacyPolicyButton: Button

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewFAQsButton = view.findViewById(R.id.btn_view_faqs)
        contactSupportButton = view.findViewById(R.id.btn_contact_support)
        submitFeedbackButton = view.findViewById(R.id.btn_submit_feedback)
        termsButton = view.findViewById(R.id.btn_terms_conditions)
        privacyPolicyButton = view.findViewById(R.id.btn_privacy_policy)

        viewFAQsButton.setOnClickListener {
            Toast.makeText(requireContext(), "Navigating to FAQs", Toast.LENGTH_SHORT).show()
            // Implement navigation to FAQs
            // we don't have faqs now so we just show toast
        }

        contactSupportButton.setOnClickListener {
            sendSupportEmail()
        }

        submitFeedbackButton.setOnClickListener {
            showFeedbackDialog()
        }

        termsButton.setOnClickListener {
            openWebPage("https://www..com/terms")
        }

        privacyPolicyButton.setOnClickListener {
            openWebPage("https://www..com/privacy")
        }
    }

    private fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@example.com")
            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFeedbackDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Submit Feedback")
        builder.setMessage("Enter your feedback:")

        val input = androidx.appcompat.widget.AppCompatEditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val feedback = input.text.toString()
            if (feedback.isNotEmpty()) {
                // Handle feedback submission (e.g., send to server)
                Toast.makeText(requireContext(), "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Feedback cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No browser app found", Toast.LENGTH_SHORT).show()
        }
    }
}
