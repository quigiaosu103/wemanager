package com.example.wemanager


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CertificateAdapter(var mList: ArrayList<Certificate>, val context: Context): RecyclerView.Adapter<CertificateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.certificate_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertificateAdapter.ViewHolder, position: Int) {
        var item = mList[position]
        holder.txtName.setText(item.Name)
        holder.txtContent.setText(item.Content)
        holder.txtDate.setText(item.Date)
        holder.txtSigner.setText(item.Signer)
        val sharedPref =  context.getSharedPreferences("storage_account", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "null")

        if(item.Name == "") {
            enableEdit(holder)
        }else{
            disableEdit(holder)
        }

        holder.btnEdit.setOnClickListener{
            v->
            enableEdit(holder)
        }

        holder.btnSave.setOnClickListener{
            v->
            var studentId = item.studentId
            if(studentId != "") {
                var col = Firebase.firestore.collection("students")
                col.document(studentId!!).get()
                    .addOnSuccessListener {
                            document->
                        var student: MutableMap<String, Any>? = document.data
                        var certificatesList = student?.get("certificates") as ArrayList<HashMap<String, String>>
                        var newL = ArrayList<Certificate>()
                        for (ct in certificatesList) {
                            if(ct.get("id") != item.Id) {
                                newL.add(Certificate(Id = ct.get("id")!!, Name = ct.get("name")!!, Content = ct.get("content")!!, Date = ct.get("date")!!, Signer = ct.get("signer")!!, studentId = ct.get("studentId")!!))
                            }
                        }
                        newL.add(Certificate(Id = item.Id, Name = holder.txtName.text.toString(), Content = holder.txtContent.text.toString(), Date = holder.txtDate.text.toString(), Signer = holder.txtSigner.text.toString(), studentId = studentId.toString()))
                        col.document(studentId!!).update("certificates", newL)
                    }
                disableEdit(holder)
            }

        }

        holder.btnRemove.setOnClickListener{
            v->
            confirmDelete(item)
        }

        if(role == "Employee") {
            holder.btnEdit.isVisible = false
            holder.btnRemove.isVisible = false
        }
    }


    public fun createEmptyCreadit(username: String) {
        mList.add(0, Certificate("", "", "", "","${username}${mList.size}", studentId = username))
        notifyDataSetChanged()

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

    fun enableEdit(holder: CertificateAdapter.ViewHolder) {
        holder.txtName.isEnabled = true
        holder.txtContent.isEnabled = true
        holder.txtDate.isEnabled = true
        holder.txtSigner.isEnabled = true
        holder.btnEdit.isVisible = false
        holder.btnSave.isVisible = true
    }

    fun disableEdit(holder: CertificateAdapter.ViewHolder) {
        holder.txtName.isEnabled = false
        holder.txtContent.isEnabled = false
        holder.txtDate.isEnabled = false
        holder.txtSigner.isEnabled = false
        holder.btnEdit.isVisible = true
        holder.btnSave.isVisible = false
    }

    fun handleRemove(item:Certificate) {
        var studentId = item.studentId
        if(studentId != "") {
            var col = Firebase.firestore.collection("students")
            col.document(studentId!!).get()
                .addOnSuccessListener {
                        document->
                    var student: MutableMap<String, Any>? = document.data
                    var certificatesList = student?.get("certificates") as ArrayList<HashMap<String, String>>
                    var newL = ArrayList<Certificate>()
                    for (ct in certificatesList) {
                        if(ct.get("id") != item.Id) {
                            newL.add(Certificate(Id = ct.get("id")!!, Name = ct.get("name")!!, Content = ct.get("content")!!, Date = ct.get("date")!!, Signer = ct.get("signer")!!, studentId = ct.get("studentId")!!))
                        }
                    }
                    mList = ArrayList(mList.filter { it.Id!=item.Id })
                    notifyDataSetChanged()
                    col.document(studentId!!).update("certificates", newL)
                }
        }
    }

    fun confirmDelete(item: Certificate) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Delete Students Certificate '${item.Name}'")
            .setTitle("Alert")
            .setPositiveButton("DELETE") {
                    dialog, which ->
                    handleRemove(item)
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
        var txtName: EditText = itemView.findViewById(R.id.txtName)
        var txtContent: EditText = itemView.findViewById(R.id.txtContent)
        var txtDate: EditText = itemView.findViewById(R.id.txtDate)
        var txtSigner: EditText = itemView.findViewById(R.id.txtSigner)
        var btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        var btnSave: ImageButton = itemView.findViewById(R.id.btnSave)
        var btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

}