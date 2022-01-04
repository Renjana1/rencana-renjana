package com.example.renjana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.renjana.fragments.HomeFragment
import com.example.renjana.fragments.ProfileFragment
import com.example.renjana.fragments.TransactionFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainMenuActivity : AppCompatActivity() {

    lateinit var botNav:BottomNavigationView
    lateinit var frameLayout: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        botNav = findViewById(R.id.bottomNavView)
        frameLayout = findViewById(R.id.frameLayout)

        val homeFragment = HomeFragment()
        val transactionFragment = TransactionFragment()
        val profileFragment = ProfileFragment()

        makeCurrentFragment(homeFragment)

        botNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.bnvHomeButton->{
                    makeCurrentFragment(homeFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.bnvTransactionButton->{
                    makeCurrentFragment(transactionFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.bnvProfileButton->{
                    makeCurrentFragment(profileFragment)
                    return@setOnItemSelectedListener true
                }

            }
            false
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }
}