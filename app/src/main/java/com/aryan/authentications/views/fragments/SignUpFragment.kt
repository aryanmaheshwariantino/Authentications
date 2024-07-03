package com.aryan.authentications.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aryan.authentications.MainActivity
import com.aryan.authentications.R
import com.aryan.authentications.databinding.FragmentSignUpBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAnalytics:FirebaseAnalytics
    private  lateinit var firebaseCrashlytics: FirebaseCrashlytics


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Sign Up"


        firebaseAuth = (requireActivity() as MainActivity).getFirebaseAuth()
        firebaseCrashlytics = (requireActivity() as MainActivity).getFirebaseCrashlytics()

        callbackManager = CallbackManager.Factory.create()

        googleSignInClient = (requireActivity() as MainActivity).getGoogleSignInClient()

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.etGoogle.setOnClickListener {
            signInWithGoogle()
        }
        binding.etFacebook.setOnClickListener{
            signInWithFacebook()
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val reenteredPassword = binding.etRePassword.text.toString().trim()

            if (validateInputs(email, password, reenteredPassword)) {
                createUserWithEmailAndPassword(email, password)
            }
        }
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME,"SignUp Fragment")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // Facebook Sign In Firebase Handling

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d(TAG, "Facebook login success, token: ${result.accessToken.token}")
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "Facebook login canceled")
                    firebaseCrashlytics.log("Facebook login canceled")
                    // Handle cancellation
                }

                override fun onError(error: FacebookException?) {
                    Log.e(TAG, "Facebook login error: ${error?.message}")
                    Toast.makeText(context, "Facebook login failed.", Toast.LENGTH_SHORT).show()
                    firebaseCrashlytics.recordException(error ?: Exception("Unknown Facebook login error"))

                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(context, "Facebook Sign-In successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    firebaseCrashlytics.recordException(task.exception ?: Exception("Unknown sign-in failure"))

                }
            }
    }


    // Google Sign Firebase Handling

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(context, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
            firebaseCrashlytics.recordException(e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    firebaseCrashlytics.recordException(task.exception ?: Exception("Unknown sign-in failure"))

                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "SignUpFragment"
    }

    // Email Password Firebase Handling

    private fun validateInputs(
        email: String,
        password: String,
        reenteredPassword: String
    ): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email cannot be empty"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password cannot be empty"
            return false
        }
        if (password != reenteredPassword) {
            binding.etRePassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Signup successful")
                    Snackbar.make(binding.root,"Account has been created ",Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_signupFragment_to_otpFragment)
                } else {
                    Log.w(TAG, "Signup failed", task.exception)
                    handleSignupError(task.exception)
                    firebaseCrashlytics.recordException(task.exception ?: Exception("Unknown signup failure"))

                }
            }
    }

    private fun handleSignupError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                binding.etPassword.error = "Weak password"
                binding.etPassword.requestFocus()
            }

            is FirebaseAuthInvalidCredentialsException -> {
                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
            }

            is FirebaseAuthUserCollisionException -> {
                binding.etEmail.error = "Email already exists"
                binding.etEmail.requestFocus()
            }

            else -> {
                Log.e(TAG, "Signup error: ${exception?.message}")
                firebaseCrashlytics.recordException(exception ?: Exception("Unknown signup error"))

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
