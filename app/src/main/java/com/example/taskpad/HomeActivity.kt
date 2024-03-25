package com.example.taskpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.taskpad.fragments.AddNewFragment
import com.example.taskpad.fragments.NoteListFragment
import com.example.taskpad.fragments.SignOutFragment
import com.example.taskpad.fragments.TaskListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var appBarNavigationView: NavigationView
    private lateinit var auth: FirebaseAuth

    private var doubleClickToExit = false
    private val doubleClickDelay = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        appBarNavigationView = findViewById(R.id.top_navigation)

        appBarNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.top_navigation -> {
                    val dialogFragment = SignOutFragment()
                    dialogFragment.show(supportFragmentManager, "SettingsPopup")
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottom_task_list -> {
                    replaceFragment(TaskListFragment())
                    true
                }
                R.id.bottom_add -> {
                    replaceFragment(AddNewFragment())
                    true
                }
                R.id.bottom_note_list -> {
                    replaceFragment(NoteListFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(TaskListFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    override fun onBackPressed() {
        if (doubleClickToExit) {
            super.onBackPressed()
            return
        }

        this.doubleClickToExit = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

        object : CountDownTimer(doubleClickDelay.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                doubleClickToExit = false
            }
        }.start()
    }

}







