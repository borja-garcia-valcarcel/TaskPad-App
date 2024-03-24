package com.example.taskpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.taskpad.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkIfUserIsLogged()

        binding.loginButton.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            //Validacion de campos en email y contraseña

            if (email.isNotEmpty() && password.isNotEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){task ->
                        if (task.isSuccessful){
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login Failed: Email or password not valid", Toast.LENGTH_SHORT).show()

                        }
                        binding.progressBar.visibility = View.GONE

                    }
            } else {
                Toast.makeText(this, "Please, enter email and password", Toast.LENGTH_SHORT).show()

            }


        }

        //Cambio de activity: LoginActivity -> SignUpActivity
        binding.signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))

        }

        //Cambio de activity: LoginActivity -> ForgotPasswordActivity
        binding.forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))

        }

    }

    private fun checkIfUserIsLogged() {
        if (auth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }













}