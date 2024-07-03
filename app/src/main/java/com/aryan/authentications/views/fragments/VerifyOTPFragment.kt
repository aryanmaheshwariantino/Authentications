package com.aryan.authentications.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aryan.authentications.MainActivity
import com.aryan.authentications.R
import com.aryan.authentications.databinding.FragmentVerifyOTPBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics

class VerifyOTPFragment : Fragment() {
    private var _binding: FragmentVerifyOTPBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private var verificationId:String?=null
   private lateinit var firebaseAnalytics:FirebaseAnalytics
    private  lateinit var firebaseCrashlytics: FirebaseCrashlytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerifyOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Verify OTP"


        firebaseAuth = (requireActivity() as MainActivity).getFirebaseAuth()
        firebaseCrashlytics = (requireActivity() as MainActivity).getFirebaseCrashlytics()

        verificationId = arguments?.getString("verificationId")


        binding.btnVerifyOtp.setOnClickListener {
            val code = binding.etOtp.text.toString().trim()

            if (code.isNotEmpty()) {
                verifyCode(code)
            } else {
                Toast.makeText(context, "Please enter the OTP", Toast.LENGTH_SHORT).show()
                firebaseCrashlytics.log("OTP is empty")
            }
        }
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME,"VerifyOTP Fragment")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun verifyCode(code: String) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            signInWithPhoneAuthCredential(credential)
        } ?: run {
            Toast.makeText(context, "Verification ID is null", Toast.LENGTH_SHORT).show()
            firebaseCrashlytics.log("Verification ID is null")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: com.google.firebase.auth.PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("aryan", "signInWithCredential:success")
                    findNavController().navigate(R.id.action_verifyOtpFragment_to_loginFragment)
                    Toast.makeText(context, "Verification successful", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("aryan", "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    firebaseCrashlytics.recordException(task.exception ?: Exception("Unknown sign-in failure"))
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}