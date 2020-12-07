package com.example.pokergamesessiontracker

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// This class helps organize a list of sessions to make it easier to keep track of and display in the home page

class SessionList(private val context: Activity, private var sessions: List<Session>) : ArrayAdapter<Session>(context,
    R.layout.layout_session_list, sessions) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_session_list, null, true)

        val textViewName = listViewItem.findViewById<View>(R.id.textViewName) as TextView
        val textViewCountry = listViewItem.findViewById<View>(R.id.textViewCountry) as TextView

        val session = sessions[position]

        textViewName.text = session.sessionId
        textViewCountry.text = session.location

        return listViewItem
    }
}
