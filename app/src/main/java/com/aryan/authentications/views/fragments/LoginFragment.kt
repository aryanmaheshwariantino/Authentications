package com.aryan.authentications.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.aryan.authentications.MainActivity
import com.aryan.authentications.R
import com.aryan.authentications.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Login"

        // Retrieve FirebaseAuth instance from MainActivity
        firebaseAuth = (requireActivity() as MainActivity).getFirebaseAuth()
        firebaseAnalytics = (requireActivity() as MainActivity).getFirebaseAnalytics()
        firebaseCrashlytics = (requireActivity() as MainActivity).getFirebaseCrashlytics()

        // Log Fragment Creation
        firebaseCrashlytics.log("LoginFragment created")

        // Set click listener for signup TextView
        binding.tvSignup.setOnClickListener {
            Log.d("aryan", "Successfully Moved to Signup Page")
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        // Forgot password handling
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("aryan", "Password reset email has been sent")
                            Snackbar.make(binding.root, "Password reset email sent", Snackbar.LENGTH_LONG).show()
                        } else {
                            val exception = task.exception
                            Log.d("aryan", "Password reset failed", exception)
                            firebaseCrashlytics.recordException(exception ?: Exception("Unknown error during password reset"))
                            Snackbar.make(binding.root, "Failed to send password reset email", Snackbar.LENGTH_LONG).show()
                        }
                    }
            } else {
                Log.d("aryan", "Email field is empty for password reset")
                Snackbar.make(binding.root, "Please enter your email to reset password", Snackbar.LENGTH_LONG).show()
            }
        }

        // Set click listener for login button
        binding.btnLogin.setOnClickListener {
            // Perform login logic using FirebaseAuth
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d("aryan", "Login successful")
                            // Navigate to Home screen
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        } else {
                            val exception = task.exception
                            Log.w("aryan", "Login failed", exception)
                            firebaseCrashlytics.recordException(exception ?: Exception("Unknown error during login"))
                            // Handle login failure (show error message, etc.)
                            Snackbar.make(binding.root, "Login failed: ${exception?.message}", Snackbar.LENGTH_LONG).show()
                        }
                    }
            } else {
                // Handle empty email or password
                Log.d("aryan", "Email or Password field is empty")
                Snackbar.make(binding.root, "Please fill in both email and password fields", Snackbar.LENGTH_LONG).show()
            }
        }

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, "LoginFragment")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        firebaseCrashlytics.log("LoginFragment destroyed")
    }
}
