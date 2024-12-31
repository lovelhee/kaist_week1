package com.example.madcampweek1.help

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcampweek1.R

class HelpAdapter(private val helpList: List<Help>) : RecyclerView.Adapter<HelpAdapter.HelpViewHolder>() {

    class HelpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPeople: ImageView = itemView.findViewById(R.id.ivPeople)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvReplyPercent: TextView = itemView.findViewById(R.id.tvReplyPercent)
        val tvTalkPercent: TextView = itemView.findViewById(R.id.tvTalkPercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_people, parent, false)
        return HelpViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        val help = helpList[position]
        holder.tvCategory.text = "#${help.tag}"
        holder.tvName.text = help.name
        holder.tvContent.text = help.content
        holder.tvReplyPercent.text = help.talkRate
        holder.tvTalkPercent.text = help.talkCount

        Glide.with(holder.itemView.context)
            .load(help.imageUrl)
            .placeholder(R.drawable.default_contact_image)
            .centerCrop()
            .into(holder.ivPeople)
    }

    override fun getItemCount(): Int = helpList.size
}