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
            Toast.makeText(requireContext(),
                getString(R.string.navigating_to_faqs), Toast.LENGTH_SHORT).show()
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
            openWebPage("https://www.google.com/terms")
        }

        privacyPolicyButton.setOnClickListener {
            openWebPage("https://www.google.com/privacy")
        }
    }

    private fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("hemdanmohamedhany@gmail.com")
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
        builder.setTitle(getString(R.string.submit_feedback))
        builder.setMessage(getString(R.string.enter_your_feedback))

        val input = androidx.appcompat.widget.AppCompatEditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.submit)) { dialog, _ ->
            val feedback = input.text.toString()
            if (feedback.isNotEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.thank_you_for_your_feedback), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.feedback_cannot_be_empty), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
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
