package com.example.taskpad.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.taskpad.R
import com.example.taskpad.databinding.FragmentNoteListBinding
import com.example.taskpad.databinding.FragmentTaskListBinding
import com.example.taskpad.utils.NoteAdapter
import com.example.taskpad.utils.NoteData
import com.example.taskpad.utils.TaskAdapter
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NoteListFragment : Fragment(), NoteAdapter.NoteAdapterClicksInterface,
AddNewNotePopupFragment.UpdateDialogBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentNoteListBinding
    private lateinit var adapter: NoteAdapter
    private var popupFragment: AddNewNotePopupFragment? = null
    private lateinit var mList: MutableList<NoteData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()


    }

    // Se crea el nodo de notas en la bbdd y se inicializan los adapters y la lista
    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("notes")

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        mList   = mutableListOf()
        adapter = NoteAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }


    // funcion para recuperar los datos de firebase
    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (noteSnapshot in snapshot.children) {
                    val noteId = noteSnapshot.key ?: ""
                    val noteValue = noteSnapshot.child("title").getValue(String::class.java) ?: ""
                    val noteDescValue = noteSnapshot.child("description").getValue(String::class.java) ?: ""
                    val note = NoteData(noteId, noteValue, noteDescValue)
                    mList.add(note)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    // funcion para actualizar la nota existente en la app y la bbdd

    override fun onUpdateNote(
        noteData: NoteData,
        newNoteTitleEt: TextInputEditText,
        newNoteDescEt: TextInputEditText
    ) {
        val newTitle = newNoteTitleEt.text.toString().trim()
        if (newTitle.isNotEmpty()) {
            val noteMap = HashMap<String, Any>()
            noteMap["title"] = newTitle
            val newDesc = newNoteDescEt.text.toString()
            if (newDesc.isNotEmpty()) {
                noteMap["description"] = newDesc
            }
            val noteRef = databaseReference.child(noteData.noteId)
            noteRef.setValue(noteMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
                popupFragment!!.dismiss()
            }
        } else {
            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

        // funcion para eliminar una nota existente en la app y la bbdd
    override fun onDeleteNoteBtnClicked(noteData: NoteData) {
        databaseReference.child(noteData.noteId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // funcionalidad de pulsar en una nota para editarla y abrir el popup
    override fun onEditNoteBtnClicked(noteData: NoteData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = AddNewNotePopupFragment.newInstance(noteData.noteId, noteData.note, noteData.noteDesc)
        popupFragment!!.setListener(this)
        popupFragment!!.setNoteAction("Update")
        popupFragment!!.show(childFragmentManager, AddNewNotePopupFragment.TAG)
    }


}