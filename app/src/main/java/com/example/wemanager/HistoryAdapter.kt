package com.example.wemanager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView




    class HistoryAdapter(var mList: ArrayList<String>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
            val item = mList[position]
            holder.txtContent.text = item
        }

        override public fun getItemCount(): Int {
            return mList.size
        }
     class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var txtContent: TextView = itemView.findViewById(R.id.txtDate)
        }

    }
