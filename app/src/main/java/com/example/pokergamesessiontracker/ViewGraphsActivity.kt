package com.example.pokergamesessiontracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.util.Log
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

    private lateinit var graphView: GraphView

    private lateinit var databaseSession: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_page)

        graphView = findViewById(R.id.graph1View) as GraphView

        databaseSession = FirebaseDatabase.getInstance().getReference()

        uid = intent.getStringExtra("uid").toString()

        var series = LineGraphSeries<DataPoint>()

        databaseSession.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var session: Session? = null
                Log.i("child count is = ", dataSnapshot.childrenCount.toString())
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        session = postSnapshot.getValue(Session::class.java)

                        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
                        var date1 = LocalDate.parse(session!!.date, formatter).toString()
                        val date2 = java.sql.Date.valueOf(date1)
                        var profit = session!!.cashOutAmount - session!!.buyInAmount
                        //Log.i("date is ", session!!.date.toString())
                        Log.i("date is ", date2.toString())

                        series.appendData(DataPoint(date2 as Date, profit.toDouble()), true, 500)
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

        graphView.addSeries(series)

    }
}
