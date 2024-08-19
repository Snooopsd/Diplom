// Основная активность - камера и определитель сортов
package com.example.diplom

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.diplom.fragments.CameraFragment
import com.example.diplom.fragments.HistoryFragment
import com.example.diplom.fragments.LibraryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navMenu = findViewById(R.id.navMenu)

        val libraryFragment = LibraryFragment()
        val cameraFragment = CameraFragment()
        val historyFragment = HistoryFragment()

        makeCurrentFragment(cameraFragment)

        navMenu.selectedItemId = R.id.camera

        navMenu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.library -> {
                    makeCurrentFragment(libraryFragment)
                }
                R.id.camera -> {
                    makeCurrentFragment(cameraFragment)
                }
                R.id.history -> {
                    makeCurrentFragment(historyFragment)
                }
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
    }
}