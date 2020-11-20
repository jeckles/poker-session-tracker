package com.example.pokergamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.lang.Exception
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var spinnerCountry: Spinner
    private lateinit var buttonAddAuthor: Button
    internal lateinit var listViewSessions: ListView

    internal lateinit var sessions: MutableList<Session>

    private lateinit var databaseAuthors: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("in onCreate", "in on create method ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val username = intent.getStringExtra("com.example.tesla.myhomelibrary.useremail")
        //val username = savedInstanceState!!.getString("com.example.tesla.myhomelibrary.userid")
        Toast.makeText(
            applicationContext,
            "welcome user " + username.toString(),
            Toast.LENGTH_LONG
        ).show()
    }
}
