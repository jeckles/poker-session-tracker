package com.example.pokergamesessiontracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class DashboardActivity : AppCompatActivity() {

    private lateinit var buttonAddSession: Button
    internal lateinit var listViewSessions: ListView

    internal lateinit var sessions: ArrayList<Session>

    private lateinit var databaseSession: DatabaseReference

    private lateinit var uid: String

    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("in onCreate", "in on create method ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        listViewSessions = findViewById<View>(R.id.listViewSessions) as ListView
        buttonAddSession = findViewById<View>(R.id.buttonAddSession) as Button

        databaseSession = FirebaseDatabase.getInstance().getReference()

        sessions = ArrayList()

        uid = intent.getStringExtra("com.example.pokergamesessiontracker.userid").toString()
        Log.i("DashboardActivity", "uid is " + uid.toString())

        buttonAddSession.setOnClickListener {
            val intent = Intent(applicationContext, SessionActivity::class.java)
            intent.putExtra("count", count)
            intent.putExtra("uid", uid)
            count++
            startActivityForResult(intent, ACTIVITY_CODE)
        }

        listViewSessions.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val session = sessions[i]

            val intent = Intent(applicationContext, SessionView::class.java)

            intent.putExtra("Session ID", session.sessionId)
            startActivity(intent)
        }

        val username = intent.getStringExtra("com.example.pokergamesessiontracker.useremail")
        Toast.makeText(
            applicationContext,
            "welcome user " + username.toString(),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var array = data?.getParcelableArrayListExtra<Session>("result")
                if (array != null) {
                    for (session in array) {
                        sessions.add(session)
                    }
                    Log.i("onActivityResult", "array count = " + array.count().toString())
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("onActivityResult", "no result")
            }
        }

    }

    override fun onStart() {
        Log.i("onStart", "In onStart method")
        super.onStart()

        val sessionAdapter = SessionList(this@DashboardActivity, sessions)
        listViewSessions.adapter = sessionAdapter
    }

    companion object {
        const val ACTIVITY_CODE = 1
    }
}
