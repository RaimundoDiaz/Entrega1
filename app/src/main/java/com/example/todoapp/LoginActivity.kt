package com.example.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun OnLoginButtonPressed(view: View){
        val myIntent = Intent(view.context, DashboardActivity::class.java)
        startActivity(myIntent)
    }
}
