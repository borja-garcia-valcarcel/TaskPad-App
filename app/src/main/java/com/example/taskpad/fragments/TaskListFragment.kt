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


class TaskListFragment : Fragment(), TaskAdapter.TaskAdapterClicksInterface, AddNewTaskPopupFragment.UpdateDialogBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskAdapter
    private var popupFragment: AddNewTaskPopupFragment? = null
    private lateinit var mList:MutableList<TaskData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentTaskListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()


    }

    private fun init(view:View) {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tasks")
            .child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = TaskAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }


    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object: ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children){
                    val task = taskSnapshot.key?.let {
                        TaskData(it, taskSnapshot.value.toString())
                    }
                    if (task != null) {
                        mList.add(task)
                    }
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
            }else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(taskData: TaskData) {
       if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = AddNewTaskPopupFragment.newInstance(taskData.taskId, taskData.task )
        popupFragment!!.setListener(this)
        popupFragment!!.setTaskAction("Update")
        popupFragment!!.show(childFragmentManager, AddNewTaskPopupFragment.TAG)
    }

    override fun onUpdateTask(taskData: TaskData, newTaskEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[taskData.taskId] = taskData.task
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }


}