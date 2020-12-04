package com.example.pokergamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

import java.sql.Date

class ViewGraphsActivity : AppCompatActivity() {

    private lateinit var buttonBackToHomePage: Button


    private lateinit var graphView1: GraphView
    private lateinit var graphView2: GraphView

    private lateinit var databaseSession: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_page)

        buttonBackToHomePage = findViewById<View>(R.id.buttonBackToHome) as Button
        buttonBackToHomePage.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }



        graphView1 = findViewById(R.id.graph1View) as GraphView
        graphView2 = findViewById(R.id.graph2View) as GraphView

        databaseSession = FirebaseDatabase.getInstance().getReference()

        uid = intent.getStringExtra("uid").toString()

        var series = LineGraphSeries<DataPoint>()
        //series.appendData( DataPoint(0.0,0.0), true, 500)
        //series.appendData( DataPoint(1.0,1.0), true, 500)
        //series.appendData( DataPoint(2.0,3.0), true, 500)

        databaseSession.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var session: Session? = null
                Log.i("child count is = ", dataSnapshot.childrenCount.toString())
                var currProfit = 0
                var numSessions = 0

                series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        session = postSnapshot.getValue(Session::class.java)

                        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                        //var date1 = LocalDate.parse(session!!.date, formatter).toString()
                        //val date2 = java.sql.Date.valueOf(date1)
                        //var profit = session!!.cashOutAmount - session!!.buyInAmount
                        //Log.i("date is ", session!!.date.toString())
                        //Log.i("date is ", date2.toString())
                        //series.appendData(DataPoint(date2 as Date, profit.toDouble()), true, 500)

                        numSessions += 1
                        currProfit += session!!.cashOutAmount - session!!.buyInAmount
                        series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)


                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    } finally {
                        Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                        Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                        Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        graphView1.addSeries(series)


    }
}
