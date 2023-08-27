package com.androidrider.firebase_authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.androidrider.firebase_authentication.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhoneBinding

    lateinit var mProgressBar: ProgressBar
    lateinit var auth: FirebaseAuth
    lateinit var number: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sendOTPBtn = binding.sendOTPBtn
        val phoneNumberET = binding.phoneEditTextNumber
        mProgressBar = binding.phoneProgressBar
        auth = FirebaseAuth.getInstance()

        sendOTPBtn.setOnClickListener {
            number = phoneNumberET.text.trim().toString()

            if (number.isNotEmpty()) {
                if (number.length == 10) {
                    number = "+91$number"

                    mProgressBar.visibility = View.VISIBLE

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateUI(){
        startActivity(Intent(this@PhoneActivity, MainActivity::class.java))
    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                updateUI()
                Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
            }
            else {
                // Sign in failed, display a message and update the UI
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
                // Update UI
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Toast.makeText(this@PhoneActivity, "Invalid Request", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(this@PhoneActivity, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Toast.makeText(this@PhoneActivity, "Missing", Toast.LENGTH_SHORT).show()
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(verificationId: String,token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            val intent = Intent(this@PhoneActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)

            // Hide the progress bar
            mProgressBar.visibility = View.INVISIBLE
            // Show a toast message indicating that the OTP has been sent
            Toast.makeText(this@PhoneActivity, "OTP has been sent to your number", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            startActivity(Intent(this@PhoneActivity, MainActivity::class.java))
        }
    }

}