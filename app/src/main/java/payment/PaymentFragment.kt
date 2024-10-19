package payment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sportsstore.R
import com.example.sportsstore.databinding.FragmentPaymentBinding
import com.example.sportsstore.models.User
import com.example.sportsstore.viewmodels.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
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

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var authViewModel: AuthViewModel
    private val args by navArgs<PaymentFragmentArgs>()
    private val CHANNEL_ID = "purchase_notification_channel"
    private val NOTIFICATION_ID = 1

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
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))[AuthViewModel::class.java]

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
                    binding.addressEditText.setText(it.address)
                    binding.emailEditText.setText(it.email)
                    binding.phoneNumberEditText.setText(it.phoneNumber)
                }
            }
        }
        var totalCost = 0.0
        args.currentProduct.forEachIndexed { index, childItem ->
            totalCost += childItem.price * args.currentAmount[index]
        }
        val totalAmount = args.currentAmount.sum()
        binding.totalCostText.text = "${totalCost}EGP"
        binding.numberOfItemsText.text = "${totalAmount} items"

        binding.btnpayment.setOnClickListener {
            if(binding.cashOnDeliveryCheckbox.isChecked){
                args.currentProduct.forEachIndexed { productIndex, productIt ->
                    authViewModel.addOrder(
                        productIt.productName,
                        productIt.year,
                        productIt.price,
                        productIt.imageUrl,
                        productIt.id,
                        args.selectedColor[productIndex],
                        args.selectedSize[productIndex],
                        args.currentAmount[productIndex],
                        binding.addressEditText.text.toString(),
                        binding.emailEditText.text.toString(),
                        binding.phoneNumberEditText.text.toString(),
                        "Cash On Delivery"
                    )
                }
                val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
                val check = sharedPref.getBoolean("push_notifications", true)
                if(check){
                    sendNotification()
                    Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                }
                triggerVibrationOnPurchase()
                if(args.fromCart) authViewModel.deleteAllCartItems()
                findNavController().navigate(R.id.action_paymentFragment_to_homeFragment)
            }else{
                getPayment()
            }
        }

        return binding.root
    }

    private fun validateInputs(): Boolean {
        if (binding.addressEditText.text.isNullOrBlank() ||
            binding.emailEditText.text.isNullOrBlank() ||
            binding.phoneNumberEditText.text.isNullOrBlank()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        // Add more validation as needed
        return true
    }

    private fun getPayment() {
        val amount = binding.totalCostText.text.toString().substringBefore("EGP")
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
    @Suppress("DEPRECATION")
    private fun triggerVibrationOnPurchase() {
        val sharedPref = requireActivity().getSharedPreferences("notification_settings", Context.MODE_PRIVATE)
        val check = sharedPref.getBoolean("vibration", true)

        if (check) {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            // Vibrate for 500 milliseconds (half a second)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For Android O and above
                val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                // For Android below O
                vibrator.vibrate(500)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun sendNotification() {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this icon exists
            .setContentTitle("Purchase Complete")
            .setContentText("You have successfully made a purchase!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
    override fun onDestroy() {
        requireActivity().stopService(Intent(requireContext(), PayPalService::class.java))
        super.onDestroy()
    }

}