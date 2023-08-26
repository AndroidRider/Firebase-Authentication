package com.androidrider.firebase_authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidrider.firebase_authentication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        finish()
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Email Verification
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(baseContext,"Check your email and verify", Toast.LENGTH_SHORT,).show()
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(baseContext, it.toString() ,Toast.LENGTH_SHORT,).show()
                            }
                        // Sign in success, update UI with the signed-in user's information
                        updateUI()
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(baseContext,"Authentication failed.",Toast.LENGTH_SHORT,).show()

                    }
                }
        }
    }

    private fun updateUI() {

        // Inside SignUpActivity after successful sign-up
        val intent = Intent(this, SignInActivity::class.java)
        intent.putExtra("SIGN_UP_SUCCESSFUL", true)
        startActivity(intent)
        finish()
    }

}
