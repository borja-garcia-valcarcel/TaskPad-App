package com.example.taskpad.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskpad.databinding.FragmentTaskListBinding
import com.example.taskpad.utils.TaskAdapter
import com.example.taskpad.utils.TaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class TaskListFragment : Fragment(), TaskAdapter.TaskAdapterClicksInterface,
    AddNewTaskPopupFragment.UpdateDialogBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskAdapter
    private var popupFragment: AddNewTaskPopupFragment? = null
    private lateinit var mList: MutableList<TaskData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("tasks")

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = TaskAdapter(mList, requireContext())
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val taskId = taskSnapshot.key ?: ""
                    val taskValue = taskSnapshot.child("task").getValue(String::class.java) ?: ""
                    val dueDateValue = taskSnapshot.child("dueDate").getValue(String::class.java)

                    var dueDate: Calendar? = null
                    if (dueDateValue != null && dueDateValue.isNotEmpty()) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dueDate = Calendar.getInstance()
                        dueDate.time = sdf.parse(dueDateValue) ?: Date()
                    }

                    val task = TaskData(taskId, taskValue, dueDate)
                    mList.add(task)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onDeleteTaskBtnClicked(taskData: TaskData) {
        databaseReference.child(taskData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(taskData: TaskData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = AddNewTaskPopupFragment.newInstance(taskData.taskId, taskData.task, taskData.dueDate)
        popupFragment!!.setListener(this)
        popupFragment!!.setTaskAction("Update")
        popupFragment!!.show(childFragmentManager, AddNewTaskPopupFragment.TAG)
    }


    override fun onUpdateTask(taskData: TaskData, dueDate: Calendar, newTaskEt: TextInputEditText) {
        val newTitle = newTaskEt.text.toString().trim()

        if (newTitle.isNotEmpty()) {
            if (dueDate.before(Calendar.getInstance()) && !isSameDay(dueDate, Calendar.getInstance())) {
                Toast.makeText(context, "Due date cannot be earlier than today", Toast.LENGTH_SHORT).show()
                return
            }

            val taskMap = HashMap<String, Any>()
            taskMap["task"] = newTitle

            // Aquí establecemos el valor del campo de fecha
            val formattedDate = formatDate(dueDate)
            taskMap["dueDate"] = formattedDate

            // Calcular los días restantes
            val currentDate = Calendar.getInstance().time
            val diff = dueDate.timeInMillis - currentDate.time
            val days = diff / (24 * 60 * 60 * 1000)
            taskMap["daysLeft"] = days

            val taskRef = databaseReference.child(taskData.taskId)
            taskRef.updateChildren(taskMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    popupFragment!!.dismiss()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error updating task: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }



    private fun formatDate(calendar: Calendar): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return simpleDateFormat.format(calendar.time)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

}


