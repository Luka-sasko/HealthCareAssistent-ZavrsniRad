package com.example.healthcareassistent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MyAdapter(private val bpList:ArrayList<BloodPresureData>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
       val itemView=LayoutInflater.from(p0.context).inflate(R.layout.list_item,p0,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        var currentItem=bpList[p1]
        p0.tvDatum.text=currentItem.Datum
        p0.tvValue.text=currentItem.Value

    }

    override fun getItemCount(): Int {
        return bpList.size
    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvDatum=itemView.findViewById<TextView>(R.id.tvDatum)
        val tvValue=itemView.findViewById<TextView>(R.id.tvValue)

    }
}