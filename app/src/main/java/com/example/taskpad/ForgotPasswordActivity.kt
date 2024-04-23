package com.example.taskpad

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskpad.databinding.ActivityForgotPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.resetEmailButton.setOnClickListener {
            val email = binding.recoveryEmail.text.toString()
            forgotPassword(email)
        }

        binding.backLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


    }

    // validacion de email
    private fun validateEmail(email: String): Boolean {

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (email.isEmpty()) {
            binding.recoveryEmail.setError("Field cannot be empty")
            false
        } else if (!email.matches(emailPattern.toRegex())) {
            binding.recoveryEmail.setError("Invalid email address")
            false
        } else {
            binding.recoveryEmail.setError(null)
            true
        }
    }

    // funcion para enviar correo de reset password
    private fun forgotPassword(email: String) {
        if (!validateEmail(email)) {
            return
        }
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this, LoginActivity::class.java))
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please check your email",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Invalid email address",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}

