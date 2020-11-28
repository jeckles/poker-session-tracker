package com.example.pokergamesessiontracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlin.collections.ArrayList

class SessionView : AppCompatActivity() {

    private lateinit var datePlayed: TextView
    private lateinit var locationPlayed: TextView
    private lateinit var gameTypePlayed: TextView
    private lateinit var hoursPlayed: TextView
    private lateinit var smallBlindAmount: TextView
    private lateinit var bigBlindAmount: TextView
    private lateinit var buyInAmount: TextView
    private lateinit var cashOutAmount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_view)

        datePlayed = findViewById<TextView>(R.id.datePlayed)
        locationPlayed = findViewById<TextView>(R.id.locationPlayed)
        gameTypePlayed = findViewById<TextView>(R.id.gameTypePlayed)
        hoursPlayed = findViewById<TextView>(R.id.hoursPlayed)
        smallBlindAmount = findViewById<TextView>(R.id.smallBlindAmount)
        bigBlindAmount = findViewById<TextView>(R.id.bigBlindAmount)
        buyInAmount = findViewById<TextView>(R.id.buyInAmount)
        cashOutAmount = findViewById<TextView>(R.id.cashOutAmount)

        val bundle = intent.extras
        val date = bundle?.get("date").toString()
        val location = bundle?.get("location").toString()
        val gameType = bundle?.get("gameType").toString()
        val hours = bundle?.get("hoursPlayed").toString()
        val smallBlind = bundle?.get("smallBlind").toString()
        val bigBlind = bundle?.get("bigBlind").toString()
        val buyIn = bundle?.get("buyInAmount").toString()
        val cashOut = bundle?.get("cashOutAmount").toString()

        datePlayed.text = date
        locationPlayed.text = location
        gameTypePlayed.text = gameType
        hoursPlayed.text = hours
        smallBlindAmount.text = smallBlind
        bigBlindAmount.text = bigBlind
        buyInAmount.text = buyIn
        cashOutAmount.text = cashOut
    }
}