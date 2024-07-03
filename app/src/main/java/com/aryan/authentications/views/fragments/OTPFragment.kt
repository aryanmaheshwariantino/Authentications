package com.aryan.authentications.views.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.aryan.authentications.MainActivity
import com.aryan.authentications.R
import com.aryan.authentications.databinding.FragmentOTPBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.concurrent.TimeUnit

class OTPFragment : Fragment() {
    private var _binding: FragmentOTPBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics:FirebaseAnalytics
    private  lateinit var firebaseCrashlytics: FirebaseCrashlytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Enter OTP"


        firebaseAuth = (requireActivity() as MainActivity).getFirebaseAuth()
        firebaseCrashlytics = (requireActivity() as MainActivity).getFirebaseCrashlytics()


        binding.btnSendOtp.setOnClickListener {
            val countryCode = binding.etCountryCode.selectedCountryCodeWithPlus
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()

            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(countryCode+phoneNumber)
                Log.d("aryan",countryCode+phoneNumber)
            } else {
                Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                firebaseCrashlytics.log("Phone number is empty")
            }
        }
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "OTPFragment")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())    // Activity (for callback binding)
            .setCallbacks(callbacks)           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
            Log.d("aryan", "Verification completed: $credential")
        }

        override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
            Log.w("aryan", "Verification failed", e)
            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            firebaseCrashlytics.recordException(e)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            val bundle = Bundle().apply {
                putString("verificationId", verificationId)
            }
            findNavController().navigate(R.id.action_otpFragment_to_verifyOtpFragment, bundle)

            Log.d("aryan", "Code sent: $verificationId")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
