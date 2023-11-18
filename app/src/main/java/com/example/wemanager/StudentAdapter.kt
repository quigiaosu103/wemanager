package com.example.wemanager

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(val mList: ArrayList<Student>): RecyclerView.Adapter<StudentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.student_short_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtName.text = item.FullName
        holder.txtId.text = item.Id
        holder.txtDeparment.text = item.Deparment
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun removeItem(index: Int) {
        mList.removeAt(index)
        notifyDataSetChanged()
    }

    fun addItem(item: Student){
        mList.add(item)
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtId: TextView = itemView.findViewById(R.id.txtId)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var txtDeparment: TextView = itemView.findViewById(R.id.txtDeparment)
    }

}