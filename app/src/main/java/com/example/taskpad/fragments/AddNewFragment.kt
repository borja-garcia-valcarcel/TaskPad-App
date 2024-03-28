package com.example.taskpad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taskpad.databinding.FragmentAddNewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewFragment : Fragment(),
    AddNewTaskPopupFragment.SaveDialogBtnClickListener,
    AddNewNotePopupFragment.SaveDialogBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentAddNewBinding
    private var popupFragmentTask: AddNewTaskPopupFragment? = null
    private var popupFragmentNote: AddNewNotePopupFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        registerEvents()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            databaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
        }
    }

    private fun registerEvents() {
        binding.createNewTaskBtn.setOnClickListener {
            showTaskPopup()
        }
        binding.createNewNoteBtn.setOnClickListener {
            showNotePopup()
        }
    }

    private fun showTaskPopup() {
        if (popupFragmentTask != null)
            childFragmentManager.beginTransaction().remove(popupFragmentTask!!).commit()
        popupFragmentTask = AddNewTaskPopupFragment()
        popupFragmentTask!!.setListener(this)
        popupFragmentTask!!.setTaskAction("Create")
        popupFragmentTask!!.show(childFragmentManager, "AddNewTaskPopupFragment")
    }

    private fun showNotePopup() {
        if (popupFragmentNote != null)
            childFragmentManager.beginTransaction().remove(popupFragmentNote!!).commit()
        popupFragmentNote = AddNewNotePopupFragment()
        popupFragmentNote!!.setListener(this)
        popupFragmentNote!!.setNoteAction("Create")
        popupFragmentNote!!.show(childFragmentManager, "AddNewNotePopupFragment")
    }

    override fun onSaveTask(task: String, dueDate: Calendar?, newTaskEt: TextInputEditText) {
        if (dueDate != null && dueDate.before(Calendar.getInstance()) && !isSameDay(dueDate, Calendar.getInstance())) {
            Toast.makeText(context, "Due date cannot be earlier than today", Toast.LENGTH_SHORT).show()
            return
        }

        val taskMap = HashMap<String, Any>()
        taskMap["task"] = task

        // Agregar la fecha solo si no es null
        dueDate?.let {
            taskMap["dueDate"] = formatDate(it)

            val currentDate = Calendar.getInstance()
            val diff = it.timeInMillis - currentDate.timeInMillis
            val days = diff / (24 * 60 * 60 * 1000)
            taskMap["daysLeft"] = days
        }

        databaseReference.child("tasks").push().setValue(taskMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                    newTaskEt.text = null
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
                popupFragmentTask?.dismiss()
            }
    }



    // Función para formatear la fecha a un formato específico (dd/MM/yyyy)
    private fun formatDate(dueDate: Calendar?): String {
        return dueDate?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            simpleDateFormat.format(it.time)
        } ?: ""
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }




    override fun onSaveNote(note: String, newNoteTitleEt: TextInputEditText, newNoteDescEt: TextInputEditText
    ) {
        val noteMap = HashMap<String, Any>()
        noteMap["title"] = note
        val description = newNoteDescEt.text.toString()
        if (description.isNotEmpty()) {
            noteMap["description"] = description
        }

        databaseReference.child("notes").push().setValue(noteMap).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show()
                newNoteTitleEt.text = null
                newNoteDescEt.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragmentNote?.dismiss()
        }
    }

}

