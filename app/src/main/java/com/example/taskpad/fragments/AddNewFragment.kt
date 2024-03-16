package com.example.taskpad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taskpad.databinding.FragmentAddNewBinding
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddNewFragment : Fragment(), AddNewTaskPopupFragment.SaveDialogBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentAddNewBinding
    private var popupFragment: AddNewTaskPopupFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddNewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun registerEvents() {
        binding.createNewTaskBtn.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = AddNewTaskPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.setTaskAction("Create")
            popupFragment!!.show(childFragmentManager, "AddNewTaskPopupFragment")
        }
    }


    private fun init(view:View) {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tasks")
            .child(auth.currentUser?.uid.toString())
    }

    override fun onSaveTask(task: String, newTaskEt: TextInputEditText) {

        databaseReference.push().setValue(task).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                newTaskEt.text = null
            }else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }




}