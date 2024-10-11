package payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.sportsstore.R
import com.example.sportsstore.fragments.ProductOverviewFragmentArgs
import com.example.sportsstore.models.User
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.hbb20.CountryCodePicker
import com.paypal.android.sdk.payments.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    private lateinit var btnPayment: Button
    private lateinit var checkBox: CheckBox
    private lateinit var numberOfItemsText: TextView
    private lateinit var totalCostText: TextView
    private lateinit var authViewModel: AuthViewModel
    private val args by navArgs<PaymentFragmentArgs>()

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

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

        // Initialize views
        addressEditText = view.findViewById(R.id.addressEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        countryCodePicker = view.findViewById(R.id.countryCodePicker)
        btnPayment = view.findViewById(R.id.btnpayment)
        checkBox = view.findViewById(R.id.cash_on_delivery_checkbox)
        numberOfItemsText = view.findViewById(R.id.number_of_items_text)
        totalCostText = view.findViewById(R.id.total_cost_text)

        val firestore = FirebaseFirestore.getInstance()

        viewLifecycleOwner.lifecycleScope.launch {
            val userRefDeferred = async {
                authViewModel.user.value?.uid?.let {
                    firestore.collection("users").document(it)
                        .get()
                        .await()
                        .toObject(User::class.java)
                }
            }

            val userRef = userRefDeferred.await()

            withContext(Dispatchers.Main){
                userRef?.let {
                    addressEditText.setText(it.address)
                    emailEditText.setText(it.email)
                    phoneNumberEditText.setText(it.phoneNumber)
                }
            }
        }

        btnPayment.setOnClickListener {
            getPayment()
        }

        return view
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