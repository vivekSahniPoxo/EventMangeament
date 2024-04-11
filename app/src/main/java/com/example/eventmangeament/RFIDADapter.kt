package com.example.eventmangeament

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmangeament.databinding.RfidNoBinding
import com.example.eventmangeament.userinfo.Rfid

class RFIDADapter( private val mList: ArrayList<Rfid>) : RecyclerView.Adapter<RFIDADapter.ViewHOlder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHOlder {
        val itemBinding = RfidNoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHOlder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHOlder, position: Int) {
        val items = mList[position]
        holder.bind(items)
    }

    override fun getItemCount(): Int = mList.size

    class ViewHOlder(private val itemBinding: RfidNoBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(items: Rfid) {
            itemBinding.tvRfidNo.text = items.rfid
        }
    }
}