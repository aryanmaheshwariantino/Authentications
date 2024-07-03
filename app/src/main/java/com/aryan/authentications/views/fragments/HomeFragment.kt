package com.aryan.authentications.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aryan.authentications.MainActivity
import com.aryan.authentications.R
import com.aryan.authentications.adapter.ProductAdapter
import com.aryan.authentications.databinding.FragmentHomeBinding
import com.aryan.authentications.respository.ProductRepository
import com.aryan.authentications.respository.RetrofitClient
import com.aryan.authentications.viewmodel.HomeViewModel
import com.aryan.authentications.viewmodel.HomeViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var productAdapter: ProductAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(ProductRepository(RetrofitClient.apiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true) // Enable options menu in fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar Action
        val toolbar = (activity as? AppCompatActivity)?.supportActionBar
        toolbar?.title = "Amazon Products"

        // Getting instances from MainActivity
        firebaseAnalytics = (requireActivity() as MainActivity).getFirebaseAnalytics()
        firebaseCrashlytics = (requireActivity() as MainActivity).getFirebaseCrashlytics()
        firebaseAuth = (requireActivity() as MainActivity).getFirebaseAuth()
        googleSignInClient = (requireActivity() as MainActivity).getGoogleSignInClient()

        // Log Fragment Creation
        firebaseCrashlytics.log("HomeFragment created")

        // API calling for Product list
        productAdapter = ProductAdapter()
        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.etProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                firebaseCrashlytics.recordException(it)
                Log.e("HomeFragment", "Error fetching products", it)
                // Show error message to the user
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        }

        try {
            homeViewModel.searchProducts("Phone", 1, "US", "RELEVANCE", "ALL")
        } catch (e: Exception) {
            firebaseCrashlytics.recordException(e)
            Log.e("HomeFragment", "Exception during searchProducts", e)
        }

        binding.etRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, "HomeFragment")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // Dialog for logout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.etLogOutBtn -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Dialog Handling
    private fun showLogoutConfirmationDialog() {
        val dialog = DialogFragment {
            signOut()
        }
        dialog.show(parentFragmentManager, "LogoutConfirmationDialog")
    }

    // Sign out Handling
    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            firebaseAuth.signOut()
            // Navigate back to signup screen
            findNavController().navigate(R.id.action_homeFragment_to_SignUpFragment)
            firebaseCrashlytics.log("User signed out")
        }.addOnFailureListener {
            firebaseCrashlytics.recordException(it)
            Log.e("HomeFragment", "Error during sign out", it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseCrashlytics.log("HomeFragment destroyed")
    }
}
