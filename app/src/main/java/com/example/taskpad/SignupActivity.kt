package com.example.taskpad

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpad.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupButton.setOnClickListener {
           val  email = binding.signupEmail.text.toString()
           val  password = binding.signupPassword.text.toString()
            val repeatPassword =binding.repeatSignupPassword.text.toString()

            if (validateEmail(email) && validatePassword(password, repeatPassword)){
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this) {task ->
                        if (task.isSuccessful){
                            Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
                            val  intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Signup Failed: Email or password not valid", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        }

        binding.loginText.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }


    //Validacion de Email
    private fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (email.isEmpty()) {
            binding.signupEmail.setError("Field cannot be empty")
            false
        } else if (!email.matches(emailPattern.toRegex())) {
            binding.signupEmail.setError("Invalid email address")
            false
        } else {
            binding.signupEmail.setError(null)
            true
        }
    }


    //Validacion de contraseñas
    private fun validatePassword(password: String , repeatSignupPassword: String): Boolean {
        val passwordVal = "^" +
                "(?=.*[0-9])" +           //at least 1 number
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +        //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +             //no white spaces
                ".{6,}" +                 //at least 6 characters
                "$"
        return if (password.isEmpty() && repeatSignupPassword.isEmpty()) {
            binding.signupPassword.setError("Field cannot be empty")
            binding.repeatSignupPassword.setError("Field cannot be empty")
            false
        } else if (!password.matches(passwordVal.toRegex())) {
            binding.signupPassword.setError("Password must have at least 6 characters between letters and numbers")
            false
        } else if (password != repeatSignupPassword) {
            binding.repeatSignupPassword.setError("Password is not the same")
            false
        } else {
            binding.signupPassword.setError(null)
            true
        }
    }
}