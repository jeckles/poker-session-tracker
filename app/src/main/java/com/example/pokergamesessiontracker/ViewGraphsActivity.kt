package com.example.pokergamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.google.firebase.database.*
import com.jjoe64.graphview.series.BarGraphSeries
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

import java.sql.Date

// This class handles the displaying of the graphs
// The top graph will display the total profit/loss of the user (on the y-axis) against the number of sessions (on the x-axis)
// This graph will be useful to see general trends of making or losing money over different time periods

// The bottom graph will display a bar graph that compares the profitability of session depending on stakes or game type (whatever one the user selects)
// If the user chooses game type, you will see two bar, one to represent money made/lost in PLO and one for NLH
// If the user chooses stakes, you will see multiple bars, each one representing money made/lost playing with a specific big blind amount

class ViewGraphsActivity : AppCompatActivity() {
    private lateinit var graphView1: GraphView
    private lateinit var graphView2: GraphView
    private lateinit var stakesSelection: RadioButton
    private lateinit var locationSelection: RadioButton
    private lateinit var gameTypeSelection: RadioButton

    private lateinit var buttonBackToHomePage: Button
    private lateinit var valueEventListener: ValueEventListener

    private lateinit var databaseSession: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_page)

        buttonBackToHomePage = findViewById<View>(R.id.buttonBackToHome) as Button
        stakesSelection = findViewById(R.id.stakesSelection)
        locationSelection = findViewById(R.id.locationSelection)
        gameTypeSelection = findViewById(R.id.gameTypeSelection)

        buttonBackToHomePage.setOnClickListener {
            databaseSession.removeEventListener(valueEventListener)
            finish()
        }

        graphView1 = findViewById(R.id.graph1View) as GraphView
        graphView2 = findViewById(R.id.graph2View) as GraphView

        databaseSession = FirebaseDatabase.getInstance().getReference()

        uid = intent.getStringExtra("uid").toString()

        var series = LineGraphSeries<DataPoint>()
        var series2 = BarGraphSeries<DataPoint>()

        valueEventListener = databaseSession.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("onDataChange", "in onDataChange")
                var session: Session? = null
                Log.i("child count is = ", dataSnapshot.childrenCount.toString())
                var currProfit = 0
                var numSessions = 0

                series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                series2.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)

                // This code iterates through the users sessions and keeps track of cumulative profits/losses so that we can see a profit vs number of sessions graph
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        session = postSnapshot.getValue(Session::class.java)

                        //val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                        //var date1 = LocalDate.parse(session!!.date, formatter).toString()
                        //val date2 = java.sql.Date.valueOf(date1)
                        //var profit = session!!.cashOutAmount - session!!.buyInAmount
                        //Log.i("date is ", session!!.date.toString())
                        //Log.i("date is ", date2.toString())
                        //series.appendData(DataPoint(date2 as Date, profit.toDouble()), true, 500)

                        numSessions += 1
                        currProfit += session!!.cashOutAmount - session!!.buyInAmount
                        Log.i("currProfit and numSessions", currProfit.toString() + " " + numSessions.toString())
                        series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                        series2.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    } finally {
                        Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                        Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                        Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                    }
                }
                series.setAnimated(true)

                graphView1.viewport.isXAxisBoundsManual
                graphView2.viewport.isXAxisBoundsManual
                graphView2.viewport.isScrollable
                graphView2.legendRenderer.isVisible
                graphView1.viewport.setMinX(0.0)
                graphView1.viewport.setMaxX(200.0)
                graphView1.addSeries(series)
                graphView2.addSeries(series2)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
