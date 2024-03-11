package com.example.taskpad.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskpad.LoginActivity
import com.example.taskpad.R
import com.example.taskpad.databinding.FragmentSignoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignOutFragment : Fragment() {

    private lateinit var binding: FragmentSignoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userEmailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignoutBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        userEmailTextView = view.findViewById(R.id.userEmailTextView)

        binding.signOutButton.setOnClickListener {
            // Cierra sesion de usuario al hacer click y envia un toast de confirmacion
            auth.signOut()
            Toast.makeText(context, "Sign Out successful", Toast.LENGTH_SHORT).show()

            // Redirige a la activity de Login
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)

            // Finaliza el fragment
            activity?.finish()
        }

        // Boton atras
        binding.backToHomeBtn.setOnClickListener {
            val taskListFragment = TaskListFragment()
            fragmentManager?.beginTransaction()?.replace(R.id.frame_container, taskListFragment)
                ?.commit()
        }

        // Muestra correo electronico logeado
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email
        userEmailTextView.text = userEmail ?: "Email is not available"



    }
}

