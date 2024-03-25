package com.example.taskpad.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.example.taskpad.databinding.TaskListItemBinding

class TaskAdapter(private val list: MutableList<TaskData>, private val context: Context) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var listener: TaskAdapterClicksInterface? = null
    private val TAG = "TaskAdapter"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE)

    fun setListener(listener: TaskAdapterClicksInterface) {
        this.listener = listener
    }

    inner class TaskViewHolder(val binding: TaskListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = list[position]

        with(holder){
            with(currentItem){
                binding.titleTask.text = this.task

                // Obtener el estado de isCompleted desde SharedPreferences
                val isCompleted = sharedPreferences.getBoolean(this.taskId, false)

                if (isCompleted) {
                    binding.titleTask.paintFlags = binding.titleTask.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.titleTask.paintFlags = binding.titleTask.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }

                binding.doneTask.setOnClickListener {
                    // Invertir el estado de isCompleted
                    val newIsCompleted = !isCompleted

                    // Guardar el nuevo estado en SharedPreferences
                    sharedPreferences.edit {
                        putBoolean(this@with.taskId, newIsCompleted)
                        apply()
                    }

                    notifyDataSetChanged()
                }
            }
        }
    }

    interface TaskAdapterClicksInterface {
        fun onDeleteTaskBtnClicked(taskData: TaskData)
        fun onEditTaskBtnClicked(taskData: TaskData)
    }
}







