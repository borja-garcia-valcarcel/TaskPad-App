package com.example.taskpad


import SignOutFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var appBarNavigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        appBarNavigationView = findViewById(R.id.top_navigation)

        appBarNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.top_navigation -> {
                    replaceFragment(SignOutFragment())
                    true
                }
                else -> false
            }
        }


        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
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
                R.id.top_navigation ->{
                    replaceFragment(SignOutFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(TaskListFragment())
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit()
    }


}






