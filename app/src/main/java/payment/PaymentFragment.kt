package payment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sportsstore.R
import com.hbb20.CountryCodePicker
import com.paypal.android.sdk.payments.*

import org.json.JSONException
import java.math.BigDecimal

class PaymentFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentFragment()
        private const val PAYPAL_REQUEST_CODE = 123
    }

    private lateinit var numItemEditText: EditText
    private lateinit var totalCostEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var emailSignUpButton: ImageView
    private lateinit var btnPayment: Button

    // PayPal configuration
    private val clientId = "AY8KF77cO2qwMGh7Vx7Sk7rTPR6T2eJ6smH1r-GUQXAqo-AhqoBUVf2NHl999oHZN-rnlSfdiGzw4Eou"
    private lateinit var configuration: PayPalConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PayPal configuration
        configuration = PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(clientId)

        // Initialize PayPal service
        val intent = Intent(requireContext(), PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration)
        requireActivity().startService(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        // Initialize views
        numItemEditText = view.findViewById(R.id.numItemEditText)
        totalCostEditText = view.findViewById(R.id.passwordEditText3)
        addressEditText = view.findViewById(R.id.addressEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        countryCodePicker = view.findViewById(R.id.countryCodePicker)
        emailSignUpButton = view.findViewById(R.id.emailSignUpButton)
        btnPayment = view.findViewById(R.id.btnpayment)

        // Set up listeners
        numItemEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateTotalCost()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        emailSignUpButton.setOnClickListener {
            if (validateInputs()) {
                // Proceed with purchase
                getPayment()
            }
        }

        btnPayment.setOnClickListener {
            getPayment()
        }

        return view
    }

    private fun calculateTotalCost() {
        val numItems = numItemEditText.text.toString().toIntOrNull() ?: 0
        // Assume a fixed price per item of $10 for this example
        val totalCost = numItems * 10
        totalCostEditText.setText("$$totalCost")
    }

    private fun validateInputs(): Boolean {
        if (numItemEditText.text.isNullOrBlank() ||
            addressEditText.text.isNullOrBlank() ||
            emailEditText.text.isNullOrBlank() ||
            phoneNumberEditText.text.isNullOrBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        // Add more validation as needed
        return true
    }

    private fun getPayment() {
        val amount = totalCostEditText.text.toString().replace("$", "")
        if (amount.isNotEmpty()) {
            try {
                val payment = PayPalPayment(
                    BigDecimal(amount),
                    "USD",
                    "Sports Store Purchase",
                    PayPalPayment.PAYMENT_INTENT_SALE
                )

                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration)
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
                startActivityForResult(intent, PAYPAL_REQUEST_CODE)
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYPAL_REQUEST_CODE) {
            when (resultCode) {
                android.app.Activity.RESULT_OK -> {
                    val confirmation = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    confirmation?.let {
                        try {
                            val paymentDetails = it.toJSONObject()
                                ?.getJSONObject("response")
                                ?.getString("state")
                            Toast.makeText(requireContext(), "Payment $paymentDetails!", Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                android.app.Activity.RESULT_CANCELED -> Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
                PaymentActivity.RESULT_EXTRAS_INVALID -> Toast.makeText(requireContext(), "Invalid payment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        requireActivity().stopService(Intent(requireContext(), PayPalService::class.java))
        super.onDestroy()
    }
}