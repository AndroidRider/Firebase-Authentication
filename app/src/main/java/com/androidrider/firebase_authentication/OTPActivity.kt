package com.androidrider.firebase_authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.androidrider.firebase_authentication.databinding.ActivityOtpactivityBinding
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

class OTPActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpactivityBinding
    lateinit var auth: FirebaseAuth
    lateinit var OTP : String
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var phoneNumber : String

    lateinit var progressBar: ProgressBar
    lateinit var verifyBtn: View
    lateinit var resendTV: View
    lateinit var inputOTP1: EditText
    lateinit var inputOTP2: EditText
    lateinit var inputOTP3: EditText
    lateinit var inputOTP4: EditText
    lateinit var inputOTP5: EditText
    lateinit var inputOTP6: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        progressBar = binding.otpProgressBar
        verifyBtn = binding.verifyOTPBtn
        resendTV = binding.resendTextView
        inputOTP1 = binding.otpEditText1
        inputOTP2 = binding.otpEditText2
        inputOTP3 = binding.otpEditText3
        inputOTP4 = binding.otpEditText4
        inputOTP5 = binding.otpEditText5
        inputOTP6 = binding.otpEditText6



        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        progressBar.visibility = View.INVISIBLE

        addTextChangedListener()// method
        resendOTPVisibility() // method

        resendTV.setOnClickListener {
            resendVerificationCode() // method
            resendOTPVisibility() // method
        }

        verifyBtn.setOnClickListener {
            // collect otp from all the edit texts
            val typedOTP = (inputOTP1.text.toString()+inputOTP2.text.toString()+inputOTP3.text.toString()+
                           inputOTP4.text.toString()+inputOTP5.text.toString()+inputOTP6.text.toString())

            if(typedOTP.isNotEmpty()){
                if (typedOTP.length == 6){
                    val credential = PhoneAuthProvider.getCredential(OTP, typedOTP)
                    progressBar.visibility = View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this, "please Enter Correct OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "please Enter Correct OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }


   fun resendOTPVisibility(){
       inputOTP1.setText("")
       inputOTP2.setText("")
       inputOTP3.setText("")
       inputOTP4.setText("")
       inputOTP5.setText("")
       inputOTP6.setText("")
       resendTV.visibility = View.VISIBLE
       resendTV.isEnabled = false
       Handler(Looper.myLooper()!!).postDelayed(Runnable {
           resendTV.visibility = View.VISIBLE
           resendTV.isEnabled = true

       }, 6000)
   }
    fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun updateUI(){
        startActivity(Intent(this@OTPActivity, MainActivity::class.java))
    }
     fun addTextChangedListener(){
         inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
         inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
         inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
         inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
         inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
         inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
     }
    inner class EditTextWatcher(private val view: View): TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when(view.id){
                R.id.otpEditText1 -> if(text.length == 1) inputOTP2.requestFocus()
                R.id.otpEditText2 -> if(text.length == 1) inputOTP3.requestFocus() else if(text.isEmpty()) inputOTP1.requestFocus()
                R.id.otpEditText3 -> if(text.length == 1) inputOTP4.requestFocus() else if(text.isEmpty()) inputOTP2.requestFocus()
                R.id.otpEditText4 -> if(text.length == 1) inputOTP5.requestFocus() else if(text.isEmpty()) inputOTP3.requestFocus()
                R.id.otpEditText5 -> if(text.length == 1) inputOTP6.requestFocus() else if(text.isEmpty()) inputOTP4.requestFocus()
                R.id.otpEditText6 -> if(text.isEmpty()) inputOTP5.requestFocus()
            }
        }

    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                progressBar.visibility = View.VISIBLE
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
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {

            }
            // Show a message and update the UI
        }

        override fun onCodeSent(verificationId: String,token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later

            OTP = verificationId
            resendToken = token
        }
    }
}