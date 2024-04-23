package com.example.taskpad.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskpad.LoginActivity
import com.example.taskpad.R
import com.example.taskpad.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignOutFragment : DialogFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // funcionalidad para cerrar sesión de usuario, muestra un popup con el boton de cierre y el correo logueado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        userEmailTextView = view.findViewById(R.id.userEmailTextView)
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.closeSettingsPopup.setOnClickListener {
            dismiss()
        }

        val currentUser = auth.currentUser
        val userEmail = currentUser?.email
        userEmailTextView.text = userEmail ?: "Email is not available"
    }
}




