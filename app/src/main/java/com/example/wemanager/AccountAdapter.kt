package com.example.wemanager


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

class AccountAdapter(val mList: ArrayList<Account>, val context: Context): RecyclerView.Adapter<AccountAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.student_short_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        holder.txtName.text = item.FullName
        holder.txtId.text = item.UserName
        holder.txtDeparment.text = item.Role
        var imageHandler = ImageHandler()
        imageHandler.getImage(item.Image, holder.icon)
        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, item.UserName)
            true
        }

        holder.itemView.setOnClickListener{
            v->
            var intent = Intent(context, AccountView::class.java)
            intent.putExtra("username", item.UserName)
            context.startActivity(intent)
        }
    }

    private fun showPopupMenu(view: View, username: String) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.popup_menu) // Inflate your menu resource

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popupBtnDelete -> {
                    confirmDelete(username)
                    true
                }
                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun removeItem(index: Int) {
        mList.removeAt(index)
        notifyDataSetChanged()
    }

    fun addItem(item: Account){
        mList.add(item)
        notifyDataSetChanged()
    }

    fun confirmDelete(username: String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Confirm Delete Students")
            .setTitle("Alert")
            .setPositiveButton("DELETE") {
                    dialog, which ->
                    var dataHandler = DataHandler()
                    dataHandler.removeAccount(username)
            }
            .setNegativeButton("CANCLE") {
                    dialog, which ->
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                v->
            dialog.cancel()
        }
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var txtId: TextView = itemView.findViewById(R.id.txtId)
        var txtName: TextView = itemView.findViewById(R.id.txtName)
        var txtDeparment: TextView = itemView.findViewById(R.id.txtDeparment)
        var icon: ImageView = itemView.findViewById(R.id.icon)
    }

}