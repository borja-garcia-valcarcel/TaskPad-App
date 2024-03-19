package com.example.taskpad.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskpad.databinding.FragmentAddNewNotePopupBinding
import com.example.taskpad.databinding.FragmentAddNewTaskPopupBinding
import com.example.taskpad.utils.NoteData
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class AddNewNotePopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddNewNotePopupBinding
    private lateinit var listener: DialogBtnClickListener
    private lateinit var auth: FirebaseAuth
    private var noteData: NoteData? = null
    private lateinit var noteAction: String

    fun setListener(listener: DialogBtnClickListener) {
        this.listener = listener
    }
    fun setNoteAction(noteAction: String) {
        this.noteAction = noteAction
    }

    companion object {
        const val TAG = "AddNewNotePopupFragment"

        @JvmStatic
        fun newInstance(noteId: String, note: String, noteDesc: String) = AddNewNotePopupFragment().apply {
            arguments = Bundle().apply {
                putString("noteId", noteId)
                putString("note", note)
                putString("noteDesc", noteDesc)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddNewNotePopupBinding.inflate(inflater, container, false)
        this.binding.popupTitle.text = this.noteAction + " note"
        this.binding.createNoteBtn.text = this.noteAction + " note"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            noteData = NoteData(
                arguments?.getString("noteId").toString(),
                arguments?.getString("note").toString(),
                arguments?.getString("noteDesc").toString()
            )

            binding.newNoteTitleEt.setText(noteData?.note)
        }
        registerEvents()
    }

    private fun registerEvents() {
        auth = FirebaseAuth.getInstance()
        binding.createNoteBtn.setOnClickListener {
            val note = binding.newNoteTitleEt.text.toString()
            if (note.trim().isNotEmpty()) {
                if (noteData == null) {
                    (listener as SaveDialogBtnClickListener).onSaveNote(note, binding.newNoteTitleEt, binding.newNoteDescEt)
                }else {
                    noteData?.note = note
                    (listener as UpdateDialogBtnClickListener).onUpdateNote(noteData!!, binding.newNoteTitleEt, binding.newNoteDescEt)
                }

            } else {
                Toast.makeText(context, "Please write your note", Toast.LENGTH_SHORT).show()
            }
        }
        binding.closeNotePopup.setOnClickListener {
            dismiss()
        }
    }


    // Necesario para instanciar Listener al crear Fragments que implementan interfaces derivadas de esta
    interface DialogBtnClickListener {}
    interface SaveDialogBtnClickListener: DialogBtnClickListener {
        fun onSaveNote(note: String, newNoteTitleEt: TextInputEditText, newNoteDescEt: TextInputEditText)
    }
    interface UpdateDialogBtnClickListener: DialogBtnClickListener {
        fun onUpdateNote(noteData: NoteData, newNoteTitleEt: TextInputEditText, newNoteDescEt: TextInputEditText)
    }


}