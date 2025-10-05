package com.example.todolist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.domain.MeetingData

class MeetingAdapter(
    private val onClick: (MeetingData) -> Unit
) : RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    private val meetings: MutableList<MeetingData> = mutableListOf()

    inner class MeetingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMeeting: TextView = view.findViewById(R.id.tvMeeting)
        val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
        val tvAttendees: TextView = view.findViewById(R.id.tvAttendees)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meeting, parent, false)
        return MeetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        val meeting = meetings[position]
        holder.tvMeeting.text = "${meeting.title} (${meeting.date} @ ${meeting.time})"
        holder.tvPurpose.text = meeting.purpose ?: "No purpose specified"
        holder.tvAttendees.text = meeting.attendees ?: "No attendees specified"
        holder.itemView.setOnClickListener { onClick(meeting) }
    }

    override fun getItemCount(): Int = meetings.size

    fun submitList(newMeetings: List<MeetingData>) {
        meetings.clear()
        meetings.addAll(newMeetings)
        notifyDataSetChanged()
    }
}