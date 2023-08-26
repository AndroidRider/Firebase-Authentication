package com.androidrider.firebase_authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.androidrider.firebase_authentication.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val tvMessage = binding.tvMessage
        tvMessage.visibility = View.GONE

        // Check if the activity was started after successful sign-up
        val signUpSuccessful = intent.getBooleanExtra("SIGN_UP_SUCCESSFUL", false)
        if (signUpSuccessful) {
            tvMessage.visibility = View.VISIBLE
            tvMessage.text = "Sign up successful! We have sent an email verification link to your email."
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        binding.btnSignIn.setOnClickListener {

            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val verification = auth.currentUser?.isEmailVerified
                        if (verification == true) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI()
                            Toast.makeText(this, "SignIn Successful", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this,"Check your email and verify", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateUI() {
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        val verification = auth.currentUser?.isEmailVerified
        if (currentUser != null && verification == true) {
            updateUI()
        }
    }
}