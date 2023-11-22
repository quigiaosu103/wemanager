package com.example.wemanager

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(var mList: ArrayList<Student>): RecyclerView.Adapter<StudentAdapter.ViewHolder>() {
   public var isSelecting = false
    public var removeIdList = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.student_short_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtName.text = item.FullName
        holder.txtId.text = item.Id
        holder.txtDeparment.text = item.Deparment
        holder.itemView.alpha = 1.0.toFloat()
        holder.itemView.setOnClickListener{
            v->
            if(isSelecting == true) {
                if(v.alpha.toString() == "0.6") {
                    v.alpha = 1.0.toFloat()
                    removeIdList.remove(item.Id)
                }else {
                    v.alpha = 0.6.toFloat()
                    removeIdList.add(item.Id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun removeItem(index: Int) {
        mList.removeAt(index)
        notifyDataSetChanged()
    }

    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }


    fun removeSelected() {
        mList = ArrayList(mList.filterIndexed{
                index, itemModel->
                !removeIdList.contains(itemModel.Id)
        })
        removeIdList.clear()
        notifyDataSetChanged()
    }





    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtId: TextView = itemView.findViewById(R.id.txtId)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var txtDeparment: TextView = itemView.findViewById(R.id.txtDeparment)
    }

}