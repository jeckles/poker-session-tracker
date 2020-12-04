package com.example.pokergamesessiontracker


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

class graphsActivity : AppCompatActivity() {
    internal lateinit var sessions: ArrayList<Session>

    override fun onCreate(Bundle: savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view

        }

    fun barGraph(){
        val barGraph = HashMap<String, Int>()
        val entries = ArrayList<BarEntry>()
        var graphview : GraphView = findViewById(R.id.graphview) as GraphView
        series = BarGraphSeries<DataPoint>()


        for (session in sessions){
            var profit = session.cashOutAmount - session.buyInAmount



            var game = session.smallBlindEntry.toString() + '/' + session.bigBlindEntry.toString()


            if (game in barGraph){
                barGraph[game] += profit
            }else{
                barGraph[game] = profit
            }
        }

        for (key in barGraph.keys){
            series.appendData(DataPoint(key, barGraph[key]))
        }
        series.setSpacing(50)
        series.setAnimated(true)
        graphview.addSeries(series)

    }

    fun lineGraph(){
        val lineGraph = HashMap<String, Int>()
        var graphview : GraphView = findViewById(R.id.graphview) as GraphView
        var profitCumulative = 0
        for (session in sessions){
            var profit = session.cashOutAmount - session.buyInAmount
            profitCumulative += profit

            var date = session.sessionDate
            lineGraph[date] = profitCumulative

        }
        series = LineGraphSeries<DataPoint>()
        for (key in lineGraph.keys){
            series.appendData(DataPoint(key, lineGraph[key]))
        }
        series.setSpacing(50)
        series.setAnimated(true)
        graphview.addSeries(series)

    }


}


