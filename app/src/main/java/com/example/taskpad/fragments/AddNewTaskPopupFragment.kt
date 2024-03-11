package com.example.taskpad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskpad.R
import com.example.taskpad.databinding.FragmentAddNewTaskPopupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class AddNewTaskPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddNewTaskPopupBinding
    private lateinit var listener: DialogBtnClickListener
    private lateinit var auth: FirebaseAuth

    fun setListener(listener: DialogBtnClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddNewTaskPopupBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }

    private fun registerEvents() {
        auth = FirebaseAuth.getInstance()
        binding.createTaskBtn.setOnClickListener {
            val task = binding.newTaskEt.text.toString()
            if (task.trim().isNotEmpty()) {
                listener.onSaveTask(task, binding.newTaskEt)
            }else {
                Toast.makeText(context, "Please write your task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.closeTaskPopup.setOnClickListener {
            dismiss()
        }
    }

    interface DialogBtnClickListener {
        fun onSaveTask(task : String, newTaskEt : TextInputEditText)
    }


}