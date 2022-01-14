package com.example.renjana

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ConsultantAdapter(val context: Context, val consultantList: ArrayList<Consultant>) : RecyclerView.Adapter<ConsultantAdapter.ConsultantViewHolder>(){



    class ConsultantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val idConsultant = itemView.findViewById<TextView>(R.id.rvConsultantId)
        val imageView = itemView.findViewById<ImageView>(R.id.rvConsultantImage)
        val consultantName = itemView.findViewById<TextView>(R.id.rvConsultantName)
        val consultantTitle = itemView.findViewById<TextView>(R.id.rvConsultantTitle)
        val consultantDesc = itemView.findViewById<TextView>(R.id.rvConsultantDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.rv_consultant, parent, false)
        return ConsultantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultantViewHolder, position: Int) {
        val currentConsultant = consultantList[position]
        holder.idConsultant.text = currentConsultant.id
        holder.consultantName.text = currentConsultant.name
        holder.consultantTitle.text = currentConsultant.title
        holder.consultantDesc.text = currentConsultant.description
        Picasso.get()
            .load(currentConsultant.image)
            .error(R.drawable.ic_person)
            .placeholder(R.drawable.ic_person)
            .into(holder.imageView)


    }

    override fun getItemCount(): Int {
        return consultantList.size
    }


}