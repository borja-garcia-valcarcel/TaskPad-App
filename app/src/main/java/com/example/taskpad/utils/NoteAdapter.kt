package com.example.taskpad.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpad.databinding.NoteListItemBinding



class NoteAdapter (private val list: MutableList<NoteData>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var listener: NoteAdapterClicksInterface? = null
    private  val TAG = "NoteAdapter"
    fun setListener(listener: NoteAdapterClicksInterface) {
        this.listener = listener
    }

    inner class NoteViewHolder(val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.noteTitle.text = this.note
                binding.noteDesc.text = this.noteDesc
                Log.d(TAG, "onBindViewHolder: "+this)
                binding.deleteNote.setOnClickListener {
                    listener?.onDeleteNoteBtnClicked(this)
                }

                binding.noteContent.setOnClickListener {
                    listener?.onEditNoteBtnClicked(this)
                }
            }
        }

    }

    interface NoteAdapterClicksInterface {
        fun onDeleteNoteBtnClicked(noteData: NoteData)
        fun onEditNoteBtnClicked(noteData: NoteData)
    }
}

