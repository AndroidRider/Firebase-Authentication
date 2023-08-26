package com.androidrider.firebase_authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.androidrider.firebase_authentication.databinding.ActivityResetPasswordBinding
import com.androidrider.firebase_authentication.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityResetPasswordBinding

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnReset.setOnClickListener {

            binding.tvMessage.visibility = View.VISIBLE
            binding.tvMessage.text = "We have sent an reset password link to your email."

            val email = binding.edtEmail.text.toString()
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "please check your email to reset the password",Toast.LENGTH_SHORT,).show()
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext, it.toString() , Toast.LENGTH_SHORT,).show()
                }
        }
    }
}