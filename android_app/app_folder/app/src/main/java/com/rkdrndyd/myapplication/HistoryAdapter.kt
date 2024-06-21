// HistoryAdapter.kt
package com.rkdrndyd.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val logList: List<String>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(logList[position])
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dataTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val locationTextView: TextView = itemView.findViewById(R.id.lcationTextView)
        private val pointTextView: TextView = itemView.findViewById(R.id.pointTextView)
        private val crackTextView: TextView = itemView.findViewById(R.id.crackTextView)

        fun bind(item: String) {
            val parts = item.split("//")
            if (parts.size == 3) {
                dataTextView.text = parts[0]
                locationTextView.text = parts[1]
                val point = "+${parts[2]}"
                pointTextView.text = point
            } else {
                crackTextView.visibility = View.VISIBLE
                dataTextView.text = ""
                locationTextView.text = ""
                pointTextView.text = "" // 안보이게 만들면 다른 것까지 안보여서 안됨(아마 안보일거임)
            }
        }
    }
}
