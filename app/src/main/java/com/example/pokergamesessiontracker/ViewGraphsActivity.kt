package com.example.pokergamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
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
import kotlin.collections.HashMap

// This class handles the displaying of the graphs
// The top graph will display the total profit/loss of the user (on the y-axis) against the number of sessions (on the x-axis)
// This graph will be useful to see general trends of making or losing money over different time periods

// The bottom graph will display a bar graph that compares the profitability of session depending on stakes or game type (whatever one the user selects)
// If the user chooses game type, you will see two bar, one to represent money made/lost in PLO and one for NLH
// If the user chooses stakes, you will see multiple bars, each one representing money made/lost playing with a specific big blind amount

class ViewGraphsActivity : AppCompatActivity() {
    private lateinit var graphView1: GraphView
    private lateinit var graphView2: GraphView
    private lateinit var stakesSelectionButton: RadioButton
    private lateinit var locationSelectionButton: RadioButton
    private lateinit var gameTypeSelectionButton: RadioButton

    private lateinit var buttonBackToHomePage: Button
    private lateinit var valueEventListener: ValueEventListener

    private lateinit var databaseSession: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_page)

        buttonBackToHomePage = findViewById<View>(R.id.buttonBackToHome) as Button
        stakesSelectionButton = findViewById(R.id.stakesSelection) as RadioButton
        locationSelectionButton = findViewById(R.id.locationSelection) as RadioButton
        gameTypeSelectionButton = findViewById(R.id.gameTypeSelection) as RadioButton

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
        var series2stakes = BarGraphSeries<DataPoint>()
        var series2location = BarGraphSeries<DataPoint>()

        valueEventListener = databaseSession.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("onDataChange", "in onDataChange")
                var session: Session? = null
                Log.i("child count is = ", dataSnapshot.childrenCount.toString())
                var currProfit = 0
                var numSessions = 0

                series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)

                // This code iterates through the users sessions and keeps track of cumulative profits/losses so that we can see a profit vs number of sessions graph
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        session = postSnapshot.getValue(Session::class.java)
                        numSessions += 1
                        currProfit += session!!.cashOutAmount - session!!.buyInAmount
                        Log.i("currProfit and numSessions", currProfit.toString() + " " + numSessions.toString())
                        series.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                    } catch (e: Exception) {
                        Log.e("Error", e.toString())
                    } finally {
                        Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                        Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                        Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                    }
                }
                graphView1.addSeries(series)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        gameTypeSelectionButton.setOnClickListener() {
            valueEventListener = databaseSession.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("onDataChange", "in onDataChange")
                    var session: Session? = null
                    Log.i("child count is = ", dataSnapshot.childrenCount.toString())
                    var currProfit = 0
                    var numSessions = 0
                    var nlhProfit = 0
                    var ploProfit = 0

                    series2.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)

                    // This code iterates through the users sessions and keeps track of cumulative profits/losses so that we can see a profit vs number of sessions graph
                    for (postSnapshot in dataSnapshot.child(uid).children) {
                        try {
                            session = postSnapshot.getValue(Session::class.java)

                            if (session!!.gameType == "Texas Hold'Em") {
                                nlhProfit += (session!!.cashOutAmount - session!!.buyInAmount)
                            } else {
                                ploProfit += (session!!.cashOutAmount - session!!.buyInAmount)
                            }
                            numSessions += 1
                            currProfit += session!!.cashOutAmount - session!!.buyInAmount
                            Log.i("currProfit and numSessions", currProfit.toString() + " " + numSessions.toString())
                        // series2.appendData(DataPoint(numSessions.toDouble(), currProfit.toDouble()), true, 500)
                        } catch (e: Exception) {
                            Log.e("Error", e.toString())
                        } finally {
                            Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                            Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                            Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                        }
                    }


                    series2.appendData(DataPoint(1.0, nlhProfit.toDouble()), true, 500)
                    series2.appendData(DataPoint(2.0, ploProfit.toDouble()), true, 500)

                    graphView2.addSeries(series2)
                    }
                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            //databaseSession.removeEventListener(valueEventListener)
            }

        stakesSelectionButton.setOnClickListener{
            var stakesHashMap = HashMap<Int, Int>()

            valueEventListener = databaseSession.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("onDataChange", "in onDataChange")
                    var session: Session? = null
                    Log.i("child count is = ", dataSnapshot.childrenCount.toString())

                    // This code iterates through the users sessions and keeps track of cumulative profits/losses so that we can see a profit vs number of sessions graph
                    for (postSnapshot in dataSnapshot.child(uid).children) {
                        try {
                            session = postSnapshot.getValue(Session::class.java)

                            if(stakesHashMap.containsKey(session!!.bigBlind)){
                                var currVal = stakesHashMap?.get(session!!.bigBlind)
                                    if (currVal != null) {
                                    currVal += (session!!.cashOutAmount - session!!.buyInAmount)
                                }
                                if (currVal != null) {
                                    stakesHashMap.put(session!!.bigBlind, currVal)
                                }
                            } else {
                                stakesHashMap.put(session!!.bigBlind, ((session!!.cashOutAmount) - (session!!.buyInAmount)))
                            }
                        } catch (e: Exception) {
                            Log.e("Error", e.toString())
                        } finally {
                            Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                            Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                            Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                        }
                    }

                    var counter = 0
                    for( key in stakesHashMap.keys.sorted()){
                        series2stakes.appendData(DataPoint(counter.toDouble(), stakesHashMap.get(key)!!.toDouble()), true, 500)
                        counter += 1
                    }

                    graphView2.addSeries(series2stakes)

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
            //databaseSession.removeEventListener(valueEventListener)
        }


        locationSelectionButton.setOnClickListener{
            var locationHashMap = HashMap<String, Int>()

            valueEventListener = databaseSession.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("onDataChange", "in onDataChange")
                    var session: Session? = null
                    Log.i("child count is = ", dataSnapshot.childrenCount.toString())

                    // This code iterates through the users sessions and keeps track of cumulative profits/losses so that we can see a profit vs number of sessions graph
                    for (postSnapshot in dataSnapshot.child(uid).children) {
                        try {
                            session = postSnapshot.getValue(Session::class.java)

                            if(locationHashMap.containsKey(session!!.location)){
                                var currVal = locationHashMap?.get(session!!.location)
                                if (currVal != null) {
                                    currVal += (session!!.cashOutAmount - session!!.buyInAmount)
                                }
                                if (currVal != null) {
                                    session!!.location?.let { it1 -> locationHashMap.put(it1, currVal) }
                                }
                            } else {
                                session!!.location?.let { it1 -> locationHashMap.put(it1, ((session!!.cashOutAmount) - (session!!.buyInAmount))) }
                            }
                        } catch (e: Exception) {
                            Log.e("Error", e.toString())
                        } finally {
                            Log.i("session buyInAmount = ", session!!.buyInAmount.toString())
                            Log.i("session cashOutAmount = ", session!!.cashOutAmount.toString())
                            Log.i("session hoursPlayed = ", session!!.hoursPlayed.toString())
                        }
                    }

                    var counter = 0
                    for( key in locationHashMap.keys){
                        series2location.appendData(DataPoint(counter.toDouble(), locationHashMap.get(key)!!.toDouble()), true, 500)
                        counter += 1
                    }
                    graphView2.addSeries(series2location)
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
           // databaseSession.removeEventListener(valueEventListener)
        }

    }
}
