package com.example.wemanager


import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class CertificateAdapter(val mList: ArrayList<Certificate>): RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.certificate_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertificateAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtName.setText(item.Name)
        holder.txtContent.setText(item.Content)
        holder.txtDate.setText(item.Date)
        holder.txtSigner.setText(item.Signer)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun removeItem(index: Int) {
        mList.removeAt(index)
        notifyDataSetChanged()
    }

    fun addItem(item: Certificate){
        mList.add(item)
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtName: EditText = itemView.findViewById(R.id.txtName)
        var txtContent: EditText = itemView.findViewById(R.id.txtContent)
        var txtDate: EditText = itemView.findViewById(R.id.txtDate)
        var txtSigner: EditText = itemView.findViewById(R.id.txtSigner)
    }

}