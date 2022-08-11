package com.vishal.bountylostfound.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vishal.bountylostfound.R
import com.vishal.bountylostfound.databinding.ActivityMainBinding
import com.vishal.bountylostfound.network.NetworkConnection

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        private const val RC_SIGN_IN = 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null) {
            val intent = Intent(this, BottomNavActivity::class.java)
            startActivity(intent)
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_ide))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        val animationcontinious = AnimationUtils.loadAnimation(this, R.anim.rotatecontinious)
        binding.imageView.startAnimation(animation)
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            if (isConnected){
                binding.networkavailable.visibility = View.VISIBLE
                binding.networknotavailable.visibility = View.GONE
            }else{
                binding.networkavailable.visibility = View.GONE
                binding.networknotavailable.visibility = View.VISIBLE
            }
        })

        binding.googleauthbutton.setOnClickListener {
            binding.imageView.startAnimation(animationcontinious)
            Signin()
        }
        binding.exitbounty.setOnClickListener {
            finish()
        }
    }
    private fun Signin(){
        var signinIntent = googleSignInClient.signInIntent
        startActivityForResult(signinIntent,
            RC_SIGN_IN
        )
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                catch (e: ApiException){
                }
            }else if (task.isComplete){
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken : String){
        val credentials = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credentials).addOnCompleteListener(this){
                task -> if(task.isSuccessful){
            val intent = Intent(this,BottomNavActivity::class.java)
            startActivity(intent)
            finish()

        }
        }
    }
}