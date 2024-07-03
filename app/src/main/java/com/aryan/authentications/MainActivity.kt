package com.aryan.authentications

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.aryan.authentications.databinding.ActivityMainBinding
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)


        // Navigation Graph instance
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)


        //Firebase crashlytics/analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseCrashlytics = FirebaseCrashlytics.getInstance()


        // Initialize Firebase instance
        firebaseAuth =  FirebaseAuth.getInstance()

        // Initialize GoogleSignInClient instance
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }
    fun getFirebaseAuth():FirebaseAuth{
        return firebaseAuth
    }
    fun getGoogleSignInClient():GoogleSignInClient{
        return googleSignInClient
    }
    fun getFirebaseAnalytics():FirebaseAnalytics{
        return firebaseAnalytics
    }
    fun getFirebaseCrashlytics():FirebaseCrashlytics{
        return firebaseCrashlytics
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
