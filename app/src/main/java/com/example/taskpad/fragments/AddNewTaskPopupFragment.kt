package com.example.taskpad.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskpad.databinding.FragmentAddNewTaskPopupBinding
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddNewTaskPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddNewTaskPopupBinding
    private lateinit var listener: DialogBtnClickListener
    private lateinit var auth: FirebaseAuth
    private var taskData: TaskData? = null
    private lateinit var taskAction: String
    private lateinit var selectedDueDate: Calendar

    fun setListener(listener: DialogBtnClickListener) {
        this.listener = listener
    }

    fun setTaskAction(taskAction: String) {
        this.taskAction = taskAction
    }

    companion object {
        const val TAG = "AddNewTaskPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String, dueDate: String?) = AddNewTaskPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
                putString("dueDate", dueDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewTaskPopupBinding.inflate(inflater, container, false)
        this.binding.popupTitle.text = this.taskAction + " Task"
        this.binding.createTaskBtn.text = this.taskAction + " task"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDueDate = Calendar.getInstance()

        if (arguments != null) {
            taskData = TaskData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString(),
                arguments?.getString("dueDate").toString()
            )

            binding.newTaskEt.setText(taskData?.task)
            binding.dueDateEt.setText(taskData?.dueDate)
        }
        registerEvents()
    }

    private fun registerEvents() {
        auth = FirebaseAuth.getInstance()

        // Mostrar DatePicker al hacer clic en el campo de fecha de fin
        binding.dueDateEt.setOnClickListener {
            showDatePicker()
        }

        binding.createTaskBtn.setOnClickListener {
            val task = binding.newTaskEt.text.toString()
            val dueDate = binding.dueDateEt.text.toString()
            if (task.trim().isNotEmpty()) {
                if (taskData == null) {
                    (listener as SaveDialogBtnClickListener).onSaveTask(task, dueDate, binding.newTaskEt)
                } else {
                    taskData?.task = task
                    (listener as UpdateDialogBtnClickListener).onUpdateTask(
                        taskData!!,
                        dueDate,
                        binding.newTaskEt
                    )
                }
            } else {
                Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.closeTaskPopup.setOnClickListener {
            dismiss()
        }
    }

    // Función para mostrar el DatePicker
    private fun showDatePicker() {
        selectedDueDate = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, day: Int ->
                selectedDueDate.set(year, month, day)



                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.dueDateEt.setText(sdf.format(selectedDueDate.time))
            },
            selectedDueDate.get(Calendar.YEAR),
            selectedDueDate.get(Calendar.MONTH),
            selectedDueDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePicker.show()
    }


    interface DialogBtnClickListener {}

    interface SaveDialogBtnClickListener : DialogBtnClickListener {
        fun onSaveTask(task: String, dueDate: String, newTaskEt: TextInputEditText)
    }

    interface UpdateDialogBtnClickListener : DialogBtnClickListener {
        fun onUpdateTask(taskData: TaskData, dueDate: String, newTaskEt: TextInputEditText)
    }
}
