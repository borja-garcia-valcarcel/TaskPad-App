package com.example.taskpad.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskpad.databinding.FragmentAddNewTaskPopupBinding
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class AddNewTaskPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddNewTaskPopupBinding
    private lateinit var listener: DialogBtnClickListener
    private lateinit var auth: FirebaseAuth
    private var taskData: TaskData? = null
    private lateinit var taskAction: String

    fun setListener(listener: DialogBtnClickListener) {
        this.listener = listener
    }
    fun setTaskAction(taskAction: String) {
        this.taskAction = taskAction
    }

    companion object {
        const val TAG = "AddNewTaskPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = AddNewTaskPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddNewTaskPopupBinding.inflate(inflater, container, false)
        this.binding.popupTitle.text = this.taskAction + " Task"
        this.binding.createTaskBtn.text = this.taskAction + " task"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            taskData = TaskData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )

            binding.newTaskEt.setText(taskData?.task)
        }
        registerEvents()
    }

    private fun registerEvents() {
        auth = FirebaseAuth.getInstance()
        binding.createTaskBtn.setOnClickListener {
            val task = binding.newTaskEt.text.toString()
            if (task.trim().isNotEmpty()) {
                if (taskData == null) {
                    (listener as SaveDialogBtnClickListener).onSaveTask(task, binding.newTaskEt)
                }else {
                    taskData?.task = task
                    (listener as UpdateDialogBtnClickListener).onUpdateTask(taskData!!, binding.newTaskEt)
                }

            } else {
                Toast.makeText(context, "Please write your task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.closeTaskPopup.setOnClickListener {
            dismiss()
        }
    }


    // Necesario para instanciar Listener al crear Fragments que implementan interfaces derivadas de esta
    interface DialogBtnClickListener {}
    interface SaveDialogBtnClickListener: DialogBtnClickListener {
        fun onSaveTask(task: String, newTaskEt: TextInputEditText)
    }
    interface UpdateDialogBtnClickListener: DialogBtnClickListener {
        fun onUpdateTask(taskData: TaskData, newTaskEt: TextInputEditText)
    }


}