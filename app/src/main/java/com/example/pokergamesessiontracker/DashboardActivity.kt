package com.example.pokergamesessiontracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.INotificationSideChannel
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
import android.widget.PopupMenu
import kotlin.math.roundToInt

class DashboardActivity : AppCompatActivity() {

    private lateinit var buttonAddSession: Button
    internal lateinit var listViewSessions: ListView
    internal lateinit var moneyMade: TextView
    internal lateinit var returnOnInvestment: TextView
    internal lateinit var sessionsPlayed: TextView
    internal lateinit var hoursPlayed: TextView
    private lateinit var buttonViewGraphs: Button

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
        buttonViewGraphs = findViewById<View>(R.id.buttonViewGraphs) as Button

        // Career Stats text views that will be updated as the user adds sessions
        moneyMade = findViewById<TextView>(R.id.moneyMade)
        returnOnInvestment = findViewById<TextView>(R.id.returnOnInvestment)
        sessionsPlayed = findViewById<TextView>(R.id.sessionsPlayed)
        hoursPlayed = findViewById<TextView>(R.id.hoursPlayed)

        databaseSession = FirebaseDatabase.getInstance().getReference()

        sessions = ArrayList()

        uid = intent.getStringExtra("com.example.pokergamesessiontracker.userid").toString()
        Log.i("DashboardActivity", "uid is " + uid.toString())

        // Clicking the Add Session Button brings the user to another page where they can add information about their session
        buttonAddSession.setOnClickListener {
            val intent = Intent(applicationContext, SessionActivity::class.java)
            intent.putExtra("count", count)
            intent.putExtra("uid", uid)
            count++
            startActivityForResult(intent, ACTIVITY_CODE)
        }

        // Clicking the View Graphs Button brings the user to another page where they can see graphs over all the sessions they have entered
        buttonViewGraphs.setOnClickListener {
            val intent = Intent(applicationContext, ViewGraphsActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }

        // Clicking on a specfic session bring up a new page where the user can see all the specific information of that session, such as stakes played and hours played etc.
        listViewSessions.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val session = sessions[i]

            val intent = Intent(applicationContext, SessionView::class.java)

            // We pass all the session information on to the next page where the user can see it in a nicer format
            intent.putExtra("uid", uid)
            intent.putExtra("id", session.id)
            intent.putExtra("date", session.date)
            intent.putExtra("location", session.location)
            intent.putExtra("gameType", session.gameType)
            intent.putExtra("hoursPlayed", session.hoursPlayed)
            intent.putExtra("smallBlind", session.smallBlind)
            intent.putExtra("bigBlind", session.bigBlind)
            intent.putExtra("buyInAmount", session.buyInAmount)
            intent.putExtra("cashOutAmount", session.cashOutAmount)
            intent.putExtra("Session ID", session.sessionId)
            intent.putParcelableArrayListExtra("sessionsList", sessions)

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
                var array2 = data?.getParcelableArrayListExtra<Session>("result2")
                if (array != null) {
                    for (session in array) {
                        sessions.add(session)
                    }
                    Log.i("onActivityResult", "array count = " + array.count().toString())
                } else if (array2 != null) {
                    Log.i("onActivityResult", "reset the session")
                    sessions = array2
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

        var buyins: Int = 0
        var cashouts: Int = 0
        var sessionCount: Int = 0
        var hours: Int = 0

        //var allSessions = ArrayList<Session>()

        databaseSession.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sessions.clear()
                var session: Session? = null
                Log.i("child count is = ", dataSnapshot.childrenCount.toString())

                // This iterates through all the users sessions, getting information like the amount of total hours played, total sessions played, and the buyins and cashout amounts so we can calculate total profit and ROI
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        session = postSnapshot.getValue(Session::class.java)
                        buyins += session!!.buyInAmount
                        cashouts += session!!.cashOutAmount
                        sessionCount += 1
                        hours += session!!.hoursPlayed
                        sessions.add(session)
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    } finally {
                        Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                        Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                        Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                    }
                }
                var profit = cashouts - buyins
                var investment: Int = 0

                // Calculate ROI
                if (buyins > 0) {
                    investment = ((cashouts.toDouble() - buyins.toDouble())/buyins.toDouble() * 100).roundToInt()
                }

                moneyMade.text = profit.toString()
                returnOnInvestment.text = investment.toString()
                sessionsPlayed.text = sessionCount.toString()
                hoursPlayed.text = hours.toString()

                val sessionAdapter = SessionList(this@DashboardActivity, sessions)
                listViewSessions.adapter = sessionAdapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.i("onResume", "in onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("onPause", "in onPause")
        sessions.clear()
    }

    override fun onStop() {
        super.onStop()
        Log.i("onStop", "in onStop")
        sessions.clear()
    }

    companion object {
        const val ACTIVITY_CODE = 1
    }
}
