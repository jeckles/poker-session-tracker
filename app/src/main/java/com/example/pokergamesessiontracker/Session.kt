package com.example.pokergamesessiontracker

data class Session (val sessionId: String ="", val date: String = "", val location: String = "", val smallBlind: Int = 0, val bigBlind: Int = 0, val buyInAmount: Int = 0, val cashOutAmount: Int = 0, val hoursPlayed: Int = 0)