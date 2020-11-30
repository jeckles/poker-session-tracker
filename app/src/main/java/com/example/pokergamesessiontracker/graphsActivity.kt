package com.example.pokergamesessiontracker

import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*


class graphsActivity : AppCompatActivity() {
    internal lateinit var sessions: ArrayList<Session>

    override fun onCreate(Bundle: savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view
        setBarChart()
        setLineChart()
        }

    fun barGraph(){
        val barGraph = HashMap<String, Int>()
        val entries = ArrayList<BarEntry>()

        var profitCumulative = 0
        for (session in sessions){
            var profit = session.cashOutAmount - session.buyInAmount
            profitCumulative += profit


            var game = session.smallBlindEntry.toString() + '/' + session.bigBlindEntry.toString()


            if (game in barGraph){
                barGraph[game] += profitCumulative
            }else{
                barGraph[game] = profitCumulative
            }
        }
        val labels = ArrayList<String>()
        for (key in barGraph.keys){
            entries.add(BarEntry(key, barGraph[key]))
            labels.add(key)
        }
        val barDataSet = BarDataSet(entries, "Cells")
        val data = BarData(labels, barDataSet)
        barChart.data = data

        barChart.setDescription("Profits by Game type")






    }

    fun lineGraph(){
        val lineGraph = HashMap<String, Int>()
        val lineEntries = ArrayList<Entry>()
        for (session in sessions){
            var profit = session.cashOutAmount - session.buyInAmount

            var date = session.sessionDate
            lineGraph[date] = profit


        }
        for (key in lineGraph.keys){
            lineEntries.add(Entry(key, lineGraph[key]))
        }
        val lineDataSet = LineDataSet(lineEntries, "Profit over time")
        val lineData = LineData(lineDataSet)
        lineChart.setData(lineData)

    }


}


