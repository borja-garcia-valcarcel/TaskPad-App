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

    // Se inicializa una instancia de Firebase y se comprueba el usuario autenticado
    private fun init() {
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            databaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
        }
    }

    // funcion para registrar los eventos al pulsar los botones de crear
    private fun registerEvents() {
        binding.createNewTaskBtn.setOnClickListener {
            showTaskPopup()
        }
        binding.createNewNoteBtn.setOnClickListener {
            showNotePopup()
        }
    }
    // Funcion para mostrar el PopUp de creacion de tareas
    private fun showTaskPopup() {
        if (popupFragmentTask != null)
            childFragmentManager.beginTransaction().remove(popupFragmentTask!!).commit()
        popupFragmentTask = AddNewTaskPopupFragment()
        popupFragmentTask!!.setListener(this)
        popupFragmentTask!!.setTaskAction("Create")
        popupFragmentTask!!.show(childFragmentManager, "AddNewTaskPopupFragment")
    }

    // Funcion para mostrar el PopUp de creacion de notas
    private fun showNotePopup() {
        if (popupFragmentNote != null)
            childFragmentManager.beginTransaction().remove(popupFragmentNote!!).commit()
        popupFragmentNote = AddNewNotePopupFragment()
        popupFragmentNote!!.setListener(this)
        popupFragmentNote!!.setNoteAction("Create")
        popupFragmentNote!!.show(childFragmentManager, "AddNewNotePopupFragment")
    }

    // En esta funcion se guarda la tarea en Firebase con un Map. Se crean los nodos de task y DueDate en la bbdd
    override fun onSaveTask(task: String, dueDate: String, newTaskEt: TextInputEditText) {

        val taskMap = HashMap<String, Any>()
        taskMap["task"] = task
        taskMap["dueDate"] = dueDate


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





    // En esta funcion se guarda la nota en Firebase con un Map. Se crean los nodos de Title y Description en la bbdd
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

