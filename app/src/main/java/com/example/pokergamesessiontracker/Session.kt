package com.example.pokergamesessiontracker

import android.os.Parcel
import android.os.Parcelable

class Session(val sessionId: String?, val date: String?, val gameType: String?, val location: String?, val smallBlind: Int, val bigBlind: Int, val buyInAmount: Int, val cashOutAmount: Int, val hoursPlayed: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        sessionId = parcel.readString(),
        date = parcel.readString(),
        gameType = parcel.readString(),
        location = parcel.readString(),
        smallBlind = parcel.readInt(),
        bigBlind = parcel.readInt(),
        buyInAmount = parcel.readInt(),
        cashOutAmount = parcel.readInt(),
        hoursPlayed = parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(sessionId)
        dest?.writeString(date)
        dest?.writeString(gameType)
        dest?.writeString(location)
        dest?.writeInt(smallBlind)
        dest?.writeInt(bigBlind)
        dest?.writeInt(buyInAmount)
        dest?.writeInt(cashOutAmount)
        dest?.writeInt(hoursPlayed)
    }

    companion object CREATOR : Parcelable.Creator<Session> {
        override fun createFromParcel(parcel: Parcel): Session {
            return Session(parcel)
        }

        override fun newArray(size: Int): Array<Session?> {
            return arrayOfNulls(size)
        }
    }
}